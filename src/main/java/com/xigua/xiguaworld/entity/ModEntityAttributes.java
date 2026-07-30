package com.xigua.xiguaworld.entity;

import com.xigua.xiguaworld.entity.custom.MercuryxiguaCreature;
import com.xigua.xiguaworld.xiguaworld;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;

/**
 * 实体属性注册类
 * 用于注册所有自定义实体的属性（生命值、移动速度、攻击力等）
 */
@EventBusSubscriber(modid = xiguaworld.MOD_ID)
public class ModEntityAttributes {
    
    /**
     * 为 MercuryxiguaCreature 创建属性
     * 
     * @return 属性构建器
     */
    public static AttributeSupplier.Builder createMercuryxiguaCreatureAttributes() {
        return Mob.createMobAttributes()
                // 最大生命值
                .add(Attributes.MAX_HEALTH, 10.0)
                // 移动速度
                .add(Attributes.MOVEMENT_SPEED, 0.0)
                // 跟随范围（检测玩家的距离）
                .add(Attributes.FOLLOW_RANGE, 16.0)
                // 护甲值
                .add(Attributes.ARMOR, 0.0);
    }
    
    /**
     * 注册实体属性
     * 通过 EntityAttributeCreationEvent 自动调用
     * 
     * @param event 实体属性创建事件
     */
    @SubscribeEvent
    public static void onEntityAttributeCreation(EntityAttributeCreationEvent event) {
        event.put(ModEntityTypes.MERCURYXIGUA_CREATURE.get(), 
                  createMercuryxiguaCreatureAttributes().build());
    }
}