package com.xigua.xiguaworld.player;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.capabilities.EntityCapability;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

import javax.annotation.Nullable;

public class ModPlayerEnergy {
    
    public interface Endurance {
        double getCurrentEndurance();
        double getMaxEndurance();
        double consumeEndurance(int amount);
        double restoreEndurance(int amount);
        void setCurrentEndurance(double amount);
    }
    
    public static final EntityCapability<Endurance, Void> ENTITY =
            EntityCapability.create(
                    ResourceLocation.fromNamespaceAndPath("xiguaworld", "endurance"),
                    Endurance.class,
                    Void.class
            );
    
    public static class PlayerEndurance implements Endurance {
        private double currentEndurance;
        private double maxEndurance;

        public PlayerEndurance(double maxEndurance) {
            this.maxEndurance = maxEndurance;
            this.currentEndurance = maxEndurance;
        }

        public void setMaxEndurance(double maxEndurance) {
            this.maxEndurance = maxEndurance;
        }

        @Override
        public double getCurrentEndurance() {
            return currentEndurance;
        }

        @Override
        public double getMaxEndurance() {
            return maxEndurance;
        }

        @Override
        public double consumeEndurance(int amount) {
            double consumed = Math.min(amount, currentEndurance);
            currentEndurance -= consumed;
            return consumed;
        }

        @Override
        public double restoreEndurance(int amount) {
            double restored = Math.min(amount, maxEndurance - currentEndurance);
            currentEndurance += restored;
            return restored;
        }

        @Override
        public void setCurrentEndurance(double amount) {
            this.currentEndurance = Math.clamp(amount, 0, maxEndurance);
        }
    }
    
    @SubscribeEvent
    public static void onCapabilityEvent(RegisterCapabilitiesEvent event) {
        event.registerEntity(
                ENTITY,
                EntityType.PLAYER,
                (Player player, Void context) -> new PlayerEndurance(100)
        );
    }
}