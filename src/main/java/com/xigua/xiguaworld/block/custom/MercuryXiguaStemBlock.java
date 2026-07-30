package com.xigua.xiguaworld.block.custom;

import com.mojang.serialization.MapCodec;
import com.xigua.xiguaworld.block.ModBlocks;
import com.xigua.xiguaworld.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * 水星西瓜藤蔓方块 - 类似原版的StemBlock（西瓜/南瓜藤蔓）
 * 
 * 功能说明：
 * - 继承自CropBlock，实现作物生长逻辑
 * - 实现BonemealableBlock接口，支持骨粉催熟
 * - 生长到成熟阶段后，会在相邻位置随机生成水星西瓜果实
 * 
 * 工作流程（类似原版西瓜/南瓜）：
 * 1. 玩家使用种子右键耕地 → 放置此方块（年龄0）
 * 2. 随机刻更新 → 年龄逐渐增长（0-7）
 * 3. 年龄达到7（成熟）→ 每次随机刻有小概率在相邻空位生成果实方块
 * 4. 果实被破坏 → 掉落水星西瓜物品
 * 5. 藤蔓保持成熟状态，可以继续生成果实
 * 
 * 原版机制说明：
 * - 原版西瓜/南瓜藤蔓成熟后，每次随机刻有约1/30的概率尝试生成果实
 * - 果实生成在藤蔓相邻的4个水平方向之一
 * - 果实下方必须是耕地或泥土类方块
 * - 藤蔓不会因果实生成而消失，可以无限次生成果实
 * 
 * 引用关系：
 * - CropBlock: 父类，提供作物生长基础功能
 *   - randomTick(): 随机刻更新，控制作物生长
 *     功能：每次随机刻触发时调用，用于增加作物年龄
 *   - getAge(): 获取当前年龄
 *     功能：从方块状态中读取AGE属性值
 *   - getMaxAge(): 获取最大年龄
 *     功能：返回作物生长的最大阶段值（7）
 *   - isMaxAge(): 判断是否成熟
 *     功能：检查当前年龄是否 >= 最大年龄
 *   - getAgeProperty(): 获取年龄属性
 *     功能：返回用于存储年龄的IntegerProperty
 *   - getStateForAge(): 根据年龄获取方块状态
 *     功能：创建指定年龄的BlockState
 *   - growCrops(): 骨粉催熟逻辑
 *     功能：使用骨粉时直接增加作物年龄
 *   - getGrowthSpeed(): 计算生长速度
 *     功能：根据周围耕地情况计算生长速度因子
 *   - getBonemealAgeIncrease(): 获取骨粉增加的年龄值
 *     功能：返回使用骨粉时增加的年龄范围（2-5）
 * - BonemealableBlock: 接口，使方块支持骨粉
 *   - isValidBonemealTarget(): 判断是否可以作为骨粉目标
 *     功能：未成熟时返回true
 *   - isBonemealSuccess(): 判断骨粉使用是否成功
 *     功能：总是返回true
 *   - performBonemeal(): 执行骨粉效果
 *     功能：调用growCrops增加年龄
 * - FarmBlock: 耕地方块，藤蔓只能种植在耕地上
 * - Block: 所有方块的基类
 *   - defaultBlockState(): 获取默认方块状态
 *     功能：返回方块的初始状态（年龄0）
 *   - canSurvive(): 判断方块是否能在此位置存活
 *     功能：检查下方是否为耕地且光照充足
 */
public class MercuryXiguaStemBlock extends CropBlock implements BonemealableBlock {
    
    /**
     * MapCodec用于序列化/反序列化，网络同步和数据保存时使用
     * simpleCodec: 简单编解码器，使用单参数构造函数
     * 注意：codec创建的实例fruitBlockSupplier为null，通过getFruitBlock()从ModBlocks获取
     */
    public static final MapCodec<MercuryXiguaStemBlock> CODEC = simpleCodec(MercuryXiguaStemBlock::new);
    
    /**
     * 年龄属性，0-7共8个生长阶段
     * BlockStateProperties.AGE_7: 预定义的0-7整数属性
     *   - 用于存储作物的生长阶段
     *   - 0表示刚种植，7表示完全成熟
     */
    public static final IntegerProperty AGE = BlockStateProperties.AGE_7;
    
    /**
     * 每个年龄阶段对应的碰撞箱形状
     * 注意：使用很小的碰撞箱，类似原版西瓜/南瓜藤蔓
     * Block.box: 创建边界框（AxisAlignedBB）
     *   - 参数: x1, y1, z1, x2, y2, z2（坐标范围0-16，单位像素）
     * 碰撞箱很小（2x16x2像素），只覆盖中心区域
     */
    private static final VoxelShape[] SHAPE_BY_AGE = new VoxelShape[]{
            Block.box(7.0, 0.0, 7.0, 9.0, 16.0, 9.0),  // 年龄0: 2x16x2像素
            Block.box(7.0, 0.0, 7.0, 9.0, 16.0, 9.0),  // 年龄1: 2x16x2像素
            Block.box(7.0, 0.0, 7.0, 9.0, 16.0, 9.0),  // 年龄2: 2x16x2像素
            Block.box(7.0, 0.0, 7.0, 9.0, 16.0, 9.0),  // 年龄3: 2x16x2像素
            Block.box(7.0, 0.0, 7.0, 9.0, 16.0, 9.0),  // 年龄4: 2x16x2像素
            Block.box(7.0, 0.0, 7.0, 9.0, 16.0, 9.0),  // 年龄5: 2x16x2像素
            Block.box(7.0, 0.0, 7.0, 9.0, 16.0, 9.0),  // 年龄6: 2x16x2像素
            Block.box(7.0, 0.0, 7.0, 9.0, 16.0, 9.0)   // 年龄7: 2x16x2像素
    };
    
    /**
     * 果实方块引用，用于在成熟时生成果实
     * 使用Supplier延迟加载，避免注册时的空指针异常
     * Supplier<? extends Block>: Java函数式接口，延迟获取Block或其子类的实例
     *   - get(): 获取实际的方块实例（只能在注册完成后调用）
     *   - ? extends Block: 通配符，允许接收Block的任何子类
     */
    private final java.util.function.Supplier<? extends Block> fruitBlockSupplier;
    
    /**
     * 构造函数（供codec使用）
     * 注意：这个构造函数仅供序列化/反序列化使用，fruitBlockSupplier为null
     * 实际使用时，getFruitBlock()会从ModBlocks.MERCURY_XIGUA_FRUIT获取
     * 
     * @param properties 方块属性（BlockBehaviour.Properties）
     *                   - 定义方块的物理特性：硬度、爆炸抗性、声音类型等
     */
    public MercuryXiguaStemBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.fruitBlockSupplier = null;
        this.registerDefaultState(this.stateDefinition.any().setValue(AGE, Integer.valueOf(0)));
    }
    
    /**
     * 构造函数（供注册使用）
     * 
     * @param properties 方块属性（BlockBehaviour.Properties）
     *                   - 定义方块的物理特性：硬度、爆炸抗性、声音类型等
     * @param fruitBlockSupplier 果实方块的延迟加载引用
     *                           - 使用Supplier避免注册时的循环依赖
     *                           - 可以接收DeferredBlock或任何Supplier<Block>
     *                           - DeferredBlock<T>实现了Supplier<T>接口
     */
    public MercuryXiguaStemBlock(BlockBehaviour.Properties properties, java.util.function.Supplier<? extends Block> fruitBlockSupplier) {
        super(properties);
        // 保存果实方块的延迟加载引用，不立即获取实例
        this.fruitBlockSupplier = fruitBlockSupplier;
        // 设置默认状态：年龄为0（刚种植）
        this.registerDefaultState(this.stateDefinition.any().setValue(AGE, Integer.valueOf(0)));
    }
    
    /**
     * 获取果实方块实例
     * 延迟加载，只在需要时调用get()获取实际实例
     * 
     * @return Block 果实方块实例
     */
    private Block getFruitBlock() {
        // 如果fruitBlockSupplier为null（codec创建的实例），则从ModBlocks获取
        if (fruitBlockSupplier == null) {
            return ModBlocks.MERCURY_XIGUA_FRUIT.get();
        }
        return fruitBlockSupplier.get();
    }
    
    /**
     * 返回序列化编解码器
     * 用于网络同步、保存/加载数据时使用
     * 
     * @return MapCodec<? extends CropBlock> 编解码器实例
     */
    @Override
    public MapCodec<? extends CropBlock> codec() {
        return CODEC;
    }
    
    /**
     * 获取方块的碰撞箱形状
     * 根据当前年龄返回对应的形状
     * 
     * @param state 当前方块状态（BlockState）
     *              - 包含方块的所有属性信息
     * @param level BlockGetter（方块读取器）
     *              - 用于查询世界中其他方块的状态
     * @param pos 方块位置（BlockPos）
     *            - 包含x, y, z坐标
     * @param context CollisionContext（碰撞上下文）
     *                - 包含碰撞检测的额外信息
     * @return VoxelShape 碰撞箱形状
     *         - SHAPE_BY_AGE[age]: 根据年龄索引对应的碰撞箱
     */
    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE_BY_AGE[this.getAge(state)];
    }
    
    /**
     * 检查方块是否可以放置在目标方块上
     * 藤蔓只能种植在耕地（FarmBlock）上
     * 
     * @param state 目标方块的状态（BlockState）
     *              - 下方方块的完整状态信息
     * @param level BlockGetter（方块读取器）
     *              - 用于查询世界中其他方块的状态
     * @param pos 目标方块位置（BlockPos）
     *            - 下方方块的坐标
     * @return boolean 是否可以放置
     *         - true: 下方是耕地，可以放置
     *         - false: 下方不是耕地，不能放置
     */
    @Override
    protected boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos) {
        // instanceof: Java关键字，检查对象是否为指定类型的实例
        // FarmBlock: 耕地方块类，作物只能种植在耕地上
        return state.getBlock() instanceof FarmBlock;
    }
    
    /**
     * 获取年龄属性
     * 重写父类方法，返回我们自定义的AGE属性
     * 
     * @return IntegerProperty 年龄属性
     *         - BlockStateProperties.AGE_7: 0-7的整数属性
     */
    @Override
    protected IntegerProperty getAgeProperty() {
        return AGE;
    }
    
    /**
     * 获取最大年龄
     * 原版西瓜/南瓜藤蔓使用7，这里也使用7
     * 
     * @return int 最大年龄值（7）
     */
    @Override
    public int getMaxAge() {
        return 7;
    }
    
    /**
     * 创建方块状态定义
     * 将AGE属性注册到方块状态中
     * 
     * @param builder StateDefinition.Builder<Block, BlockState>
     *                - 用于添加方块状态的属性
     *                - builder.add(): 添加一个属性到状态定义中
     */
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(AGE);
    }
    
    /**
     * 判断是否需要随机刻更新
     * 重写父类方法，使成熟的藤蔓继续接收随机刻以生成果实
     * 
     * 原版CropBlock的行为：
     * - 未成熟时返回true（需要随机刻来生长）
     * - 成熟后返回false（不再需要随机刻）
     * 
     * 我们的行为（类似原版西瓜/南瓜藤蔓）：
     * - 始终返回true，因为成熟的藤蔓需要随机刻来生成果实
     * 
     * @param state 当前方块状态（BlockState）
     * @return boolean 总是true，始终需要随机刻
     */
    @Override
    protected boolean isRandomlyTicking(BlockState state) {
        // 始终返回true，确保成熟的藤蔓也能接收随机刻
        // 这样藤蔓成熟后才能继续生成果实
        return true;
    }
    
    /**
     * 判断方块是否能在此位置存活
     * 需要满足：
     * 1. 下方是耕地（通过mayPlaceOn检查）
     * 2. 光照充足（>= 8）
     * 
     * @param state 当前方块状态（BlockState）
     *              - 包含当前方块的所有属性
     * @param level LevelReader（只读的世界视图）
     *              - 用于查询周围方块状态和光照信息
     * @param pos 方块位置（BlockPos）
     *            - 当前方块的坐标
     * @return boolean 是否能存活
     *         - true: 满足存活条件
     *         - false: 不满足存活条件
     */
    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        // hasSufficientLight: 检查光照是否充足（>= 8）
        // super.canSurvive: 调用父类方法，检查mayPlaceOn条件
        return hasSufficientLight(level, pos) && super.canSurvive(state, level, pos);
    }
    
    /**
     * 静态方法：检查光照是否充足
     * 作物生长需要至少8级光照
     * 
     * @param level LevelReader（只读的世界视图）
     *              - 用于获取光照信息
     * @param pos 方块位置（BlockPos）
     *            - 要检查的方块坐标
     * @return boolean 光照是否 >= 8
     *         - true: 光照充足
     *         - false: 光照不足
     */
    public static boolean hasSufficientLight(LevelReader level, BlockPos pos) {
        // getRawBrightness: 获取原始光照强度（不含方块自身亮度）
        // 参数1: pos - 方块位置
        // 参数2: 0 - 最小亮度（忽略方块自身发光）
        return level.getRawBrightness(pos, 0) >= 8;
    }
    
    /**
     * 获取基础种子物品
     * 用于玩家破坏藤蔓时掉落种子
     * 
     * @return ItemLike 种子物品
     *         - ModItems.MERCURY_XIGUA_SEEDS.get(): 获取注册的水星瓜种
     */
    @Override
    protected ItemLike getBaseSeedId() {
        return ModItems.MERCURY_XIGUA_SEEDS.get();
    }
    
    /**
     * 随机刻更新 - 核心生长逻辑（类似原版西瓜/南瓜藤蔓）
     * 
     * 原版机制说明：
     * 1. 未成熟时（年龄 < 7）：
     *    - 检查光照 >= 9
     *    - 根据周围耕地情况计算生长速度
     *    - 有一定概率增加年龄
     * 
     * 2. 成熟后（年龄 = 7）：
     *    - 每次随机刻有小概率（约1/30）尝试在相邻位置生成果实
     *    - 果实生成在水平4个方向之一的空位上
     *    - 果实下方必须是耕地或泥土类方块
     *    - 藤蔓保持成熟状态，不会消失，可以继续生成果实
     * 
     * @param state 当前方块状态（BlockState）
     *              - 包含当前方块的年龄等信息
     * @param level ServerLevel（服务器端世界）
     *              - 提供完整的世界访问权限（可读可写）
     *              - 只有服务器端才能修改世界
     * @param pos 方块位置（BlockPos）
     *            - 当前藤蔓的坐标
     * @param random RandomSource（随机数生成器）
     *               - 用于生成随机数决定生长和果实生成
     */
    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        // 检查区块是否已加载，防止加载未加载的区块
        // isAreaLoaded: 检查指定区域是否已加载
        // 参数1: pos - 中心位置
        // 参数2: 1 - 检查范围（1个区块）
        if (!level.isAreaLoaded(pos, 1)) return;
        
        int i = this.getAge(state);
        
        // 如果未达到最大年龄，继续生长（类似原版小麦）
        if (i < this.getMaxAge()) {
            // 检查光照强度是否 >= 9（生长需要的光照）
            // getRawBrightness: 获取原始光照强度
            if (level.getRawBrightness(pos, 0) >= 9) {
                // 计算生长速度（基于周围耕地情况）
                // getGrowthSpeed: 继承自CropBlock，计算周围耕地对生长的影响
                // 生长速度受以下因素影响：
                // - 周围3x3区域的耕地数量
                // - 耕地是否湿润（湿润的耕地生长速度更快）
                // - 周围是否有相同作物（密集种植会减慢生长）
                float f = getGrowthSpeed(state, level, pos);
                
                // 根据生长速度计算生长概率
                // 生长速度越快，概率越高
                // random.nextInt((int)(25.0F / f) + 1) == 0 表示概率为 1/((25/f)+1)
                // 例如：f=1.0时，概率约为1/26；f=3.0时，概率约为1/9
                // NeoForge的钩子函数，允许其他mod干预生长逻辑
                if (net.neoforged.neoforge.common.CommonHooks.canCropGrow(level, pos, state, random.nextInt((int)(25.0F / f) + 1) == 0)) {
                    // 增加年龄
                    // setBlock: 更新方块状态
                    // 参数1: pos - 方块位置
                    // 参数2: getStateForAge(i + 1) - 新的方块状态（年龄+1）
                    // 参数3: 2 - 更新标志（2表示只通知客户端，不触发邻居更新）
                    level.setBlock(pos, this.getStateForAge(i + 1), 2);
                    // 触发作物生长后事件（NeoForge事件）
                    net.neoforged.neoforge.common.CommonHooks.fireCropGrowPost(level, pos, state);
                }
            }
        } else {
            // 已达到最大年龄（成熟），尝试生成果实（类似原版西瓜/南瓜）
            // 成熟藤蔓每次随机刻有小概率在相邻位置生成果实
            // 原版概率约为 1/30（random.nextInt(30) == 0）
            // 这里使用 1/30 的概率，与原版一致
            if (random.nextInt(30) == 0) {
                // 尝试在相邻4个水平方向生成果实
                // Direction.Plane.HORIZONTAL: 水平方向枚举（NORTH, SOUTH, WEST, EAST）
                for (Direction direction : Direction.Plane.HORIZONTAL) {
                    // pos.relative(direction): 获取藤蔓在指定方向上的相邻位置
                    BlockPos blockpos = pos.relative(direction);
                    // 获取相邻位置的方块状态
                    BlockState blockstate = level.getBlockState(blockpos);
                    
                    // 检查相邻位置是否可以放置果实：
                    // 1. 相邻位置必须是空气（空位）
                    // 2. 果实下方必须是耕地或泥土类方块
                    // isAir(): 检查方块是否为空气
                    // blockpos.below(): 获取相邻位置下方的坐标（y-1）
                    if (blockstate.isAir()) {
                        BlockPos belowPos = blockpos.below();
                        BlockState belowState = level.getBlockState(belowPos);
                        
                        // 检查果实下方是否是合适的方块
                        // 原版西瓜/南瓜可以在以下方块上生成果实：
                        // - FarmBlock（耕地）
                        // - DirtBlock（泥土）
                        // - GrassBlock（草方块）
                        // 这里使用原版标签 #minecraft:dirt 来检查泥土类方块
                        // BlockTags.DIRT: 包含泥土、草方块、灰化土等
                        if (belowState.getBlock() instanceof FarmBlock || 
                            belowState.is(net.minecraft.tags.BlockTags.DIRT)) {
                            // 放置果实方块
                            // getFruitBlock(): 延迟获取果实方块实例
                            // defaultBlockState(): 获取果实方块的默认状态
                            // setBlock: 在相邻位置放置果实
                            level.setBlock(blockpos, this.getFruitBlock().defaultBlockState(), 2);
                            // 只生成一个果实就退出循环
                            break;
                        }
                    }
                }
            }
        }
    }
    
    /**
     * 骨粉催熟逻辑
     * 使用骨粉时直接增加年龄
     * 
     * @param level Level（世界）
     *              - 提供完整的世界访问权限
     * @param pos 方块位置（BlockPos）
     *            - 当前藤蔓的坐标
     * @param state 当前方块状态（BlockState）
     *              - 包含当前方块的年龄等信息
     */
    @Override
    public void growCrops(Level level, BlockPos pos, BlockState state) {
        // 计算使用骨粉后的新年龄
        // getAge: 获取当前年龄
        // getBonemealAgeIncrease: 获取骨粉增加的年龄值（2-5）
        int i = this.getAge(state) + this.getBonemealAgeIncrease(level);
        int j = this.getMaxAge();
        
        // 确保不超过最大年龄
        // 如果计算后的年龄超过7，则设置为7
        if (i > j) {
            i = j;
        }
        
        // 更新方块状态
        // setBlock: 设置方块状态
        // 参数1: pos - 方块位置
        // 参数2: getStateForAge(i) - 新的方块状态
        // 参数3: 2 - 更新标志（只通知客户端）
        level.setBlock(pos, this.getStateForAge(i), 2);
    }
    
    /**
     * 获取骨粉增加的年龄值
     * 返回2-5之间的随机数（与原版一致）
     * 
     * @param level Level（世界）
     *              - 用于获取随机数生成器
     * @return int 增加的年龄值（2-5）
     */
    @Override
    protected int getBonemealAgeIncrease(Level level) {
        // Mth.nextInt: 生成指定范围内的随机整数
        // 参数1: random - 随机数生成器
        // 参数2: 2 - 最小值
        // 参数3: 5 - 最大值
        return net.minecraft.util.Mth.nextInt(level.random, 2, 5);
    }
    
    /**
     * 判断是否可以作为骨粉目标
     * 未成熟时可以，成熟后不可以
     * 
     * @param level LevelReader（只读的世界视图）
     *              - 用于查询方块状态
     * @param pos 方块位置（BlockPos）
     *            - 当前藤蔓的坐标
     * @param state 方块状态（BlockState）
     *              - 包含当前方块的年龄等信息
     * @return boolean 是否可以
     *         - true: 未成熟，可以使用骨粉
     *         - false: 已成熟，不能使用骨粉
     */
    @Override
    public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state) {
        // isMaxAge: 判断是否已达到最大年龄
        // !: 取反，未成熟时返回true
        return !this.isMaxAge(state);
    }
    
    /**
     * 判断骨粉使用是否成功
     * 总是返回true（骨粉总是有效）
     * 
     * @param level Level（世界）
     * @param random RandomSource（随机数生成器）
     * @param pos BlockPos（方块位置）
     * @param state BlockState（方块状态）
     * @return boolean 总是true
     */
    @Override
    public boolean isBonemealSuccess(Level level, RandomSource random, BlockPos pos, BlockState state) {
        return true;
    }
    
    /**
     * 执行骨粉效果
     * 调用growCrops方法增加年龄
     * 
     * @param level ServerLevel（服务器端世界）
     *              - 只有服务器端才能修改世界
     * @param random RandomSource（随机数生成器）
     * @param pos BlockPos（方块位置）
     * @param state BlockState（方块状态）
     */
    @Override
    public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state) {
        this.growCrops(level, pos, state);
    }
}