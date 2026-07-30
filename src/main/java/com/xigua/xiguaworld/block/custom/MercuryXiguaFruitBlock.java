package com.xigua.xiguaworld.block.custom;

import com.xigua.xiguaworld.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

/**
 * 水星西瓜果实方块 - 类似原版的MelonBlock（西瓜方块）
 * 
 * 功能说明：
 * - 完整的16x16x16方块，可以被玩家破坏
 * - 破坏时掉落水星西瓜物品（通过战利品表处理）
 * - 需要与藤蔓相连才能存活（可选）
 * 
 * 工作流程：
 * 1. 藤蔓成熟后在相邻位置放置此方块
 * 2. 玩家破坏果实方块 → 掉落水星西瓜物品
 * 3. 可以用工具快速破坏（类似西瓜）
 * 
 * 引用关系：
 * - Block: 所有方块的基类
 *   - defaultBlockState(): 获取默认方块状态
 *     功能：返回方块的默认状态，用于放置方块时初始化
 *   - canSurvive(): 判断方块是否能在此位置存活
 *     功能：检查方块放置的环境是否满足存活条件
 *   - neighborChanged(): 邻居方块更新时调用
 *     功能：当相邻方块发生变化时，检查当前方块是否还能存活
 * - BlockBehaviour.Properties: 方块属性构建器
 *   - strength(float hardness, float blastResistance): 设置硬度和爆炸抗性
 *     功能：hardness决定破坏时间，blastResistance决定抗爆能力
 *   - sound(SoundType sound): 设置声音类型
 *     功能：定义放置、破坏、行走时的音效
 *   - mapColor(MapColor color): 设置地图颜色
 *     功能：决定方块在地图上的显示颜色
 * - ItemStack: 物品堆，表示一组物品
 *   - new ItemStack(ItemLike item): 创建物品堆
 *     功能：根据物品创建一个数量为1的物品堆
 * - LevelReader: 只读的世界视图接口
 *   - getBlockState(BlockPos pos): 获取指定位置的方块状态
 *     功能：返回该位置的BlockState对象
 * - Level: 世界类，继承自LevelReader
 *   - destroyBlock(BlockPos pos, boolean dropBlock): 破坏方块
 *     功能：移除方块并可选择是否掉落物品
 * - BlockPos: 方块位置类
 *   - below(): 获取下方位置
 *     功能：返回pos.getY()-1的位置
 *   - relative(Direction direction): 获取相对位置
 *     功能：根据方向返回相邻位置
 * - Direction.Plane.HORIZONTAL: 水平方向枚举
 *   - 包含：NORTH, SOUTH, WEST, EAST
 *     功能：用于遍历四个水平方向
 */
public class MercuryXiguaFruitBlock extends Block {
    
    /**
     * 构造函数
     * 
     * @param properties 方块属性（BlockBehaviour.Properties）
     *                   - strength(1.0F, 0.5F): 硬度1.0，爆炸抗性0.5
     *                     硬度: 破坏方块所需时间，值越大越难破坏
     *                     爆炸抗性: 抵抗爆炸的能力，值越大越抗爆
     *                   - sound(SoundType.WOOD): 使用木质音效
     *                     包括放置、破坏、行走时的声音
     *                   - mapColor(MapColor.COLOR_GREEN): 地图颜色为绿色
     */
    public MercuryXiguaFruitBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }
    
    /**
     * 判断方块是否能在此位置存活
     * 果实方块需要下方是耕地或者与藤蔓相连
     * 
     * @param state 当前方块状态（BlockState）
     *              - 包含当前方块的所有属性
     * @param level LevelReader（只读世界视图）
     *              - 用于查询周围方块状态
     * @param pos 方块位置（BlockPos）
     *            - 当前方块的坐标
     * @return boolean 是否能存活
     *         - true: 满足存活条件
     *         - false: 不满足存活条件
     */
    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        // 检查下方是否是耕地
        // pos.below(): 获取pos下方一个单位的位置（y-1）
        BlockPos belowPos = pos.below();
        
        // level.getBlockState(pos): 获取指定位置的方块状态
        // .getBlock(): 从方块状态中获取方块实例
        BlockState belowState = level.getBlockState(belowPos);
        
        // instanceof: Java关键字，检查对象是否为指定类型的实例
        // FarmBlock: 耕地方块类，作物只能种植在耕地上
        if (belowState.getBlock() instanceof net.minecraft.world.level.block.FarmBlock) {
            return true;
        }
        
        // 检查相邻位置是否有藤蔓（模拟原版逻辑）
        // Direction.Plane.HORIZONTAL: 水平方向枚举（NORTH, SOUTH, WEST, EAST）
        for (net.minecraft.core.Direction direction : net.minecraft.core.Direction.Plane.HORIZONTAL) {
            // pos.relative(direction): 获取pos在指定方向上的相邻位置
            BlockPos neighborPos = pos.relative(direction);
            
            // 获取相邻位置的方块状态
            BlockState neighborState = level.getBlockState(neighborPos);
            
            // 如果相邻位置是藤蔓方块，可以存活
            // MercuryXiguaStemBlock: 水星西瓜藤蔓方块类
            if (neighborState.getBlock() instanceof MercuryXiguaStemBlock) {
                return true;
            }
        }
        
        // 默认情况下可以存活（简化处理）
        return true;
    }
    
    /**
     * 方块被邻居方块更新时调用
     * 用于检查方块是否还能存活
     * 
     * @param state 当前方块状态（BlockState）
     *              - 包含当前方块的所有属性
     * @param level Level（世界）
     *              - 提供完整的世界访问权限（可读可写）
     *              - 继承自LevelReader
     * @param pos 方块位置（BlockPos）
     *            - 当前方块的坐标
     * @param block 更新的邻居方块（Block）
     *              - 发生变化的邻居方块实例
     * @param fromPos 更新的方块位置（BlockPos）
     *                - 发生变化的方块坐标
     * @param isMoved 是否被活塞移动（boolean）
     *                - true: 被活塞推动
     *                - false: 其他原因
     */
    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoved) {
        // super.neighborChanged(): 调用父类的neighborChanged方法
        // 功能：执行基础的邻居更新逻辑
        super.neighborChanged(state, level, pos, block, fromPos, isMoved);
        
        // 检查方块是否还能存活
        // canSurvive(): 判断方块是否能在此位置存活
        if (!canSurvive(state, level, pos)) {
            // level.destroyBlock(pos, true): 破坏方块并掉落物品
            // 参数1: pos - 要破坏的方块位置
            // 参数2: true - 是否掉落物品
            // 功能：移除方块并根据战利品表生成掉落物
            level.destroyBlock(pos, true);
        }
    }
}