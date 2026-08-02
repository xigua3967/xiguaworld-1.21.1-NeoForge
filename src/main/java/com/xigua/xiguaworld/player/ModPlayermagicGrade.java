package com.xigua.xiguaworld.player;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.capabilities.EntityCapability;

public class ModPlayermagicGrade {
    public interface MagicGrade {
        int getCurrentMagicGrade();
        int getMaxMagicGrade();
        void setCurrentMagicGrade(int grade);
    }
    public static final EntityCapability<MagicGrade, Void> CAPABILITY =
            EntityCapability.create(
                    ResourceLocation.fromNamespaceAndPath("xiguaworld", "magic_grade"),
            MagicGrade.class,
            Void.class
            );
    public class PlayerMagicGrade implements MagicGrade {
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
}
