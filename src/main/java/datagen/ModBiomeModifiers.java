package datagen;

import com.xigua.xiguaworld.entity.ModEntityTypes;
import com.xigua.xiguaworld.xiguaworld;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.common.world.BiomeModifiers;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class ModBiomeModifiers {
    public static class ModWorldGen extends DatapackBuiltinEntriesProvider {
        public static final RegistrySetBuilder BUILDER = new RegistrySetBuilder()
                .add(Registries.CONFIGURED_FEATURE, ModOreFeatures::bootstrap)
                .add(Registries.PLACED_FEATURE, ModOrePlacements::bootstrap)
                .add(NeoForgeRegistries.Keys.BIOME_MODIFIERS, ModBiomeModifiers::bootstrap);

        public ModWorldGen(PackOutput output, HolderLookup.Provider registries) {
            super(output, CompletableFuture.completedFuture(registries), BUILDER, Set.of(xiguaworld.MOD_ID));
        }
    }

    // 注册的key
    public static final ResourceKey<BiomeModifier> ADD_MYSTERIOUS_IRONSTONE = registerKey("add_mysterious_ironstone");
    public static final ResourceKey<BiomeModifier> ADD_MERCURYXIGUA_CREATURE_SPAWN = registerKey("add_mercuryxigua_creature_spawn");
    
    // BootstapContext 数据生成的上下文
    public static void bootstrap(BootstrapContext<BiomeModifier> context) {
        // 通过上下文获得PLACED_FEATURE的注册HolderGetter
        var placedFeatures = context.lookup(Registries.PLACED_FEATURE);
        // 通过上下文获得BIOME的HolderGetter
        var biomes = context.lookup(Registries.BIOME);
        // 生成json文件，第一个参数是key，第二个参数是BiomeModifiers，
        // 我们使用了子类AddFeaturesBiomeModifier，是指添加feature给biome
        // 第一个参数是holderset的biome ，这里是否是主世界的生物群系。即返回了主世界的生物群系
        // 第二个holdlerSet是指所有的feature，我们通过placedFeatures获得
        // 丢三个参数要求给出在世界生成的什么阶段加你的feature，我们这里是地下矿物生成的时候，你可以到该类下面看看，这是个枚举，
        context.register(ADD_MYSTERIOUS_IRONSTONE, new BiomeModifiers.AddFeaturesBiomeModifier(
                biomes.getOrThrow(BiomeTags.IS_OVERWORLD),
                HolderSet.direct(placedFeatures.getOrThrow(ModOrePlacements.MYSTERIOUS_IRONSTONE)),
                GenerationStep.Decoration.UNDERGROUND_ORES));

        // 注册MercuryxiguaCreature在普通针叶林群系的生成
        // 使用AddSpawnsBiomeModifier在指定群系添加实体生成
        // biomes参数：指定目标群系为普通针叶林（taiga）
        // spawners参数：实体生成配置列表
        //   - 实体类型：MercuryxiguaCreature
        //   - weight：生成权重（数值越低生成越少，这里设为10表示少量生成）
        //   - minCount：最小生成数量（1）
        //   - maxCount：最大生成数量（2）
        context.register(ADD_MERCURYXIGUA_CREATURE_SPAWN, new BiomeModifiers.AddSpawnsBiomeModifier(
                HolderSet.direct(biomes.getOrThrow(Biomes.TAIGA)),
                List.of(new MobSpawnSettings.SpawnerData(
                        ModEntityTypes.MERCURYXIGUA_CREATURE.get(),
                        10,
                        1,
                        2
                ))
        ));

    }
    private static ResourceKey<BiomeModifier> registerKey(String name) {
        return ResourceKey.create(NeoForgeRegistries.Keys.BIOME_MODIFIERS, ResourceLocation.fromNamespaceAndPath(xiguaworld.MOD_ID, name));
    }
}