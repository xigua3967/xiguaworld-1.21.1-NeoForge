package com.xigua.xiguaworld.item;
import com.xigua.xiguaworld.block.ModBlocks;
import com.xigua.xiguaworld.block.custom.MercuryXiguaStemBlock;
import com.xigua.xiguaworld.item.custom.CustomArmorItem;
import com.xigua.xiguaworld.item.custom.MercuryXiguaSeedsItem;
import com.xigua.xiguaworld.xiguaworld;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SwordItem;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * 物品注册类
 * 
 * 功能说明：
 * - 使用DeferredRegister延迟注册所有物品
 * - 每个物品在mod加载时自动注册到游戏
 * 
 * 引用关系：
 * - DeferredRegister.Items: NeoForge的物品注册器
 *   - createItems(): 创建物品注册器
 *   - register(): 注册单个物品
 * - DeferredItem: 延迟加载的物品引用
 * - Item.Properties: 物品属性构建器
 *   - food(): 设置为食物
 *   - durability(): 设置耐久度
 */
public class ModItems {
    public static final DeferredRegister.Items ITEMS =
            DeferredRegister.createItems(xiguaworld.MOD_ID);

    public static final DeferredItem<Item> ICE_ETHER =  //作为注册物品例子的占位符
            ITEMS.register("ice_ether", ()-> new Item(new Item.Properties()));
    
    /**
     * 水星西瓜种子物品
     * 
     * 功能说明：
     * - 使用MercuryXiguaSeedsItem自定义类
     * - 右键耕地时放置藤蔓方块
     * - 传入MERCURY_XIGUA_STEM方块引用
     */
    public static final DeferredItem<Item> MERCURY_XIGUA_SEEDS =
            ITEMS.register("material_mercury_xigua_seeds", () -> new MercuryXiguaSeedsItem(
                    new Item.Properties(),
                    ModBlocks.MERCURY_XIGUA_STEM.get()
            ));
    
    public static final DeferredItem<Item> MERCURY_XIGUA =  //水星西瓜，基础作物
            ITEMS.register("food_mercury_xigua", ()-> new Item(new Item.Properties().food(ModFoods.MERCURY_XIGUA)));
    //public static final DeferredItem<Item> MERCURY_XIGUA_BLOCK =  //水星西瓜块
            //ITEMS.register("block/mercury_xigua_block", ()-> new Item(new Item.Properties()));
    //public static final DeferredItem<Item> MYSTERIOUS_IRONSTONE =  //魔铁矿石
    //        ITEMS.register("block/mysterious_ironstone", ()-> new Item(new Item.Properties()));
    public static final DeferredItem<Item> MYSTERIOUS_IRON_ORE =  //魔铁矿，用于烧制魔铁
            ITEMS.register("material_mysterious_iron_ore", ()-> new Item(new Item.Properties()));
    public static final DeferredItem<Item> MYSTERIOUS_IRON =  //魔铁，mod物品，用于制作魔铁工具
            ITEMS.register("material_mysterious_iron", ()-> new Item(new Item.Properties()));
    //public static final DeferredItem<Item> MYSTERIOUS_IRON_BLOCK =  //魔铁块，或许没什么用，我可能并不会给它添加可以激活信标的功能
            //ITEMS.register("block/mysterious_iron_block", ()-> new Item(new Item.Properties()));
    public static final DeferredItem<Item> STICK_IN_LEATHER =  //皮革裹柄
            ITEMS.register("material_stick_in_leather", ()-> new Item(new Item.Properties()));
    public static final DeferredItem<Item> WOOL_PAD =  //羊毛垫片
            ITEMS.register("material_wool_pad", ()-> new Item(new Item.Properties()));
    public static final DeferredItem<Item> MYSTERIOUS_IRON_SWORD =  //魔铁剑
            ITEMS.register("mysterious_iron_sword",
                    () -> new MysteriousSwordskill(ModToolTiers.MYSTERIOUS_IRON, 4, -2.4F, new Item.Properties()));
    public static final DeferredItem<Item> MYSTERIOUS_IRON_HELMET =  //魔铁头盔
            ITEMS.register("mysterious_iron_helmet",
                    () -> new CustomArmorItem(ModArmorMaterials.MYSTERIOUS_IRON, ArmorItem.Type.HELMET,
                            new Item.Properties().durability(ArmorItem.Type.HELMET.getDurability(40))));
    public static final DeferredItem<Item> MYSTERIOUS_IRON_CHESTPLATE =  //魔铁胸甲
            ITEMS.register("mysterious_iron_chestplate",
                    () -> new ArmorItem(ModArmorMaterials.MYSTERIOUS_IRON, ArmorItem.Type.CHESTPLATE,
                            new Item.Properties().durability(ArmorItem.Type.CHESTPLATE.getDurability(40))));
    public static final DeferredItem<Item> MYSTERIOUS_IRON_LEGGINGS =  //魔铁护腿
            ITEMS.register("mysterious_iron_leggings",
                    () -> new ArmorItem(ModArmorMaterials.MYSTERIOUS_IRON, ArmorItem.Type.LEGGINGS,
                            new Item.Properties().durability(ArmorItem.Type.LEGGINGS.getDurability(40))));
    public static final DeferredItem<Item> MYSTERIOUS_IRON_BOOTS =  //魔铁靴子
            ITEMS.register("mysterious_iron_boots",
                    () -> new ArmorItem(ModArmorMaterials.MYSTERIOUS_IRON, ArmorItem.Type.BOOTS,
                            new Item.Properties().durability(ArmorItem.Type.BOOTS.getDurability(40))));
    public static final DeferredItem<Item> MEMORY_PRISM =  //记忆棱镜
            ITEMS.register("material_memory_prism", ()-> new Item(new Item.Properties()));

    //作为一个占位用的注释，我会先跟着教程把这个文件和主类的联系加上
    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}