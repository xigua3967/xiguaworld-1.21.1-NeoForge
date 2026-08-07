package com.xigua.xiguaworld.skills;

import com.xigua.xiguaworld.player.ModPlayerSkill;

/**
 * 技能注册类
 * 负责注册所有可用技能
 */
public class ModSkills {
    
    /**
     * 注册所有技能
     */
    public static void register() {
        // 注册闪光技能
        ModPlayerSkill.registerSkill("xiguaworld:flash", FlashSkill::new);
    }
}