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

@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(modid = xiguaworld.MOD_ID, value = Dist.CLIENT)
public class ModEndurancehud {

    private static final ResourceLocation ENDURANCE_BAR_TEXTURE = 
            ResourceLocation.fromNamespaceAndPath(xiguaworld.MOD_ID, "textures/gui/hud/endurance_bar.png");

    @SubscribeEvent
    public static void onRenderGuiLayer(RenderGuiLayerEvent.Post event) {
        ResourceLocation layerName = event.getName();
        
        // 在饥饿值渲染之后渲染耐力条
        if (layerName.getPath().equals("food_level")) {
            renderEnduranceBar(event.getGuiGraphics());
        }
    }

    private static void renderEnduranceBar(GuiGraphics guiGraphics) {
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        
        if (player == null) {
            return;
        }
        
        // 使用entity.getCapability()获取能力
        ModPlayerEnergy.Endurance endurance = player.getCapability(ModPlayerEnergy.ENTITY, null);
        if (endurance == null) {
            return;
        }
        
        double currentEndurance = endurance.getCurrentEndurance();
        double maxEndurance = endurance.getMaxEndurance();
        
        if (maxEndurance <= 0) {
            return;
        }
        
        int barWidth = 182;
        int barHeight = 5;
        int screenWidth = minecraft.getWindow().getGuiScaledWidth();
        int screenHeight = minecraft.getWindow().getGuiScaledHeight();
        int x = (screenWidth - barWidth) / 2;
        int y = screenHeight - 42;
        
        // 渲染背景
        guiGraphics.blit(ENDURANCE_BAR_TEXTURE, x, y, 0, 0, barWidth, barHeight);
        
        // 渲染进度
        int fillWidth = (int) ((currentEndurance / maxEndurance) * barWidth);
        guiGraphics.blit(ENDURANCE_BAR_TEXTURE, x, y, 0, 5, fillWidth, barHeight);
        
        // 渲染文本
        Component text = Component.literal(((int) currentEndurance) + "/" + ((int) maxEndurance));
        int textWidth = minecraft.font.width(text);
        guiGraphics.drawString(
                minecraft.font,
                text,
                x + barWidth / 2 - textWidth / 2,
                y - 10,
                0xFFFFFF
        );
    }
}