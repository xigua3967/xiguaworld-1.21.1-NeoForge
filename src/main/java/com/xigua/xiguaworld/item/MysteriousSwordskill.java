package com.xigua.xiguaworld.item;

import net.minecraft.network.chat.Component;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.core.particles.ParticleTypes;

import java.awt.*;
import java.util.List;

public class MysteriousSwordskill extends SwordItem {

    public MysteriousSwordskill(Tier tier, int damage, float attackSpeed, Properties properties) {
        super(tier, properties.attributes(SwordItem.createAttributes(tier, damage, attackSpeed)));

    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);

        // 获取玩家视线方向
        Vec3 viewVector = player.getViewVector(1.0F);

        // 创建射线追踪，获取玩家看向的实体
        HitResult hitResult = player.pick(5.0, 1.0F, false);

        // 获取玩家前方5格范围内的所有实体
        Vec3 eyePosition = player.getEyePosition();
        Vec3 targetVec = eyePosition.add(viewVector.scale(2.0));
        AABB searchBox = new AABB(eyePosition, targetVec).inflate(1.0);

        List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, searchBox);

        LivingEntity target = null;
        double closestDistance = Double.MAX_VALUE;

        // 找到最近的实体
        for (LivingEntity entity : entities) {
            if (entity != player && entity.isAlive()) {
                double distance = player.distanceTo(entity);
                if (distance < closestDistance && distance <= 5.0) {
                    closestDistance = distance;
                    target = entity;
                }
            }
        }

        // 如果找到目标，造成伤害
        if (target != null) {
            // 造成剑的伤害（基于工具等级和附加伤害）
            float damage = this.getAttackDamage();
            target.hurt(level.damageSources().playerAttack(player), damage);

            // 击退效果
            double knockbackStrength = 0.1;
            Vec3 knockbackVec = target.position().subtract(player.position()).normalize().scale(knockbackStrength);
            target.push(knockbackVec.x, 0.01, knockbackVec.z);



            // 产生粒子效果（可选）
            level.broadcastEntityEvent(target, (byte)4);

            target.addEffect(new MobEffectInstance(MobEffects.GLOWING, 40, 0, false, false));
        }
        player.getCooldowns().addCooldown(this, 200);

        // 返回成功结果
        return InteractionResultHolder.success(itemStack);
    }

    // 获取攻击力
    private float getAttackDamage() {
        // 基础伤害 + 工具等级伤害加成
        return 18.0F + this.getTier().getAttackDamageBonus();
    }

        public void appendHoverText(ItemStack stack, TooltipContext context, List<net.minecraft.network.chat.Component> tooltipComponents, TooltipFlag tooltipFlag){
            super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
            if (Screen.hasShiftDown()){
                tooltipComponents.add(Component.translatable("tooltip.xiguaworld.mysterious_sword.shift"));
            }else {
                tooltipComponents.add(Component.translatable("tooltip.xiguaworld.mysterious_sword"));
            }
        }


    }