package datagen;

import com.xigua.xiguaworld.block.ModBlocks;
import com.xigua.xiguaworld.xiguaworld;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.features.FeatureUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.TagMatchTest;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class ModOreFeatures {
    public class ModWorldGen extends DatapackBuiltinEntriesProvider {
        public static final RegistrySetBuilder BUILDER = new RegistrySetBuilder()
                .add(Registries.CONFIGURED_FEATURE, ModOreFeatures::bootstrap)
                .add(Registries.PLACED_FEATURE, ModOrePlacements::bootstrap)
                .add(NeoForgeRegistries.Keys.BIOME_MODIFIERS, ModBiomeModifiers::bootstrap);

        public ModWorldGen(PackOutput output, HolderLookup.Provider registries) {
            super(output, CompletableFuture.completedFuture(registries), BUILDER, Set.of(xiguaworld.MOD_ID));
        }
    }
    // 创建OreFeature对应的ResourceKey
    //
    public static final ResourceKey<ConfiguredFeature<?, ?>> MYSTERIOUS_IRONSTONE = createKey("mysterious_ironstone");

    //BootstapContext 是我们datagen的上下文，等会我们使用数据生成的时候说。
    public static void bootstrap(BootstrapContext<ConfiguredFeature<?, ?>> pContext) {
        //  创建对应的tag，如果有多个就创建多个
        RuleTest ruletest = new TagMatchTest(BlockTags.STONE_ORE_REPLACEABLES);


        // 创建一个list
        List<OreConfiguration.TargetBlockState> list = List.of(
                OreConfiguration.target(ruletest, ModBlocks.MYSTERIOUS_IRONSTONE.get().defaultBlockState())
        );
        // 注册对应orefeature，使用listOreConfiguration，9 上文提到的size
        FeatureUtils.register(pContext, MYSTERIOUS_IRONSTONE, Feature.ORE, new OreConfiguration(list, 5, 0.8f));

    }
    // 创建ResourceKey的方法
    public static ResourceKey<ConfiguredFeature<?, ?>> createKey(String pName) {
        return ResourceKey.create(Registries.CONFIGURED_FEATURE, ResourceLocation.fromNamespaceAndPath(xiguaworld.MOD_ID,pName));
    }

}
