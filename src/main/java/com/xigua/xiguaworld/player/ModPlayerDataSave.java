package com.xigua.xiguaworld.player;

import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.energy.IEnergyStorage;

public class ModPlayerDataSave {
   public static final AttachmentType<CompoundTag> PLAYER_DATA =
           AttachmentType.builder(() -> new CompoundTag()).build();
   
   public static final AttachmentType<ModPlayerEnergy.PlayerEndurance> MOD_PLAYER_ENERGY_ATTACHMENT_DATA =
           AttachmentType.builder(() -> new ModPlayerEnergy.PlayerEndurance(100)).build();
   
   public static final AttachmentType<ModPlayerGrade> MOD_PLAYER_GRADE_ATTACHMENT_DATA =
           AttachmentType.builder(() -> new ModPlayerGrade()).build();

   public static final AttachmentType<ModPlayerSwordGrade> MOD_PLAYER_SWORD_GRADE_ATTACHMENT_DATA =
           AttachmentType.builder(() -> new ModPlayerSwordGrade()).build();

   public static final AttachmentType<ModPlayermagicGrade> MOD_PLAYERMAGIC_GRADE_ATTACHMENT_DATA =
           AttachmentType.builder(() -> new ModPlayermagicGrade()).build();


}