package com.xigua.xiguaworld;

import datagen.*;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@EventBusSubscriber(modid = xiguaworld.MOD_ID)
public class ModDataGenerator {
    @SubscribeEvent
    public static void gatherData(GatherDataEvent event){
        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = generator.getPackOutput();
        ExistingFileHelper efh = event.getExistingFileHelper();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
        HolderLookup.Provider lp = lookupProvider.join();

        generator.addProvider(event.includeServer(), new LootTableProvider(packOutput, Collections.emptySet(),
                List.of(
                        new LootTableProvider.SubProviderEntry(ModBlockLootTablesProvider::new, LootContextParamSets.BLOCK),
                        new LootTableProvider.SubProviderEntry(ModEntityLootTablesProvider::new, LootContextParamSets.ENTITY)
                ), lookupProvider));

        generator.addProvider(event.includeServer(),new ModRecipesProvider(packOutput,lookupProvider));

        BlockTagsProvider blockTagsProvider = new ModBlockTagsProvider(packOutput,lookupProvider,efh);
        generator.addProvider(event.includeServer(),blockTagsProvider);
        generator.addProvider(event.includeServer(),new ModItemTagsProvider(packOutput,lookupProvider,blockTagsProvider.contentsGetter(),efh));
        generator.addProvider(event.includeClient(),new ModItemModelsProvider(packOutput,efh));
        generator.addProvider(event.includeClient(),new ModBlockStatesProvider(packOutput,efh));
        generator.addProvider(event.includeClient(),new ModEnUsLangProvider(packOutput));
        generator.addProvider(event.includeClient(),new ModZhCnLangProvider(packOutput));

        event.getGenerator().addProvider(event.includeServer(),
                (DataProvider.Factory<ModBiomeModifiers.ModWorldGen>) pOutput ->
                        new ModBiomeModifiers.ModWorldGen(pOutput, lp));
    }

}