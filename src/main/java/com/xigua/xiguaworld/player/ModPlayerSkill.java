package com.xigua.xiguaworld.player;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.EntityCapability;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * 玩家技能槽位系统
 * 使用 NeoForge 能力系统为玩家提供 4 个技能槽位
 */
@EventBusSubscriber(modid = "xigua_world")
public class ModPlayerSkill {
    
    /** 技能注册表，用于通过 ID 查找技能 */
    private static final Map<String, Supplier<ISkill>> SKILL_REGISTRY = new HashMap<>();
    
    /**
     * 注册方法，供主类调用
     */
    public static void register(IEventBus modEventBus) {
        modEventBus.register(ModPlayerSkill.class);
    }
    
    /**
     * 注册技能到全局注册表
     * @param skillId 技能唯一标识
     * @param skillSupplier 技能实例提供者
     */
    public static void registerSkill(String skillId, Supplier<ISkill> skillSupplier) {
        SKILL_REGISTRY.put(skillId, skillSupplier);
    }
    
    /**
     * 从注册表获取技能实例
     * @param skillId 技能 ID
     * @return 技能实例，如果不存在则返回 null
     */
    @Nullable
    public static ISkill getSkillFromRegistry(String skillId) {
        Supplier<ISkill> supplier = SKILL_REGISTRY.get(skillId);
        return supplier != null ? supplier.get() : null;
    }
    
    /**
     * 技能接口
     * 所有自定义技能都需要实现此接口
     */
    public interface ISkill {
        /**
         * 获取技能唯一标识
         * @return 技能 ID，格式为 "modid:skill_name"
         */
        String getSkillID();
        
        /**
         * 获取技能显示名称
         * @return 技能名称，用于 GUI 显示
         */
        String getSkillName();
        
        /**
         * 获取技能图标纹理路径
         * @return 技能图标的 ResourceLocation，用于 HUD 显示
         */
        ResourceLocation getSkillIcon();
        
        /**
         * 技能激活时调用的方法
         * @param player 触发技能的玩家
         */
        void onActivate(Player player);
    }
    
    /**
     * 技能槽位能力接口
     * 用于管理玩家的 4 个技能槽位
     */
    public interface ISkillSlots {
        /**
         * 获取指定槽位的技能
         * @param slotIndex 槽位索引（0-3）
         * @return 槽位中的技能，如果为空则返回 null
         */
        @Nullable
        ISkill getSkill(int slotIndex);
        
        /**
         * 设置指定槽位的技能
         * @param slotIndex 槽位索引（0-3）
         * @param skill 要装备的技能
         * @return 是否设置成功
         */
        boolean setSkill(int slotIndex, ISkill skill);
        
        /**
         * 设置指定槽位的技能（通过技能 ID）
         * @param slotIndex 槽位索引（0-3）
         * @param skillId 技能 ID
         * @return 是否设置成功
         */
        boolean setSkillById(int slotIndex, String skillId);
        
        /**
         * 检查指定槽位是否已装备技能
         * @param slotIndex 槽位索引（0-3）
         * @return true 如果槽位有技能
         */
        boolean hasSkill(int slotIndex);
        
        /**
         * 清空指定槽位的技能
         * @param slotIndex 槽位索引（0-3）
         */
        void clearSkill(int slotIndex);
        
        /**
         * 获取最大槽位数
         * @return 固定返回 4
         */
        int getMaxSlots();
    }
    
    /**
     * 技能槽位能力常量
     * 用于注册和查询玩家技能槽位能力
     */
    public static final EntityCapability<ISkillSlots, Void> ENTITY =
            EntityCapability.create(
                    ResourceLocation.fromNamespaceAndPath("xiguaworld", "skill_slots"),
                    ISkillSlots.class,
                    Void.class
            );
    
    /**
     * 玩家技能槽位实现类
     * 管理 4 个技能槽位的数据
     */
    public static class PlayerSkillSlots implements ISkillSlots {
        
        /** 最大技能槽位数 */
        private static final int MAX_SLOTS = 4;
        
        /** NBT 中存储技能 ID 的键名前缀 */
        private static final String SKILL_SLOT_KEY = "SkillSlot_";
        
        /** 技能槽位数组，存储 4 个槽位的技能 ID */
        private String[] skillSlotIds = new String[MAX_SLOTS];
        
        /** 已实例化的技能缓存 */
        private ISkill[] skillCache = new ISkill[MAX_SLOTS];
        
        /**
         * 无参构造函数（能力系统需要）
         */
        public PlayerSkillSlots() {
            for (int i = 0; i < MAX_SLOTS; i++) {
                skillSlotIds[i] = null;
                skillCache[i] = null;
            }
        }
        
        @Override
        @Nullable
        public ISkill getSkill(int slotIndex) {
            if (slotIndex < 0 || slotIndex >= MAX_SLOTS) {
                return null;
            }
            
            // 如果缓存为空但槽位有 ID，尝试从注册表加载
            if (skillCache[slotIndex] == null && skillSlotIds[slotIndex] != null) {
                skillCache[slotIndex] = getSkillFromRegistry(skillSlotIds[slotIndex]);
            }
            
            return skillCache[slotIndex];
        }
        
        @Override
        public boolean setSkill(int slotIndex, ISkill skill) {
            if (slotIndex < 0 || slotIndex >= MAX_SLOTS || skill == null) {
                return false;
            }
            skillSlotIds[slotIndex] = skill.getSkillID();
            skillCache[slotIndex] = skill;
            return true;
        }
        
        @Override
        public boolean setSkillById(int slotIndex, String skillId) {
            if (slotIndex < 0 || slotIndex >= MAX_SLOTS || skillId == null) {
                return false;
            }
            skillSlotIds[slotIndex] = skillId;
            skillCache[slotIndex] = null;
            return true;
        }
        
        @Override
        public boolean hasSkill(int slotIndex) {
            if (slotIndex < 0 || slotIndex >= MAX_SLOTS) {
                return false;
            }
            return skillSlotIds[slotIndex] != null && !skillSlotIds[slotIndex].isEmpty();
        }
        
        @Override
        public void clearSkill(int slotIndex) {
            if (slotIndex >= 0 && slotIndex < MAX_SLOTS) {
                skillSlotIds[slotIndex] = null;
                skillCache[slotIndex] = null;
            }
        }
        
        @Override
        public int getMaxSlots() {
            return MAX_SLOTS;
        }
        
        /**
         * 将技能槽位数据序列化为 NBT
         */
        public CompoundTag serializeNBT() {
            CompoundTag tag = new CompoundTag();
            for (int i = 0; i < MAX_SLOTS; i++) {
                if (skillSlotIds[i] != null) {
                    tag.putString(SKILL_SLOT_KEY + i, skillSlotIds[i]);
                }
            }
            return tag;
        }
        
        /**
         * 从 NBT 加载技能槽位数据
         */
        public void deserializeNBT(CompoundTag tag) {
            for (int i = 0; i < MAX_SLOTS; i++) {
                String key = SKILL_SLOT_KEY + i;
                if (tag.contains(key)) {
                    skillSlotIds[i] = tag.getString(key);
                    skillCache[i] = null;
                } else {
                    skillSlotIds[i] = null;
                    skillCache[i] = null;
                }
            }
        }
    }
    
    /**
     * 注册技能槽位能力到 NeoForge 能力系统
     */
    @SubscribeEvent
    public static void onRegisterCapabilities(RegisterCapabilitiesEvent event) {
        event.registerEntity(
                ENTITY,
                EntityType.PLAYER,
                (Player player, Void context) -> new PlayerSkillSlots()
        );
    }
}