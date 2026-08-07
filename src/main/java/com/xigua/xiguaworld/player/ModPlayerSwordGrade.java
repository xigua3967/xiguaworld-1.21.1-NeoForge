package com.xigua.xiguaworld.player;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.EntityCapability;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

@EventBusSubscriber(modid = "xigua_world")
public class ModPlayerSwordGrade {
    public interface SwordGrade {
        int getCurrentSwordGrade();
        int getMaxSwordGrade();
        void setCurrentSwordGrade(int grade);
    }
    public static final EntityCapability<SwordGrade, Void> ENTITY =
            EntityCapability.create(
                    ResourceLocation.fromNamespaceAndPath("xiguaworld", "sword_grade"),
                    SwordGrade.class,
                    Void.class
            );
    public static class PlayerSwordGrade implements SwordGrade {
        private int currentSwordGrade ;
        private final int maxSwordGrade = 5;

        public PlayerSwordGrade(){
            this.currentSwordGrade = 0;
        }

        @Override
        public int getCurrentSwordGrade() {
            return 0;
        }

        @Override
        public int getMaxSwordGrade() {
            return 0;
        }

        @Override
        public void setCurrentSwordGrade(int grade) {
            this.currentSwordGrade = Math.clamp(grade, 0, maxSwordGrade);
        }
    }
    public static void register(IEventBus modEventBus) {
        modEventBus.register(ModPlayerSwordGrade.class);
    }
    @SubscribeEvent
    public static void onCapabilityEvent(RegisterCapabilitiesEvent event) {
        event.registerEntity(ENTITY, EntityType.PLAYER,
                (player, context) -> new PlayerSwordGrade());
    }
}
