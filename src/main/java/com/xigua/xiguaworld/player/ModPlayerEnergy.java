package com.xigua.xiguaworld.player;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.capabilities.EntityCapability;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import javax.annotation.Nullable;

// 定义模组玩家气力/耐力系统的主类
public class ModPlayerEnergy {
    
    // 定义气力能力接口，描述气力系统应提供的行为
    public interface Endurance {
        // 获取当前剩余气力值，返回 double 类型支持小数
        double getCurrentEndurance();
        // 获取气力最大值（上限），返回 double 类型
        double getMaxEndurance();
        // 消耗指定数量的气力，参数和返回值都是 double 类型
        double consumeEndurance(double amount);
        // 恢复指定数量的气力，参数和返回值都是 double 类型
        double restoreEndurance(double amount);
        // 直接设置气力值为指定值，支持小数
        void setCurrentEndurance(double amount);
    }
    
    // 创建气力能力的实体能力常量，用于注册和查询
    // static final 表示这是静态常量，整个游戏只创建一次
    public static final EntityCapability<Endurance, Void> ENTITY =
            // 调用 EntityCapability.create 方法创建能力实例
            EntityCapability.create(
                    // 使用 ResourceLocation 创建能力的唯一标识
                    // 命名空间为 "xiguaworld"，路径为 "endurance"
                    ResourceLocation.fromNamespaceAndPath("xiguaworld", "endurance"),
                    // 指定能力接口类型为 Endurance
                    Endurance.class,
                    // 指定上下文类型为 Void，表示查询时不需要额外参数
                    Void.class
            );
    
    // 定义 PlayerEndurance 类，实现 Endurance 接口
    // static 表示这是静态内部类，不需要外部类实例即可创建
    public static class PlayerEndurance implements Endurance {
        // 存储当前气力值，使用 double 支持小数
        private double currentEndurance;
        // 存储气力最大值，使用 double 支持小数
        private double maxEndurance;

        // 构造函数，创建气力实例时初始化最大值和当前值
        public PlayerEndurance(double maxEndurance) {
            // 将传入的最大值保存到字段中
            this.maxEndurance = maxEndurance;
            // 初始时当前气力等于最大气力（满状态）
            this.currentEndurance = maxEndurance;
        }

        // 设置气力最大值的方法，用于等级提升时更新上限
        public void setMaxEndurance(double maxEndurance) {
            // 更新最大气力值
            this.maxEndurance = maxEndurance;
        }

        // 实现接口方法：获取当前气力值
        @Override
        public double getCurrentEndurance() {
            // 返回当前存储的气力值
            return currentEndurance;
        }

        // 实现接口方法：获取最大气力值
        @Override
        public double getMaxEndurance() {
            // 返回当前存储的最大气力值
            return maxEndurance;
        }

        // 实现接口方法：消耗指定数量的气力
        @Override
        public double consumeEndurance(double amount) {
            // 计算实际消耗量：取请求值和当前气力中的较小值，防止透支
            double consumed = Math.min(amount, currentEndurance);
            // 从当前气力中减去实际消耗量
            currentEndurance -= consumed;
            // 返回实际消耗的气力值
            return consumed;
        }

        // 实现接口方法：恢复指定数量的气力
        @Override
        public double restoreEndurance(double amount) {
            // 计算实际恢复量：取请求值和剩余空间中的较小值，防止超出上限
            double restored = Math.min(amount, maxEndurance - currentEndurance);
            // 将实际恢复量加到当前气力上
            currentEndurance += restored;
            // 返回实际恢复的气力值
            return restored;
        }

        // 实现接口方法：直接设置气力值
        @Override
        public void setCurrentEndurance(double amount) {
            // 使用 Math.clamp 将值限制在 0 到 maxEndurance 范围内
            // 如果 amount < 0，设为 0；如果 amount > maxEndurance，设为 maxEndurance
            this.currentEndurance = Math.clamp(amount, 0, maxEndurance);
        }
    }
    
    // 使用 SubscribeEvent 注解标记能力注册事件处理方法
    // 该方法会在游戏加载时自动调用
    @SubscribeEvent
    // 定义静态事件处理方法，处理能力注册事件
    public static void onCapabilityEvent(RegisterCapabilitiesEvent event) {
        // 调用事件的 registerEntity 方法注册实体能力
        event.registerEntity(
                // 传入之前定义的气力能力常量 ENTITY
                ENTITY,
                // 指定要绑定能力的实体类型为玩家
                EntityType.PLAYER,
                // 提供能力提供者 Lambda 表达式
                // 当查询玩家气力能力时，会调用此表达式创建 PlayerEndurance 实例
                // player 是玩家实体，context 是上下文（Void 类型所以为 null）
                (Player player, Void context) -> new PlayerEndurance(100)
        );
    }
}