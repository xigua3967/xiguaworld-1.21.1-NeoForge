package com.xigua.xiguaworld.item;

import com.xigua.xiguaworld.block.ModBlocks;
import com.xigua.xiguaworld.xiguaworld;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModCreativeModeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, xiguaworld.MOD_ID);
    public static final Supplier<CreativeModeTab> XIGUAWORLD_TAB =
        CREATIVE_MODE_TABS.register("xiguaworld_tab",()-> CreativeModeTab.builder()
                .icon(() -> new ItemStack(ModItems.MERCURY_XIGUA.get()))
                .title(Component.translatable("itemGroup.xiguaworld_tab"))
                .displayItems((parameters, output) -> {
                    output.accept(ModItems.MERCURY_XIGUA_SEEDS.get());
                    output.accept(ModItems.MERCURY_XIGUA.get());
                    output.accept(ModBlocks.MERCURY_XIGUA_FRUIT.get());
                    //output.accept(ModItems.MERCURY_XIGUA_BLOCK.get());
                    output.accept(ModItems.MYSTERIOUS_IRON_ORE.get());
                    output.accept(ModItems.MYSTERIOUS_IRON.get());
                    output.accept(ModBlocks.MYSTERIOUS_IRON_BLOCK.get());
                    output.accept(ModBlocks.MYSTERIOUS_IRONSTONE.get());
                    output.accept(ModItems.STICK_IN_LEATHER.get());
                    output.accept(ModItems.MYSTERIOUS_IRON_SWORD.get());
                    output.accept(ModItems.MYSTERIOUS_IRON_HELMET.get());
                    output.accept(ModItems.MYSTERIOUS_IRON_CHESTPLATE.get());
                    output.accept(ModItems.MYSTERIOUS_IRON_LEGGINGS.get());
                    output.accept(ModItems.MYSTERIOUS_IRON_BOOTS.get());
                    output.accept(ModItems.WOOL_PAD.get());
                    output.accept(ModItems.MEMORY_PRISM.get());

                }).build());

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}
