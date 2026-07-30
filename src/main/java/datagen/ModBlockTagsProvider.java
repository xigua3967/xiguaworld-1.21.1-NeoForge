package datagen;

import com.xigua.xiguaworld.block.ModBlocks;
import com.xigua.xiguaworld.xiguaworld;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ModBlockTagsProvider extends BlockTagsProvider {
    public ModBlockTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider,  @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, xiguaworld.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(BlockTags.MINEABLE_WITH_PICKAXE)
        .add(ModBlocks.MYSTERIOUS_IRONSTONE.get())
        .add(ModBlocks.MYSTERIOUS_IRON_BLOCK.get());
        tag(BlockTags.NEEDS_IRON_TOOL)
                .add(ModBlocks.MYSTERIOUS_IRON_BLOCK.get())
                .add(ModBlocks.MYSTERIOUS_IRONSTONE.get());
        tag(TagKey.create(BuiltInRegistries.BLOCK.key(),
                ResourceLocation.fromNamespaceAndPath(xiguaworld.MOD_ID, "needs_mysterious_iron_tool")))
                .add(ModBlocks.MYSTERIOUS_IRONSTONE.get());

    }
}
