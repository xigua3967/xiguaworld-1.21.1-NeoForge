package com.xigua.xiguaworld.player;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.EntityCapability;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

@EventBusSubscriber(modid = "xigua_world")
public class ModPlayermagicGrade {
    public interface MagicGrade {
        int getCurrentMagicGrade();
        int getMaxMagicGrade();
        void setCurrentMagicGrade(int grade);
    }
    public static final EntityCapability<MagicGrade, Void> ENTITY =
            EntityCapability.create(
                    ResourceLocation.fromNamespaceAndPath("xiguaworld", "magic_grade"),
            MagicGrade.class,
            Void.class
            );
    public static class PlayerMagicGrade implements MagicGrade {
        private int currentMagicGrade;
        private final int maxMagicGrade = 1;

        public PlayerMagicGrade() {
            this.currentMagicGrade = 0;
        }

        @Override
        public int getCurrentMagicGrade() {
            return 0;
        }

        @Override
        public int getMaxMagicGrade() {
            return 0;
        }

        @Override
        public void setCurrentMagicGrade(int grade) {

        }
    }
    public static void register(IEventBus modEventBus) {
        modEventBus.register(ModPlayermagicGrade.class);
    }
    @SubscribeEvent
    public static void onCapabilityEvent(RegisterCapabilitiesEvent event) {
        event.registerEntity(ENTITY, EntityType.PLAYER,
                (player, context) -> new PlayerMagicGrade());
    }

}
