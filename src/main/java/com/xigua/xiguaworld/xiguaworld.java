package com.xigua.xiguaworld;

import com.xigua.xiguaworld.block.ModBlocks;
import com.xigua.xiguaworld.client.ModEndurancehud;
import com.xigua.xiguaworld.entity.ModEntityAttributes;
import com.xigua.xiguaworld.entity.ModEntityTypes;
import com.xigua.xiguaworld.item.ModCreativeModeTabs;
import com.xigua.xiguaworld.item.ModItems;
import com.xigua.xiguaworld.player.*;
import com.xigua.xiguaworld.skills.ModSkills;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

// 这里的值应与 META-INF/neoforge.mods.toml 文件中的条目相匹配
@Mod(xiguaworld.MOD_ID)
public class xiguaworld {
    // 定义模组ID，用于所有内容引用
    public static final String MOD_ID = "xigua_world";
    // 直接引用一个 slf4j 日志记录器，用于记录日志
    public static final Logger LOGGER = LogUtils.getLogger();
    // 创建一个延迟注册器，用于注册块，所有块都在 "命名空间下注册
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MOD_ID);
    // 创建一个延迟注册器，用于注册物品，所有物品都在 "命名空间下注册
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MOD_ID);
    // 创建一个延迟注册器，用于注册CreativeModeTabs，所有CreativeModeTabs都在 "命名空间下注册
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MOD_ID);

    // 创建一个新的块，ID为 "命名空间下注册
    public static final DeferredBlock<Block> EXAMPLE_BLOCK = BLOCKS.registerSimpleBlock("example_block", BlockBehaviour.Properties.of().mapColor(MapColor.STONE));
    // 创建一个新的块Item，ID为 "命名空间下注册
    public static final DeferredItem<BlockItem> EXAMPLE_BLOCK_ITEM = ITEMS.registerSimpleBlockItem("example_block", EXAMPLE_BLOCK);

    // 创建一个新的食物物品，ID为 "命名空间下注册
    public static final DeferredItem<Item> EXAMPLE_ITEM = ITEMS.registerSimpleItem("example_item", new Item.Properties().food(new FoodProperties.Builder()
            .alwaysEdible().nutrition(1).saturationModifier(2f).build()));

    // 创建一个新的CreativeModeTab，ID为 "命名空间下注册
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> EXAMPLE_TAB = CREATIVE_MODE_TABS.register("example_tab", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.xigua_world")) //你的CreativeModeTab标题的语言键
            .withTabsBefore(CreativeModeTabs.COMBAT)
            .icon(() -> EXAMPLE_ITEM.get().getDefaultInstance())
            .displayItems((parameters, output) -> {
                output.accept(EXAMPLE_ITEM.get()); //将示例 com.xigua.xiguaworld.item 添加到标签页中。对于你自己的账单，这种方式比活动更受欢迎
            }).build());

    // 构造函数，用于初始化模组
    public xiguaworld(IEventBus modEventBus, ModContainer modContainer) {
        // 注册commonSetup方法，用于模组加载时初始化公共代码
        modEventBus.addListener(this::commonSetup);

        ModItems.register(modEventBus);
        ModCreativeModeTabs.register(modEventBus);
        ModBlocks.register(modEventBus);
        ModEntityTypes.register(modEventBus);
        ModPlayerEnergy.register(modEventBus);
        ModPlayerGrade.register(modEventBus);
        ModPlayermagicGrade.register(modEventBus);
        ModPlayerSwordGrade.register(modEventBus);
        ModPlayerSkill.register(modEventBus);
        
        // 注册所有技能
        ModSkills.register();

        // 注册自己，用于接收服务器事件和其他游戏事件
        // 注意，这仅当您希望此类（xiguaworld）直接响应事件时才需要。
        // 如果该类中没有@SubscribeEvent注释函数，如下面的 onServerStarting（） 那样，请不要添加这行。
        NeoForge.EVENT_BUS.register(this);

        // 注册示例 com.xigua.xiguaworld.item 到CreativeModeTab中
        modEventBus.addListener(this::addCreative);

        // 注册模组的ModConfigSpec，用于创建和加载配置文件
        // 这允许FML在模组加载时自动创建和加载配置文件，而无需手动处理
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        // 一些常见的设置代码
        LOGGER.info("HELLO FROM COMMON SETUP");

        if (Config.LOG_DIRT_BLOCK.getAsBoolean()) {
            LOGGER.info("DIRT BLOCK >> {}", BuiltInRegistries.BLOCK.getKey(Blocks.DIRT));
        }

        LOGGER.info("{}{}", Config.MAGIC_NUMBER_INTRODUCTION.get(), Config.MAGIC_NUMBER.getAsInt());

        Config.ITEM_STRINGS.get().forEach((item) -> LOGGER.info("ITEM >> {}", item));
    }

    // 将示例 com.xigua.xiguaworld.block com.xigua.xiguaworld.item 添加到构建块标签页中
    private void addCreative(BuildCreativeModeTabContentsEvent event) {

    }

    // 注册@SubscribeEvent注释方法，用于接收服务器启动事件
    // 当服务器启动时，执行此方法
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        // 当服务器启动时，执行此方法
        LOGGER.info("HELLO from server starting");
    }
}