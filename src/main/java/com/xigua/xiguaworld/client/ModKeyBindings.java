package com.xigua.xiguaworld.client;

import com.xigua.xiguaworld.player.ModPlayerSkill;
import com.xigua.xiguaworld.xiguaworld;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import org.lwjgl.glfw.GLFW;

/**
 * 客户端按键绑定系统
 * 用于注册技能槽位激活按键和 GUI 打开按键
 */
@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(modid = xiguaworld.MOD_ID, value = Dist.CLIENT)
public class ModKeyBindings {

    /** 打开技能 GUI 的按键 */
    private static KeyMapping openSkillGui;
    
    /** 激活第 1 个槽位技能的按键（默认 R） */
    private static KeyMapping activateSkill1;
    
    /** 激活第 2 个槽位技能的按键（默认 F） */
    private static KeyMapping activateSkill2;
    
    /** 激活第 3 个槽位技能的按键（默认 V） */
    private static KeyMapping activateSkill3;
    
    /** 激活第 4 个槽位技能的按键（默认 C） */
    private static KeyMapping activateSkill4;

    /**
     * 注册所有按键映射
     */
    @SubscribeEvent
    public static void onRegisterKeyMappings(RegisterKeyMappingsEvent event) {
        // 打开技能 GUI
        openSkillGui = new KeyMapping(
                "key.xigua_world.open_skill_gui",
                GLFW.GLFW_KEY_G,
                "category.xigua_world"
        );
        event.register(openSkillGui);
        
        // 激活技能槽位 1
        activateSkill1 = new KeyMapping(
                "key.xigua_world.activate_skill_1",
                GLFW.GLFW_KEY_Z,
                "category.xigua_world"
        );
        event.register(activateSkill1);
        
        // 激活技能槽位 2
        activateSkill2 = new KeyMapping(
                "key.xigua_world.activate_skill_2",
                GLFW.GLFW_KEY_X,
                "category.xigua_world"
        );
        event.register(activateSkill2);
        
        // 激活技能槽位 3
        activateSkill3 = new KeyMapping(
                "key.xigua_world.activate_skill_3",
                GLFW.GLFW_KEY_C,
                "category.xigua_world"
        );
        event.register(activateSkill3);
        
        // 激活技能槽位 4
        activateSkill4 = new KeyMapping(
                "key.xigua_world.activate_skill_4",
                GLFW.GLFW_KEY_V,
                "category.xigua_world"
        );
        event.register(activateSkill4);
    }

    /**
     * 客户端 Tick 事件，用于检测按键并激活技能
     */
    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        
        if (player == null) {
            return;
        }
        
        // 获取玩家的技能槽位
        ModPlayerSkill.ISkillSlots slots = player.getCapability(ModPlayerSkill.ENTITY, null);
        if (slots == null) {
            return;
        }
        
        // 检查并激活槽位 1 的技能
        if (activateSkill1.consumeClick()) {
            activateSlotSkill(slots, 0, player);
        }
        
        // 检查并激活槽位 2 的技能
        if (activateSkill2.consumeClick()) {
            activateSlotSkill(slots, 1, player);
        }
        
        // 检查并激活槽位 3 的技能
        if (activateSkill3.consumeClick()) {
            activateSlotSkill(slots, 2, player);
        }
        
        // 检查并激活槽位 4 的技能
        if (activateSkill4.consumeClick()) {
            activateSlotSkill(slots, 3, player);
        }
    }
    
    /**
     * 激活指定槽位的技能
     * @param slots 技能槽位
     * @param slotIndex 槽位索引
     * @param player 玩家实例
     */
    private static void activateSlotSkill(ModPlayerSkill.ISkillSlots slots, int slotIndex, LocalPlayer player) {
        if (slots.hasSkill(slotIndex)) {
            ModPlayerSkill.ISkill skill = slots.getSkill(slotIndex);
            if (skill != null) {
                skill.onActivate(player);
            }
        }
    }

    /**
     * 获取打开技能 GUI 的按键
     */
    public static KeyMapping getOpenSkillGui() {
        return openSkillGui;
    }
    
    /**
     * 获取激活技能槽位 1 的按键
     */
    public static KeyMapping getActivateSkill1() {
        return activateSkill1;
    }
    
    /**
     * 获取激活技能槽位 2 的按键
     */
    public static KeyMapping getActivateSkill2() {
        return activateSkill2;
    }
    
    /**
     * 获取激活技能槽位 3 的按键
     */
    public static KeyMapping getActivateSkill3() {
        return activateSkill3;
    }
    
    /**
     * 获取激活技能槽位 4 的按键
     */
    public static KeyMapping getActivateSkill4() {
        return activateSkill4;
    }
}