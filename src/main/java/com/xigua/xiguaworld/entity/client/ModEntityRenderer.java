package com.xigua.xiguaworld.entity.client;

import com.xigua.xiguaworld.entity.ModEntityTypes;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import com.xigua.xiguaworld.xiguaworld;

/**
 * 实体渲染器注册类
 * 用于注册所有自定义实体的渲染器
 * 此类仅在客户端加载
 */
@EventBusSubscriber(modid = xiguaworld.MOD_ID, value = Dist.CLIENT)
public class ModEntityRenderer {
    
    /**
     * 注册实体渲染器
     * 在 EntityRenderersEvent.RegisterRenderers 事件中自动调用
     * 
     * @param event 实体渲染器注册事件
     */
    @SubscribeEvent
    public static void onRegisterEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        // 注册 MercuryxiguaCreature 的渲染器
        event.registerEntityRenderer(
                ModEntityTypes.MERCURYXIGUA_CREATURE.get(),
                MercuryxiguaCreatureRenderer::new
        );
    }
    
    /**
     * 注册模型层定义
     * 在 EntityRenderersEvent.RegisterLayerDefinitions 事件中自动调用
     * 这是解决 "No model for layer" 错误的关键
     * 
     * @param event 模型层定义注册事件
     */
    @SubscribeEvent
    public static void onRegisterLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        // 注册 MercuryxiguaCreature 的模型层定义
        event.registerLayerDefinition(
                ModModelLayers.MERCURYXIGUA_CREATURE,
                MercuryxiguaCreatureModel::createBodyLayer
        );
    }
}