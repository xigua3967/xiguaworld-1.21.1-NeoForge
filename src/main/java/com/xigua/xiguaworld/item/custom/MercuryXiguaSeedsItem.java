package com.xigua.xiguaworld.item.custom;

import com.xigua.xiguaworld.block.custom.MercuryXiguaStemBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.state.BlockState;

/**
 * 水星西瓜种子物品 - 类似原版的SeedItem
 * 
 * 功能说明：
 * - 右键耕地时放置水星西瓜藤蔓方块
 * - 继承自Item，重写useOn方法实现放置逻辑
 * 
 * 工作流程：
 * 1. 玩家手持种子右键耕地
 * 2. 检查耕地是否有效
 * 3. 在耕地上方放置藤蔓方块
 * 4. 消耗一个种子
 * 
 * 引用关系：
 * - Item: 所有物品的基类
 *   - useOn(): 右键使用物品时调用
 *   - getDefaultInstance(): 获取默认物品堆
 * - BlockPlaceContext: 方块放置上下文
 *   - getClickedPos(): 获取点击的方块位置
 *   - getLevel(): 获取世界
 *   - getPlayer(): 获取玩家
 * - FarmBlock: 耕地方块
 * - MercuryXiguaStemBlock: 水星西瓜藤蔓方块
 * - InteractionResult: 交互结果枚举
 *   - SUCCESS: 成功
 *   - PASS: 跳过（让其他逻辑处理）
 *   - FAIL: 失败
 * - ItemStack: 物品堆
 */
public class MercuryXiguaSeedsItem extends Item {
    
    /**
     * 藤蔓方块引用
     * 用于放置时获取方块实例
     */
    private final MercuryXiguaStemBlock stemBlock;
    
    /**
     * 构造函数
     * 
     * @param properties 物品属性
     *                   - Item.Properties() 创建默认属性
     * @param stemBlock 藤蔓方块实例
     */
    public MercuryXiguaSeedsItem(Properties properties, MercuryXiguaStemBlock stemBlock) {
        super(properties);
        this.stemBlock = stemBlock;
    }
    
    /**
     * 右键使用物品时调用
     * 用于在耕地上放置藤蔓方块
     * 
     * @param context UseOnContext（使用上下文）
     *                - 包含玩家、世界、点击位置等信息
     * @return InteractionResult 交互结果
     */
    @Override
    public InteractionResult useOn(UseOnContext context) {
        // 获取点击的方块位置
        BlockPos pos = context.getClickedPos();
        
        // 获取世界
        Level level = context.getLevel();
        
        // 获取点击的方块状态
        BlockState clickedState = level.getBlockState(pos);
        
        // 检查点击的方块是否是耕地
        // instanceof: Java关键字，检查对象是否为指定类型的实例
        if (!(clickedState.getBlock() instanceof FarmBlock)) {
            // 如果不是耕地，跳过处理（让其他逻辑处理）
            return InteractionResult.PASS;
        }
        
        // 获取玩家
        Player player = context.getPlayer();
        
        // 获取玩家手中的物品堆
        ItemStack itemStack = context.getItemInHand();
        
        // 检查是否有权限放置方块（服务器端检查）
        if (player != null && !player.mayUseItemAt(pos, context.getClickedFace(), itemStack)) {
            return InteractionResult.FAIL;
        }
        
        // 获取藤蔓方块的默认状态
        BlockState stemState = this.stemBlock.defaultBlockState();
        
        // 检查目标位置是否可以放置藤蔓
        // pos.above(): 获取点击位置上方的位置
        BlockPos abovePos = pos.above();
        BlockState aboveState = level.getBlockState(abovePos);
        
        // 如果上方是空气，则可以放置
        if (aboveState.isAir()) {
            // 在客户端不执行实际放置（只播放动画）
            if (level.isClientSide) {
                return InteractionResult.SUCCESS;
            }
            
            // 在服务器端放置藤蔓方块
            level.setBlock(abovePos, stemState, 3);
            
            // 消耗一个种子
            // shrink: 减少物品堆数量
            itemStack.shrink(1);
            
            // 返回成功
            return InteractionResult.SUCCESS;
        }
        
        // 如果上方不是空气，无法放置
        return InteractionResult.PASS;
    }
}