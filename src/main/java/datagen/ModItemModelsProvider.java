package datagen;

import com.xigua.xiguaworld.item.ModItems;
import com.xigua.xiguaworld.xiguaworld;
import net.minecraft.data.PackOutput;
import net.minecraft.data.models.ModelProvider;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;


public class ModItemModelsProvider extends ItemModelProvider {
    public ModItemModelsProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, xiguaworld.MOD_ID, existingFileHelper);
    }
    @Override
    protected void registerModels() {
        basicItem(ModItems.MERCURY_XIGUA.get());
        // 水星西瓜种子 - 贴图文件名为 material_mercury_xigua_seeds.png
        basicItem(ModItems.MERCURY_XIGUA_SEEDS.get());
        basicItem(ModItems.MYSTERIOUS_IRON_ORE.get());
        basicItem(ModItems.MYSTERIOUS_IRON.get());
        basicItem(ModItems.STICK_IN_LEATHER.get());
        basicItem(ModItems.WOOL_PAD.get());
        basicItem(ModItems.MYSTERIOUS_IRON_HELMET.get());
        basicItem(ModItems.MYSTERIOUS_IRON_CHESTPLATE.get());
        basicItem(ModItems.MYSTERIOUS_IRON_LEGGINGS.get());
        basicItem(ModItems.MYSTERIOUS_IRON_BOOTS.get());
        basicItem(ModItems.MEMORY_PRISM.get());

        handheldItem(ModItems.MYSTERIOUS_IRON_SWORD.get());


    }
}