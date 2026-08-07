package com.xigua.xiguaworld.client;

import com.xigua.xiguaworld.player.ModPlayerSkill;
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
public class ModSkillhud {

    private static final Logger LOGGER = LogUtils.getLogger();
    private static boolean hasLoggedLayers = false;
    private static String[] lastLoggedSkills = new String[4];

    // 空槽位背景纹理
    private static final ResourceLocation EMPTY_SLOT_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(xiguaworld.MOD_ID, "textures/gui/hud/empty_slot.png");

    @SubscribeEvent
    public static void onRenderGuiLayer(RenderGuiLayerEvent.Post event) {
        ResourceLocation layerName = event.getName();

        if (!hasLoggedLayers) {
            LOGGER.info("Skill HUD Layer rendered: {}", layerName);
        }

        if (layerName.getPath().equals("hotbar")) {
            if (!hasLoggedLayers) {
                LOGGER.info("Found hotbar layer, rendering skill slots");
                hasLoggedLayers = true;
            }
            renderSkillSlots(event.getGuiGraphics());
        }
    }

    private static void renderSkillSlots(GuiGraphics guiGraphics) {
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;

        if (player == null) {
            return;
        }

        ModPlayerSkill.ISkillSlots skillSlots = player.getCapability(ModPlayerSkill.ENTITY, null);
        if (skillSlots == null) {
            return;
        }

        int screenWidth = minecraft.getWindow().getGuiScaledWidth();
        int screenHeight = minecraft.getWindow().getGuiScaledHeight();

        int hotbarLeftX = (screenWidth - 182) / 2;
        int slotSize = 20;
        int gap = 2;
        int totalWidth = slotSize * 4 + gap * 3;
        int startX = hotbarLeftX - totalWidth - 8;
        int y = screenHeight - 22 - slotSize;

        for (int i = 0; i < 4; i++) {
            int slotX = startX + i * (slotSize + gap);

            // 渲染槽位背景（空槽位）
            guiGraphics.blit(
                    EMPTY_SLOT_TEXTURE,
                    slotX, y,
                    0, 0,
                    slotSize, slotSize,
                    20, 20
            );

            if (skillSlots.hasSkill(i)) {
                ModPlayerSkill.ISkill skill = skillSlots.getSkill(i);
                if (skill != null) {
                    // 渲染技能自己的图标纹理
                    ResourceLocation skillIcon = skill.getSkillIcon();
                    guiGraphics.blit(
                            skillIcon,
                            slotX, y,
                            0, 0,
                            slotSize, slotSize,
                            20, 20
                    );

                    // 渲染技能名称（槽位上方）
                    Component skillName = Component.literal(skill.getSkillName());
                    int textWidth = minecraft.font.width(skillName);
                    guiGraphics.drawString(
                            minecraft.font,
                            skillName,
                            slotX + slotSize / 2 - textWidth / 2,
                            y - 10,
                            0xFFFFFF
                    );

                    // 渲染槽位编号（1-4）
                    Component slotNum = Component.literal(String.valueOf(i + 1));
                    guiGraphics.drawString(
                            minecraft.font,
                            slotNum,
                            slotX + 2,
                            y + slotSize - 10,
                            0xAAAAAA
                    );
                }
            } else {
                // 空槽位显示槽位编号
                Component slotNum = Component.literal(String.valueOf(i + 1));
                guiGraphics.drawString(
                        minecraft.font,
                        slotNum,
                        slotX + slotSize / 2 - 3,
                        y + slotSize / 2 - 4,
                        0x666666
                );
            }
        }

        logSkillChanges(skillSlots);
    }

    private static void logSkillChanges(ModPlayerSkill.ISkillSlots skillSlots) {
        boolean changed = false;
        for (int i = 0; i < 4; i++) {
            String currentSkill = skillSlots.hasSkill(i) ? skillSlots.getSkill(i).getSkillID() : "empty";
            if (!currentSkill.equals(lastLoggedSkills[i])) {
                changed = true;
                lastLoggedSkills[i] = currentSkill;
            }
        }

        if (changed) {
            StringBuilder sb = new StringBuilder("Skill slots: [");
            for (int i = 0; i < 4; i++) {
                if (i > 0) sb.append(", ");
                sb.append(lastLoggedSkills[i]);
            }
            sb.append("]");
            LOGGER.debug(sb.toString());
        }
    }
}