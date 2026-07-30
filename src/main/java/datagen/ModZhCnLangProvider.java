package datagen;

import com.xigua.xiguaworld.block.ModBlocks;
import com.xigua.xiguaworld.entity.ModEntityTypes;
import com.xigua.xiguaworld.item.ModItems;
import com.xigua.xiguaworld.xiguaworld;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;
public class ModZhCnLangProvider extends LanguageProvider {
    public ModZhCnLangProvider(PackOutput output) {
        super(output, xiguaworld.MOD_ID, "zh_cn");
    }
    @Override
    protected void addTranslations() {
        add(ModItems.MERCURY_XIGUA.get(), "水星西瓜");
        add(ModItems.MERCURY_XIGUA_SEEDS.get(), "水星瓜种");
        add(ModBlocks.MERCURY_XIGUA_STEM.get(), "水星西瓜藤");
        add(ModBlocks.MERCURY_XIGUA_FRUIT.get(), "水星西瓜");
        add(ModEntityTypes.MERCURYXIGUA_CREATURE.get(), "水星西瓜");
        add(ModItems.MYSTERIOUS_IRON_ORE.get(), "魔铁矿");
        add(ModItems.MYSTERIOUS_IRON.get(), "魔铁");
        add(ModBlocks.MYSTERIOUS_IRON_BLOCK.get(), "魔铁块");
        add(ModBlocks.MYSTERIOUS_IRONSTONE.get(), "魔铁矿石");
        add(ModItems.STICK_IN_LEATHER.get(), "皮革裹柄");
        add(ModItems.WOOL_PAD.get(), "羊毛垫片");
        add(ModItems.MEMORY_PRISM.get(), "记忆棱镜");
        add(ModItems.MYSTERIOUS_IRON_SWORD.get(), "魔铁剑");
        add(ModItems.MYSTERIOUS_IRON_HELMET.get(), "魔铁头盔");
        add(ModItems.MYSTERIOUS_IRON_CHESTPLATE.get(), "魔铁胸甲");
        add(ModItems.MYSTERIOUS_IRON_LEGGINGS.get(), "魔铁护腿");
        add(ModItems.MYSTERIOUS_IRON_BOOTS.get(), "魔铁靴子");
        add("tooltip.xiguaworld.mysterious_sword","按住§9§n§lshift§r§r§r以获取更多信息!");
        add("tooltip.xiguaworld.mysterious_sword.shift","使用右键释放技能!");
        add("itemGroup.xiguaworld_tab", "西瓜の世界");
    }
    /*
  "item.xigua_world.food.mercury_xigua": "水星西瓜",
  "item.xigua_world.material.mercury_xigua_seeds": "水星瓜种",
  "item.xigua_world.block.mercury_xigua_block": "水星瓜块",
  "item.xigua_world.material.mysterious_iron_ore": "魔铁矿",
  "item.xigua_world.material.mysterious_iron": "魔铁",
  "itemGroup.xiguaworld_tab": "西瓜の世界",

  "block.xigua_world.mysterious_ironstone": "魔铁矿石",
  "block.xigua_world.mysterious_iron_block": "魔铁块"*/
}