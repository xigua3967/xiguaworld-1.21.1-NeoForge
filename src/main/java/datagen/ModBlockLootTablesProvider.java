package datagen;

import com.xigua.xiguaworld.block.ModBlocks;
import com.xigua.xiguaworld.item.ModItems;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

import java.util.Set;

public class ModBlockLootTablesProvider extends BlockLootSubProvider {
    public ModBlockLootTablesProvider(HolderLookup.Provider registries) {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags(), registries);
    }

    @Override
    protected void generate() {
        dropSelf(ModBlocks.MYSTERIOUS_IRON_BLOCK.get());
        add(ModBlocks.MYSTERIOUS_IRONSTONE.get(),
                block -> createMysteriousIronOreDrops(ModBlocks.MYSTERIOUS_IRONSTONE.get(), ModItems.MYSTERIOUS_IRON_ORE.get(), 1.0F, 2.0F));

        // 水星西瓜果实 - 破坏时掉落水星西瓜物品
        add(ModBlocks.MERCURY_XIGUA_FRUIT.get(),
                block -> createMysteriousIronOreDrops(ModBlocks.MERCURY_XIGUA_FRUIT.get(), ModItems.MERCURY_XIGUA.get(), 3.0F, 4.0F));

        // 水星西瓜藤蔓 - 破坏时掉落种子
        // 手动创建战利品表，因为createCropDrops需要非null的条件参数
        add(ModBlocks.MERCURY_XIGUA_STEM.get(), block -> LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(ModItems.MERCURY_XIGUA_SEEDS.get()))
                )
        );
    }
    protected LootTable.Builder createMysteriousIronOreDrops(Block block, Item item,float min,float max ) {
        HolderLookup.RegistryLookup<Enchantment> registrylookup = this.registries.lookupOrThrow(Registries.ENCHANTMENT);
        return this.createSilkTouchDispatchTable(
                block,
                (LootPoolEntryContainer.Builder<?>)this.applyExplosionDecay(
                        block,
                        LootItem.lootTableItem(item)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(min, max)))
                                .apply(ApplyBonusCount.addOreBonusCount(registrylookup.getOrThrow(Enchantments.FORTUNE)))
                )
        );
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return ModBlocks.BLOCKS.getEntries().stream().map(Holder::value)::iterator;
    }
}