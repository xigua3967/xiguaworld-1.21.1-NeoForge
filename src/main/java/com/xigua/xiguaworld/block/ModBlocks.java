package com.xigua.xiguaworld.block;

import com.xigua.xiguaworld.block.custom.MercuryXiguaFruitBlock;
import com.xigua.xiguaworld.block.custom.MercuryXiguaStemBlock;
import com.xigua.xiguaworld.xiguaworld;
import com.xigua.xiguaworld.item.ModItems;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

/**
 * 方块注册类
 * 
 * 功能说明：
 * - 使用DeferredRegister延迟注册所有方块
 * - 每个方块自动创建对应的BlockItem
 * 
 * 引用关系：
 * - DeferredRegister.Blocks: NeoForge的方块注册器
 *   - createBlocks(): 创建方块注册器
 *   - register(): 注册单个方块
 * - DeferredBlock: 延迟加载的方块引用
 * - BlockItem: 方块的物品形式，用于在背包中显示
 * - BlockBehaviour.Properties: 方块属性构建器
 */
public class ModBlocks  {
    public static final DeferredRegister.Blocks BLOCKS =
            DeferredRegister.createBlocks(xiguaworld.MOD_ID);

    public static final DeferredBlock<Block> MYSTERIOUS_IRONSTONE =
            registerBlocks("mysterious_ironstone",()->new Block(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(3.5F, 3.0F)));

    public static final DeferredBlock<Block> MYSTERIOUS_IRON_BLOCK =
            registerBlocks("mysterious_iron_block",()->new Block(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_PURPLE).requiresCorrectToolForDrops().strength(5F, 6.0F).sound(SoundType.METAL)));

    /**
     * 水星西瓜果实方块
     * 
     * 属性说明：
     * - strength(1.0F, 0.5F): 硬度1.0，爆炸抗性0.5
     * - sound(SoundType.WOOD): 使用木质音效
     * - mapColor(MapColor.COLOR_GREEN): 地图颜色为绿色
     * 
     * 注意：果实方块必须在藤蔓方块之前注册，
     * 因为藤蔓方块的构造函数需要引用果实方块
     */
    public static final DeferredBlock<MercuryXiguaFruitBlock> MERCURY_XIGUA_FRUIT =
            BLOCKS.register("mercury_xigua_fruit", () -> new MercuryXiguaFruitBlock(
                    BlockBehaviour.Properties.of()
                            .strength(1.0F, 0.5F)    // 硬度和爆炸抗性
                            .sound(SoundType.WOOD)   // 木质音效
                            .mapColor(MapColor.COLOR_GREEN) // 地图颜色
            ));

    /**
     * 水星西瓜藤蔓方块
     * 
     * 属性说明：
     * - noCollission(): 无碰撞箱，玩家可以穿过藤蔓
     * - randomTicks(): 启用随机刻更新，用于生长逻辑
     * - strength(0.0F): 硬度为0，可以瞬间破坏
     * - sound(SoundType.CROP): 使用作物音效
     * 
     * 参数说明：
     * - 第一个参数：方块属性
     * - 第二个参数：果实方块的DeferredBlock引用（延迟加载）
     *   MERCURY_XIGUA_FRUIT: 果实方块的延迟加载引用
     *   使用DeferredBlock而不是直接调用.get()避免注册时的空指针异常
     */
    public static final DeferredBlock<MercuryXiguaStemBlock> MERCURY_XIGUA_STEM =
            BLOCKS.register("mercury_xigua_stem", () -> new MercuryXiguaStemBlock(
                    BlockBehaviour.Properties.of()
                            .noCollission()      // 无碰撞箱
                            .noOcclusion()       // 无遮挡，使透明贴图正确渲染
                            .randomTicks()       // 启用随机刻
                            .instabreak()        // 瞬间破坏
                            .sound(SoundType.CROP), // 作物音效
                    MERCURY_XIGUA_FRUIT          // 传入果实方块的DeferredBlock引用
            ));

    /**
     * 注册水星西瓜方块的BlockItem
     * 注意：水星西瓜方块不使用registerBlocks()方法注册，
     * 因为它们的注册顺序有特殊要求，所以需要手动注册BlockItem
     * 使用lambda延迟获取方块实例，避免注册时的空指针异常
     */
    public static final DeferredItem<BlockItem> MERCURY_XIGUA_FRUIT_ITEM =
            ModItems.ITEMS.register("mercury_xigua_fruit", () -> new BlockItem(MERCURY_XIGUA_FRUIT.get(), new Item.Properties()));

    /**
     * 注册方块对应的BlockItem
     * 
     * @param name 注册名称
     * @param block 方块引用
     */
    private static <T extends Block> void registerBlockItems(String name, DeferredBlock<T> block) {
        ModItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }

    /**
     * 注册方块并自动创建BlockItem
     * 
     * @param name 注册名称
     * @param block 方块Supplier
     * @return DeferredBlock<T> 延迟加载的方块引用
     */
    private static  <T extends Block> DeferredBlock<T> registerBlocks(String name, Supplier<T> block) {
        DeferredBlock<T> blocks = BLOCKS.register(name, block);
        registerBlockItems(name, blocks);
        return blocks;
    }
    
    /**
     * 注册所有方块到事件总线
     * 
     * @param eventBus NeoForge事件总线
     */
    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}