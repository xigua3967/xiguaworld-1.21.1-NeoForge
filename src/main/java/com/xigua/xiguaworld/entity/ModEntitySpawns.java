package com.xigua.xiguaworld.entity;

import com.xigua.xiguaworld.xiguaworld;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;

/**
 * 实体生成位置注册类
 * 用于注册自定义实体的生成位置和生成规则
 */
@EventBusSubscriber(modid = xiguaworld.MOD_ID)
public class ModEntitySpawns {
    
    /**
     * 注册实体生成位置
     * 通过RegisterSpawnPlacementsEvent自动调用
     * 
     * @param event 生成位置注册事件
     */
    @SubscribeEvent
    public static void registerSpawnPlacements(RegisterSpawnPlacementsEvent event) {
        // 注册MercuryxiguaCreature的生成位置
        // 参数说明：
        // 1. 实体类型：MercuryxiguaCreature
        // 2. 生成位置类型：ON_GROUND - 在地面方块上生成
        // 3. 高度图类型：MOTION_BLOCKING_NO_LEAVES - 不包括树叶的运动阻挡高度图
        // 4. 生成谓词：Animal::checkAnimalSpawnRules - 使用动物的生成规则（需要光照等级>=9）
        // 5. 操作类型：REPLACE - 替换默认生成规则
        event.register(
                ModEntityTypes.MERCURYXIGUA_CREATURE.get(),
                SpawnPlacementTypes.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Animal::checkAnimalSpawnRules,
                RegisterSpawnPlacementsEvent.Operation.REPLACE
        );
    }
}