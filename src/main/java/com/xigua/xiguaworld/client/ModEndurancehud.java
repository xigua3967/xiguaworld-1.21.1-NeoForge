package com.xigua.xiguaworld.client;

import com.xigua.xiguaworld.player.ModPlayerEnergy;
import com.xigua.xiguaworld.xiguaworld;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import org.slf4j.Logger;
import com.mojang.logging.LogUtils;

@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(modid = xiguaworld.MOD_ID, value = Dist.CLIENT)
public class ModEndurancehud {

    private static final Logger LOGGER = LogUtils.getLogger();
    private static boolean hasLoggedLayers = false;
    private static double lastLoggedEndurance = -1;  // 记录上次打印的耐力值
    private static double lastLoggedMaxEndurance = -1;  // 记录上次打印的最大耐力值
    
    private static final ResourceLocation ENDURANCE_BAR_TEXTURE = 
            ResourceLocation.fromNamespaceAndPath(xiguaworld.MOD_ID, "textures/gui/hud/endurance_bar.png");

    @SubscribeEvent
    public static void onRenderGuiLayer(RenderGuiLayerEvent.Post event) {
        ResourceLocation layerName = event.getName();
        
        // 调试：打印所有图层名称（仅第一次）
        if (!hasLoggedLayers) {
            LOGGER.info("HUD Layer rendered: {}", layerName);
        }
        
        // 在饥饿值渲染之后渲染耐力条
        if (layerName.getPath().equals("food_level")) {
            if (!hasLoggedLayers) {
                LOGGER.info("Found food_level layer, rendering endurance bar");
                hasLoggedLayers = true;
            }
            renderEnduranceBar(event.getGuiGraphics());
        }
    }

    private static void renderEnduranceBar(GuiGraphics guiGraphics) {
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        
        if (player == null) {
            LOGGER.debug("Player is null, skipping endurance bar render");
            return;
        }
        
        // 使用entity.getCapability()获取能力
        ModPlayerEnergy.Endurance endurance = player.getCapability(ModPlayerEnergy.ENTITY, null);
        if (endurance == null) {
            LOGGER.debug("Endurance capability is null, skipping render");
            return;
        }
        
        double currentEndurance = endurance.getCurrentEndurance();
        double maxEndurance = endurance.getMaxEndurance();
        
        // 仅在耐力值变化时打印日志（避免每帧都打印）
        if (currentEndurance != lastLoggedEndurance || maxEndurance != lastLoggedMaxEndurance) {
            LOGGER.debug("Rendering endurance bar: {}/{}", currentEndurance, maxEndurance);
            lastLoggedEndurance = currentEndurance;
            lastLoggedMaxEndurance = maxEndurance;
        }
        
        if (maxEndurance <= 0) {
            LOGGER.debug("Max endurance is <= 0, skipping render");
            return;
        }
        
        int barWidth = 182;
        int barHeight = 7;
        int screenWidth = minecraft.getWindow().getGuiScaledWidth();
        int screenHeight = minecraft.getWindow().getGuiScaledHeight();
        int x = (screenWidth - barWidth) / 2;
        int y = screenHeight - 49;
        
        // 渲染背景 - 使用纹理的上半部分 (v=0)
        guiGraphics.blit(
                ENDURANCE_BAR_TEXTURE,
                x, y,           // 屏幕坐标
                0, 7,           // 纹理起始坐标 (u, v)
                barWidth, barHeight,  // 渲染尺寸
                182, 14         // 纹理总尺寸 (width, height)
        );
        
        // 渲染进度 - 使用纹理的下半部分 (v=7)
        int fillWidth = (int) ((currentEndurance / maxEndurance) * barWidth);
        guiGraphics.blit(
                ENDURANCE_BAR_TEXTURE,
                x, y,           // 屏幕坐标
                0, 0,           // 纹理起始坐标 (u, v) - 从第7像素开始
                fillWidth, barHeight,  // 渲染尺寸（根据耐力值动态变化）
                182, 14         // 纹理总尺寸 (width, height)
        );
        
        // 渲染文本
        Component text = Component.literal(((int) currentEndurance) + "/" + ((int) maxEndurance));
        int textWidth = minecraft.font.width(text);
        guiGraphics.drawString(
                minecraft.font,
                text,
                x + barWidth / 2 - textWidth / 2,
                y - 0,
                0xFFFFFF
        );
    }
}