package datagen;

import com.xigua.xiguaworld.entity.ModEntityTypes;
import com.xigua.xiguaworld.item.ModItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.loot.EntityLootSubProvider;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.EnchantedCountIncreaseFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemKilledByPlayerCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

import java.util.stream.Stream;

/**
 * 实体战利品表数据生成器
 * 用于生成MercuryxiguaCreature的战利品表
 */
public class ModEntityLootTablesProvider extends EntityLootSubProvider {

    public ModEntityLootTablesProvider(HolderLookup.Provider registries) {
        super(FeatureFlags.REGISTRY.allFlags(), registries);
    }

    @Override
    public void generate() {
        // MercuryxiguaCreature战利品表
        add(ModEntityTypes.MERCURYXIGUA_CREATURE.get(),
                LootTable.lootTable()
                        // 主要掉落池：水星西瓜种子（1-3个，受抢夺影响）
                        .withPool(
                                LootPool.lootPool()
                                        .setRolls(ConstantValue.exactly(1))
                                        .setBonusRolls(UniformGenerator.between(0.0F, 1.0F))
                                        .add(LootItem.lootTableItem(ModItems.MERCURY_XIGUA_SEEDS.get())
                                                .apply(SetItemCountFunction.setCount(
                                                        UniformGenerator.between(1.0F, 3.0F)
                                                ))
                                                .apply(EnchantedCountIncreaseFunction.lootingMultiplier(
                                                        this.registries,
                                                        UniformGenerator.between(0.0F, 1.0F)
                                                ))
                                        )
                        )
                        // 稀有掉落池：水星西瓜（被玩家击杀时30%概率）
                        .withPool(
                                LootPool.lootPool()
                                        .setRolls(ConstantValue.exactly(1))
                                        .add(LootItem.lootTableItem(ModItems.MERCURY_XIGUA.get()))
                                        .when(LootItemKilledByPlayerCondition.killedByPlayer())
                                        .when(LootItemRandomChanceCondition.randomChance(0.3F))
                        )
        );
    }

    @Override
    protected Stream<EntityType<?>> getKnownEntityTypes() {
        return Stream.of(
                ModEntityTypes.MERCURYXIGUA_CREATURE.get()
        );
    }
}