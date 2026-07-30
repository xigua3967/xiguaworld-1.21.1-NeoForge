package datagen;


import com.xigua.xiguaworld.block.ModBlocks;
import com.xigua.xiguaworld.xiguaworld;
import net.minecraft.core.Direction;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

/**
 * 方块状态和数据生成器
 * 用于生成所有方块的blockstates和models文件
 * 
 * BlockStateProvider: NeoForge提供的方块状态生成器基类
 *   - 用于自动生成方块状态文件(blockstates/*.json)
 *   - 用于自动生成方块模型文件(models/block/*.json)
 */
public class ModBlockStatesProvider extends BlockStateProvider {
    public ModBlockStatesProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, xiguaworld.MOD_ID, exFileHelper);
    }

    /**
     * 注册所有方块状态和模型
     * 在数据生成时调用，为每个方块生成对应的JSON文件
     */
    @Override
    protected void registerStatesAndModels() {
        // 神秘铁矿石方块 - 使用cubeAll模型（6面相同贴图）
        simpleBlockWithItem(ModBlocks.MYSTERIOUS_IRONSTONE.get(), cubeAll(ModBlocks.MYSTERIOUS_IRONSTONE.get()));
        
        // 神秘铁块 - 使用cubeAll模型（6面相同贴图）
        simpleBlockWithItem(ModBlocks.MYSTERIOUS_IRON_BLOCK.get(), cubeAll(ModBlocks.MYSTERIOUS_IRON_BLOCK.get()));
        
        // 水星西瓜果实方块 - 使用自定义的cube_bottom_top模型
        registerMercuryXiguaFruit();
        
        // 水星西瓜藤蔓方块 - 使用自定义的薄片交叉模型
        registerMercuryXiguaStem();
    }
    
    /**
     * 注册水星西瓜果实方块的方块状态和模型
     * 果实方块是一个完整的16x16x16方块，使用side和top两张贴图
     * 
     * simpleBlock(): 生成简单的方块状态文件
     *   - 参数: Block实例
     *   - 生成: blockstates/mercury_xigua_fruit.json
     * 
     * models().cubeBottomTop(): 生成底部-侧面-顶部模型
     *   - 参数1: 模型名称
     *   - 参数2: 侧面贴图路径
     *   - 参数3: 底部贴图路径
     *   - 参数4: 顶部贴图路径
     *   - 生成: models/block/mercury_xigua_fruit.json
     */
    private void registerMercuryXiguaFruit() {
        // 获取方块实例
        Block fruitBlock = ModBlocks.MERCURY_XIGUA_FRUIT.get();
        
        // 生成方块模型文件
        // models().cubeBottomTop(): 创建侧面+底部+顶部模型
        //   - 侧面: mercury_xigua_fruit_side.png
        //   - 底部: mercury_xigua_fruit_top.png（复用顶部贴图）
        //   - 顶部: mercury_xigua_fruit_top.png
        ModelFile fruitModel = models().cubeBottomTop(
                "mercury_xigua_fruit",
                modLoc("block/mercury_xigua_fruit_side"),    // 侧面贴图
                modLoc("block/mercury_xigua_fruit_top"),     // 底部贴图
                modLoc("block/mercury_xigua_fruit_top")      // 顶部贴图
        );
        
        // 生成方块状态文件
        // simpleBlock(block, model): 使用指定的模型创建方块状态
        //   - 生成: {"variants": {"": {"model": "xigua_world:block/mercury_xigua_fruit"}}}
        simpleBlock(fruitBlock, fruitModel);
        
        // 生成物品模型文件
        // itemModels().getBuilder(): 为方块创建对应的物品模型
        //   - 当玩家在背包中持有该方块时使用的模型
        itemModels().getBuilder("mercury_xigua_fruit")
                .parent(fruitModel);
    }
    
    /**
     * 注册水星西瓜藤蔓方块的方块状态和模型
     * 藤蔓方块有8个生长阶段（age=0到7），每个阶段使用不同的贴图
     * 使用薄片交叉模型（类似原版西瓜/南瓜藤蔓）
     * 
     * getVariantBuilder(): 创建带属性的方块状态生成器
     *   - 用于处理有多个属性变体的方块
     * 
     * partialState().with(): 指定特定属性值的状态
     *   - 参数: 属性名和属性值
     * 
     * setModels(): 为该状态设置对应的模型
     *   - 参数: ConfiguredModel数组
     * 
     * models().getBuilder(): 创建自定义模型
     *   - 用于创建非标准模型（如薄片交叉）
     * 
     * element(): 添加模型元素（几何体）
     *   - from(): 起始坐标(x, y, z)
     *   - to(): 结束坐标(x, y, z)
     *   - rotation(): 旋转设置
     *   - face(): 面设置
     */
    private void registerMercuryXiguaStem() {
        // 获取藤蔓方块实例
        Block stemBlock = ModBlocks.MERCURY_XIGUA_STEM.get();
        
        // 获取年龄属性
        // AGE: IntegerProperty类型，范围0-7
        // BlockStateProperties.AGE_7: 原版定义的年龄属性
        IntegerProperty age = BlockStateProperties.AGE_7;
        
        // 为每个阶段生成模型文件
        // 阶段0-1: stage0, 阶段2-3: stage1, 阶段4-5: stage2, 阶段6-7: stage3
        for (int i = 0; i <= 3; i++) {
            String stageName = "stage" + i;
            createStemModel(stageName);
        }
        
        // 创建方块状态生成器
        // getVariantBuilder(): 为带属性的方块创建状态生成器
        getVariantBuilder(stemBlock)
                .forAllStates(state -> {
                    // 获取当前状态的年龄值
                    // state.getValue(): 从方块状态中获取指定属性的值
                    int ageValue = state.getValue(age);
                    
                    // 根据年龄值选择对应的阶段贴图
                    // 年龄0-1: stage0, 年龄2-3: stage1, 年龄4-5: stage2, 年龄6-7: stage3
                    String stageName = getStemStageName(ageValue);
                    
                    // 获取对应的模型文件
                    ModelFile model = models().getExistingFile(modLoc("block/mercury_xigua_stem_" + stageName));
                    
                    // 返回配置好的模型
                    // ConfiguredModel: 配置好的模型实例
                    return ConfiguredModel.builder()
                            .modelFile(model)
                            .build();
                });
    }
    
    /**
     * 创建藤蔓方块的薄片交叉模型
     * 模型包含两个旋转45度的薄片，形成十字交叉效果
     * 使用自定义元素而非cross父模型，以避免黑边问题
     * 
     * @param stageName 阶段名称（stage0-stage3）
     */
    private void createStemModel(String stageName) {
        // 使用自定义元素创建薄片交叉模型
        // 不使用cross父模型，而是手动定义两个旋转的薄片
        models().getBuilder("mercury_xigua_stem_" + stageName)
                // renderType("cutout"): 设置渲染类型为cutout
                //   - 消除透明贴图黑边问题
                //   - cutout渲染类型会禁用纹理插值，使透明边缘更清晰
                .renderType("cutout")
                // texture(): 添加贴图变量
                //   - "particle": 粒子贴图（破坏方块时显示）
                //   - "stem": 藤蔓贴图
                .texture("particle", modLoc("block/mercury_xigua_stem_" + stageName))
                .texture("stem", modLoc("block/mercury_xigua_stem_" + stageName))
                // element(): 添加模型元素（第一个薄片）
                //   - from(0.9, 0, 8): 起始坐标
                //   - to(15.1, 16, 8): 结束坐标
                //   - 这是一个沿Z轴方向的薄片，宽度约1.6像素
                .element()
                    .from(0.9F, 0.0F, 8.0F)
                    .to(15.1F, 16.0F, 8.0F)
                    // rotation(): 设置旋转
                    //   - angle(45): 旋转45度
                    //   - axis(Direction.Axis.Y): 绕Y轴旋转
                    //   - origin(8, 8, 8): 旋转中心点
                    //   - rescale(true): 旋转后重新缩放
                    .rotation().angle(45.0F).axis(Direction.Axis.Y).origin(8.0F, 8.0F, 8.0F).rescale(true).end()
                    // shade(false): 禁用阴影
                    //   - 使贴图不受光照方向影响
                    .shade(false)
                    // face(): 设置面
                    //   - north/south: 北/南面
                    //   - uvs(0, 0, 16, 16): UV坐标（完整贴图）
                    //   - texture("#stem"): 使用stem贴图
                    .face(Direction.NORTH).uvs(0, 0, 16, 16).texture("#stem").end()
                    .face(Direction.SOUTH).uvs(0, 0, 16, 16).texture("#stem").end()
                .end()
                // element(): 添加第二个模型元素（交叉的薄片）
                //   - from(8, 0, 0.9): 起始坐标
                //   - to(8, 16, 15.1): 结束坐标
                //   - 这是一个沿X轴方向的薄片，宽度约1.6像素
                .element()
                    .from(8.0F, 0.0F, 0.9F)
                    .to(8.0F, 16.0F, 15.1F)
                    .rotation().angle(45.0F).axis(Direction.Axis.Y).origin(8.0F, 8.0F, 8.0F).rescale(true).end()
                    .shade(false)
                    .face(Direction.WEST).uvs(0, 0, 16, 16).texture("#stem").end()
                    .face(Direction.EAST).uvs(0, 0, 16, 16).texture("#stem").end()
                .end();
    }
    
    /**
     * 根据年龄值获取对应的阶段名称
     * 年龄0-1: stage0, 年龄2-3: stage1, 年龄4-5: stage2, 年龄6-7: stage3
     * 
     * @param age 年龄值（0-7）
     * @return String 阶段名称（stage0-stage3）
     */
    private String getStemStageName(int age) {
        if (age <= 1) {
            return "stage0";
        } else if (age <= 3) {
            return "stage1";
        } else if (age <= 5) {
            return "stage2";
        } else {
            return "stage3";
        }
    }
}