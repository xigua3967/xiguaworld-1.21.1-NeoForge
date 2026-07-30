package com.xigua.xiguaworld.item.custom;

import com.google.common.collect.ImmutableMap;
import com.xigua.xiguaworld.item.ModArmorMaterials;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class CustomArmorItem extends ArmorItem {
    private static final Map<Holder<ArmorMaterial>, List<MobEffectInstance>> MAP =
            (new ImmutableMap.Builder<Holder<ArmorMaterial>, List<MobEffectInstance>>())
                    .put(ModArmorMaterials.MYSTERIOUS_IRON,
                            Arrays.asList(
                                    new MobEffectInstance(MobEffects.NIGHT_VISION, 1, 0, false, false, true),
                                    new MobEffectInstance(MobEffects.SLOW_FALLING, 1, 0, false, false, true),
                                    new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 1, 0, false, false, true),
                                    new MobEffectInstance(MobEffects.DIG_SPEED, 1, 0, false, false, true)
                            )).build();

    public CustomArmorItem(Holder<ArmorMaterial> material, Type type, Properties properties) {
        super(material, type, properties);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int itemSlot, boolean isSelected) {
        if (!level.isClientSide()) {
            if (entity instanceof Player player && hasFullSuitableArmor(player)) {
                evaluateArmorEffects(player);
            }
        }
        super.inventoryTick(stack, level, entity, itemSlot, isSelected);
    }

    private void evaluateArmorEffects(Player player) {
        for (Map.Entry<Holder<ArmorMaterial>, List<MobEffectInstance>> entry : MAP.entrySet()) {
            ArmorMaterial material = entry.getKey().value();
            List<MobEffectInstance> effects = entry.getValue();

            if (hasCorretMaterialArmorOn(material, player)) {
                for (MobEffectInstance effect : effects) {
                    Holder<MobEffect> effectHolder = effect.getEffect();
                    if (!player.hasEffect(effectHolder)) {
                        player.addEffect(effect);
                    }
                }
            }
        }
    }

    private boolean hasCorretMaterialArmorOn(ArmorMaterial material, Player player) {
        for (ItemStack stack : player.getInventory().armor) {
            if (!(stack.getItem() instanceof ArmorItem)) {
                return false;
            }
        }
        ArmorItem helmet = (ArmorItem) player.getInventory().getArmor(3).getItem();
        ArmorItem chestplate = (ArmorItem) player.getInventory().getArmor(2).getItem();
        ArmorItem leggings = (ArmorItem) player.getInventory().getArmor(1).getItem();
        ArmorItem boots = (ArmorItem) player.getInventory().getArmor(0).getItem();

        return helmet.getMaterial().value() == material &&
                chestplate.getMaterial() .value()== material &&
                leggings.getMaterial().value() == material &&
                boots.getMaterial().value() == material;
    }
    private boolean hasFullSuitableArmor(Player player) {
        ItemStack helmet = player.getInventory().getArmor(3);
        ItemStack chestplate = player.getInventory().getArmor(2);
        ItemStack leggings = player.getInventory().getArmor(1);
        ItemStack boots = player.getInventory().getArmor(0);

        return !helmet.isEmpty() && !chestplate.isEmpty() && !leggings.isEmpty() && !boots.isEmpty();
    }
}