package com.xigua.xiguaworld.player;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.capabilities.EntityCapability;

public class ModPlayerGrade {
    public interface Grade {
        int getCurrentGrade();
        int getMaxGrade();
        void setCurrentGrade(int grade);
    }
    public static final EntityCapability<Grade, Void> ENTITY =
            EntityCapability.create(
                    ResourceLocation.fromNamespaceAndPath("xiguaworld", "grade"),
                    Grade.class,
                    Void.class
            );
    
    public static class PlayerGrade implements Grade {
        private int currentGrade;
        private final int maxGrade = 10;

        public PlayerGrade() {
            this.currentGrade = 0;
        }

        @Override
        public int getCurrentGrade() {
            return currentGrade;
        }

        @Override
        public int getMaxGrade() {
            return maxGrade;
        }

        @Override
        public void setCurrentGrade(int grade) {
            this.currentGrade = Math.clamp(grade, 0, maxGrade);
        }
    }
    
}