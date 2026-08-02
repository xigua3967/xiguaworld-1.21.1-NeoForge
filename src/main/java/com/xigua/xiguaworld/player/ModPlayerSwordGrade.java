package com.xigua.xiguaworld.player;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.capabilities.EntityCapability;

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
    public class PlayerSwordGrade implements SwordGrade {
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
}
