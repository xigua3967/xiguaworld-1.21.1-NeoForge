package datagen;

import com.xigua.xiguaworld.block.ModBlocks;
import com.xigua.xiguaworld.entity.ModEntityTypes;
import com.xigua.xiguaworld.item.ModItems;
import com.xigua.xiguaworld.xiguaworld;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;
public class ModEnUsLangProvider extends LanguageProvider {
   public ModEnUsLangProvider(PackOutput output ) {
        super(output, xiguaworld.MOD_ID, "en_us");
    }

    @Override
    protected void addTranslations() {
       add(ModItems.MERCURY_XIGUA.get(), "Mercury Watermelon");
       add(ModEntityTypes.MERCURYXIGUA_CREATURE.get(), "Mercury Watermelon");
       add(ModItems.MERCURY_XIGUA_SEEDS.get(), "Mercury Watermelon Seeds");
       add(ModBlocks.MERCURY_XIGUA_STEM.get(), "Mercury Watermelon Stem");
       add(ModBlocks.MERCURY_XIGUA_FRUIT.get(), "Mercury Watermelon");
       add(ModItems.MYSTERIOUS_IRON_ORE.get(), "Mysterious Iron Ore");
       add(ModItems.MYSTERIOUS_IRON.get(), "Mysterious Iron");
       //add(ModItems.MERCURY_XIGUA_BLOCK.get(), "Mercury Watermelon Block");
        add(ModBlocks.MYSTERIOUS_IRON_BLOCK.get(), "Mysterious Iron Block");
        add(ModBlocks.MYSTERIOUS_IRONSTONE.get(), "Mysterious Ironstone");
        add(ModItems.STICK_IN_LEATHER.get(), "Leather Wrapped Stick");
        add(ModItems.WOOL_PAD.get(), "Wool Pad");
        add(ModItems.MEMORY_PRISM.get(), "Memory Prism");
        add(ModItems.MYSTERIOUS_IRON_SWORD.get(), "Mysterious Iron Sword");
        add(ModItems.MYSTERIOUS_IRON_HELMET.get(), "Mysterious Iron Helmet");
        add(ModItems.MYSTERIOUS_IRON_CHESTPLATE.get(), "Mysterious Iron Chestplate");
        add(ModItems.MYSTERIOUS_IRON_LEGGINGS.get(), "Mysterious Iron Leggings");
        add(ModItems.MYSTERIOUS_IRON_BOOTS.get(), "Mysterious Iron Boots");
        add("tooltip.xiguaworld.mysterious_sword","Hold §9§n§lshift§r§r§r for more info!");
        add("tooltip.xiguaworld.mysterious_sword.shift","Use the right mouse button to release the skill!");

        add("itemGroup.xiguaworld_tab", "xigua_world Tab");
        add("key.xigua_world.open_skill_gui", "Open Skill GUI");
        add("gui.xigua_world.skill.title", "Skill System");
        add("gui.xigua_world.skill.tab.info", "Individual Information");
        add("gui.xigua_world.skill.tab.body", "Body Skills");
        add("gui.xigua_world.skill.tab.magic", "Magic");
        add("gui.xigua_world.skill.tab.blessing", "Blessing");
        add("gui.xigua_world.skill.name", "Name:");
        add("gui.xigua_world.skill.race", "Race:");
        add("gui.xigua_world.skill.race.human", "Human");
        add("gui.xigua_world.skill.body.strength", "Strength:");
        add("gui.xigua_world.skill.body.endurance", "Endurance:");
        add("gui.xigua_world.skill.body.agility", "Agility:");
        add("gui.xigua_world.skill.body.dexterity", "Dexterity:");
        add("gui.xigua_world.skill.magic.total_grade", "Total Grade:");
        add("gui.xigua_world.skill.magic.sub_grade", "Sub Grade:");
        add("gui.xigua_world.skill.magic.sword_grade", "Sword Grade:");
        add("gui.xigua_world.skill.magic.magic_grade", "Magic Grade:");
        add("gui.xigua_world.skill.placeholder", "Coming Soon...");
        add("key.xigua_world.open_skill_gui", "Skill System");
        add("key.xigua_world.activate_skill_1", "Activate Skill Slot 1");
        add("key.xigua_world.activate_skill_2", "Activate Skill Slot 2");
        add("key.xigua_world.activate_skill_3", "Activate Skill Slot 3");
        add("key.xigua_world.activate_skill_4", "Activate Skill Slot 4");
    }
/*"item.xigua_world.food.mercury_xigua": "Mercury's Watermelon",
  "item.xigua_world.material.mysterious_iron_ore": "Mysterious Iron Ore",
  "item.xigua_world.material.mysterious_iron": "Mysterious Iron",
  "item.xigua_world.material.mercury_xigua_seeds": "Mercury's Watermelon Seeds",
  "item.xigua_world.block.mercury_xigua_block": "Mercury's Watermelon Block",
  "itemGroup.xiguaworld_tab": "xigua_world Tab",

  "block.xigua_world.mysterious_ironstone": "Mysterious Ironstone",
  "block.xigua_world.mysterious_iron_block": "Mysterious Iron Block",*/
}