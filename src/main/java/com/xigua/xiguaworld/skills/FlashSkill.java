package com.xigua.xiguaworld.skills;

import com.xigua.xiguaworld.player.ModPlayerSkill;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

/**
 * 闪光技能
 * 在玩家准星处生成一个亮度为15的光源方块，持续一段时间后消失
 */
public class FlashSkill implements ModPlayerSkill.ISkill {
    
    /** 光源方块持续时间（tick），100 tick = 5秒 */
    private static final int DURATION_TICKS = 100;
    
    /** 最大放置距离 */
    private static final double MAX_DISTANCE = 10.0;

    @Override
    public String getSkillID() {
        return "xiguaworld:flash";
    }

    @Override
    public String getSkillName() {
        return "闪光";
    }

    @Override
    public ResourceLocation getSkillIcon() {
        return ResourceLocation.fromNamespaceAndPath("xiguaworld", "textures/gui/skills/flash.png");
    }

    @Override
    public void onActivate(Player player) {
        Level level = player.level();
        
        // 只在服务端执行
        if (level.isClientSide()) {
            return;
        }
        
        // 获取玩家视线方向
        HitResult hitResult = player.pick(MAX_DISTANCE, 0.0F, false);
        
        if (hitResult instanceof BlockHitResult blockHitResult) {
            // 获取准星指向的方块位置
            BlockPos targetPos = blockHitResult.getBlockPos();
            
            // 获取点击的面，在目标方块外侧放置光源
            BlockPos lightPos = targetPos.relative(blockHitResult.getDirection());
            
            // 检查目标位置是否为空气或可被替换的方块
            if (level.getBlockState(lightPos).canBeReplaced()) {
                // 放置光源方块（亮度15）
                level.setBlock(lightPos, Blocks.LIGHT.defaultBlockState(), 3);
                
                // 设置方块在指定 tick 后消失
                level.scheduleTick(lightPos, Blocks.LIGHT, DURATION_TICKS);
            }
        }
    }
}