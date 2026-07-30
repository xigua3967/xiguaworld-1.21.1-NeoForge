package com.xigua.xiguaworld.item;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;

public class ModFoods {
    public static final FoodProperties MERCURY_XIGUA =new FoodProperties.Builder().nutrition(3).saturationModifier(3.4f)
            .effect(() ->new MobEffectInstance(MobEffects.REGENERATION, 400, 2), 1.0f)
            .effect(() ->new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 2000, 2), 1.0f)
            .effect(() ->new MobEffectInstance(MobEffects.DAMAGE_BOOST, 1000, 2), 1.0f)
            .build();
}
