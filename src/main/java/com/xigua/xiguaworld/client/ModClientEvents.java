package com.xigua.xiguaworld.client;

import com.xigua.xiguaworld.xiguaworld;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;

@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(modid = xiguaworld.MOD_ID, value = Dist.CLIENT)
public class ModClientEvents {

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft minecraft = Minecraft.getInstance();
        
        if (minecraft.player == null) {
            return;
        }

        if (ModKeyBindings.getOpenSkillGui() != null && ModKeyBindings.getOpenSkillGui().consumeClick()) {
            if (minecraft.screen == null) {
                minecraft.setScreen(new ModSkillSystemGui());
            }
        }
    }
}