package com.xigua.xiguaworld.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.xigua.xiguaworld.entity.custom.MercuryxiguaCreature;
import com.xigua.xiguaworld.xiguaworld;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

/**
 * MercuryxiguaCreature 实体渲染器
 * 负责渲染实体的模型和纹理
 */
public class MercuryxiguaCreatureRenderer extends MobRenderer<MercuryxiguaCreature, MercuryxiguaCreatureModel> {
    
    /**
     * 实体纹理位置
     * 纹理文件: src/main/resources/assets/xigua_world/textures/entity/mercury_xigua_creature.png
     */
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(
            xiguaworld.MOD_ID, "textures/entity/mercury_xigua_creature.png");
    
    /**
     * 构造函数
     * 
     * @param context 渲染器提供者上下文
     */
    public MercuryxiguaCreatureRenderer(EntityRendererProvider.Context context) {
        super(context, new MercuryxiguaCreatureModel(context.bakeLayer(ModModelLayers.MERCURYXIGUA_CREATURE)), 0.5f);
    }
    
    /**
     * 获取实体纹理
     */
    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull MercuryxiguaCreature entity) {
        return TEXTURE;
    }
}

/**
 * MercuryxiguaCreature 实体模型
 * 对应 geo/mercury_xigua_creature.java 中的模型结构
 * 
 * 注意：此类与渲染器在同一文件中，因此不能是 public（Java单文件单public类规则）
 * 但由于在同一包下，ModEntityRenderer 仍然可以访问它
 * 
 * 模型部件：
 * - zhuangshi (装饰) - 根部件 (Y=24)
 *   - hair (头发)
 *   - roof (顶部/屋顶)
 * - eye (眼睛) - Y=24
 * - body (身体) - Y=24
 *   - kuangjia (框架) - 相对于body Y=1
 * 
 * 动画系统：
 * 1. wait 动画 - 待机动画（检测到玩家前播放）
 * 2. look 动画 - 观察四周动画（触发观察行为时播放）
 * 3. 检测到玩家时 - 停止所有动画，模型转向玩家
 */
class MercuryxiguaCreatureModel extends HierarchicalModel<MercuryxiguaCreature> {
    
    // 模型部件 - 与动画文件中的部件名称对应
    private final ModelPart root;
    private final ModelPart zhuangshi;  // 装饰（根部件）
    private final ModelPart hair;       // 头发
    private final ModelPart roof;       // 顶部
    private final ModelPart eye;        // 眼睛
    private final ModelPart body;       // 身体
    private final ModelPart kuangjia;   // 框架
    
    // 动画状态
    private float headYRot = 0.0F;
    private float headXRot = 0.0F;
    
    /**
     * 模型层位置
     */
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
            ResourceLocation.fromNamespaceAndPath(xiguaworld.MOD_ID, "mercuryxigua_creature"),
            "main");
    
    /**
     * 构造函数
     * 
     * @param root 根模型部件
     */
    public MercuryxiguaCreatureModel(ModelPart root) {
        this.root = root;
        this.zhuangshi = root.getChild("zhuangshi");
        this.hair = this.zhuangshi.getChild("hair");
        this.roof = this.zhuangshi.getChild("roof");
        this.eye = this.zhuangshi.getChild("eye");
        this.body = this.zhuangshi.getChild("body");
        this.kuangjia = this.zhuangshi.getChild("kuangjia");
    }
    
    /**
     * 创建模型层定义
     * 对应 geo/mercury_xigua_creature.java 中的 createBodyLayer()
     */
    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        // zhuangshi (装饰) - 根部件，无立方体
        PartDefinition zhuangshi = partdefinition.addOrReplaceChild("zhuangshi", 
                CubeListBuilder.create(), 
                PartPose.offset(0.5F, 24.0F, -0.5F));

        // hair (头发) - 装饰部件，向下移动一格
        PartDefinition hair = zhuangshi.addOrReplaceChild("hair", 
                CubeListBuilder.create()
                        .texOffs(22, 26).addBox(-0.5F, -9.0F, 0.0F, 1.0F, 2.0F, 0.0F, new CubeDeformation(0.0F))
                        .texOffs(8, 35).addBox(-1.5F, -10.0F, 0.0F, 1.0F, 1.0F, 0.0F, new CubeDeformation(0.0F)), 
                PartPose.offset(-0.5F, 0.0F, 0.5F));

        // roof (顶部) - 西瓜的顶部
        PartDefinition roof = zhuangshi.addOrReplaceChild("roof", 
                CubeListBuilder.create().texOffs(0, 0).addBox(-7.0F, 0.0F, -7.0F, 14.0F, 0.0F, 14.0F, new CubeDeformation(0.0F)), 
                PartPose.offset(-0.5F, 0.0F, 0.5F));

        // eye (眼睛) - 眼睛部件，作为 zhuangshi 的子部件
        PartDefinition eye = zhuangshi.addOrReplaceChild("eye", 
                CubeListBuilder.create()
                        .texOffs(14, 26).addBox(-3.0F, -6.0F, -4.0F, 2.0F, 2.0F, 0.0F, new CubeDeformation(0.0F))
                        .texOffs(18, 26).addBox(1.0F, -6.0F, -4.0F, 2.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)), 
                PartPose.offset(0.0F, 0.0F, 0.0F));

        // body (身体) - 西瓜主体，作为 zhuangshi 的子部件，向下移动一格
        PartDefinition body = zhuangshi.addOrReplaceChild("body", 
                CubeListBuilder.create().texOffs(0, 14).addBox(-3.0F, -7.0F, -3.0F, 6.0F, 6.0F, 6.0F, new CubeDeformation(0.0F)), 
                PartPose.offset(0.0F, 0.0F, 0.0F));

        // kuangjia (框架) - 作为 zhuangshi 的子部件，下降一格（通过调整 addBox Y 偏移）
        PartDefinition kuangjia = zhuangshi.addOrReplaceChild("kuangjia", 
                CubeListBuilder.create()
                        .texOffs(28, 34).addBox(-4.0F, -8.0F, -4.0F, 1.0F, 8.0F, 1.0F, new CubeDeformation(0.0F))
                        .texOffs(0, 26).addBox(3.0F, -1.0F, -3.0F, 1.0F, 1.0F, 6.0F, new CubeDeformation(0.0F))
                        .texOffs(0, 35).addBox(3.0F, -8.0F, -4.0F, 1.0F, 8.0F, 1.0F, new CubeDeformation(0.0F))
                        .texOffs(4, 35).addBox(3.0F, -8.0F, 3.0F, 1.0F, 8.0F, 1.0F, new CubeDeformation(0.0F))
                        .texOffs(14, 28).addBox(3.0F, -8.0F, -3.0F, 1.0F, 1.0F, 6.0F, new CubeDeformation(0.0F))
                        .texOffs(32, 34).addBox(-4.0F, -8.0F, 3.0F, 1.0F, 8.0F, 1.0F, new CubeDeformation(0.0F))
                        .texOffs(28, 28).addBox(-3.0F, -1.0F, -4.0F, 6.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                        .texOffs(28, 30).addBox(-3.0F, -1.0F, 3.0F, 6.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                        .texOffs(28, 32).addBox(-3.0F, -8.0F, -4.0F, 6.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                        .texOffs(0, 33).addBox(-3.0F, -8.0F, 3.0F, 6.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                        .texOffs(24, 14).addBox(-4.0F, -1.0F, -3.0F, 1.0F, 1.0F, 6.0F, new CubeDeformation(0.0F))
                        .texOffs(24, 21).addBox(-4.0F, -8.0F, -3.0F, 1.0F, 1.0F, 6.0F, new CubeDeformation(0.0F)), 
                PartPose.offset(0.0F, 0.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }
    
    /**
     * 设置动画
     * 这是动画系统的核心方法，每一帧都会被调用
     * 
     * 动画调用逻辑：
     * 1. 未检测到玩家时 → 播放 wait 动画（待机呼吸）
     * 2. 触发观察四周行为时 → 播放 look 动画（环顾旋转）
     * 3. 检测到玩家时（LookAtPlayerGoal触发）→ 停止所有动画，模型转向玩家
     * 
     * @param entity 实体
     * @param limbSwing 肢体摆动（移动距离）
     * @param limbSwingAmount 肢体摆动幅度（移动速度）
     * @param ageInTicks 实体存在时间（tick数）
     * @param netHeadYaw 头部水平旋转角度
     * @param headPitch 头部垂直旋转角度
     */
    @Override
    public void setupAnim(MercuryxiguaCreature entity, float limbSwing, float limbSwingAmount, 
                          float ageInTicks, float netHeadYaw, float headPitch) {
        
        // 将角度转换为弧度
        float headYRotRad = netHeadYaw * ((float)Math.PI / 180F);
        float headXRotRad = headPitch * ((float)Math.PI / 180F);
        
        // ==================== 状态判断 ====================
        // 如果 netHeadYaw 或 headPitch 不为0，说明AI目标正在让实体看向某个方向
        // 这可能是 LookAtPlayerGoal（看向玩家）或 RandomLookAroundGoal（随机环顾）
        
        boolean isLookingAtPlayer = (headYRotRad != 0.0F || headXRotRad != 0.0F);
        
        if (isLookingAtPlayer) {
            // ==================== 检测到玩家 ====================
            // 停止所有动画，模型转向玩家
            
            // body 只能左右转动（yRot），不能上下转动（xRot）
            this.body.yRot = headYRotRad;
            this.body.xRot = 0.0F;
            this.body.y = 0.0F;
            this.body.yScale = 1.0F;
            
            // kuangjia 跟随 body 一起左右转动
            this.kuangjia.yRot = headYRotRad;
            this.kuangjia.xRot = 0.0F;
            this.kuangjia.y = 0.0F;
            
            // roof 跟随 body 一起左右转动
            this.roof.yRot = headYRotRad;
            this.roof.xRot = 0.0F;
            this.roof.y = 0.0F;
            
            // eye 也转向玩家（但保持在自己的位置）
            this.eye.yRot = headYRotRad;
            this.eye.xRot = 0.0F;
            this.eye.y = 0.0F;
            
            // 其他部件保持默认状态
            this.zhuangshi.yScale = 1.0F;
            this.hair.y = 0.0F;
            this.hair.yRot = headYRotRad;
            
        } else if (limbSwingAmount == 0.0F) {
            // ==================== wait 动画（待机呼吸） ====================
            // 未检测到玩家，且实体未移动时播放
            // 对应 animations/mercury_xigua_creature_wait.java 中的 wait 动画
            // 
            // 动画内容：
            // - eye: Y轴位置上下移动（0→1→0），长度2.0F
            // - body: Y轴缩放变化（1.0→1.1→1.0），长度2.0F
            // - hair: Y轴位置上下移动（0→1→0），长度2.0F
            
            // 计算动画进度 (0.0 到 2.0 循环)
            float animProgress = ageInTicks % 2.0F;
            
            // 使用线性插值模拟动画
            // 0.0F → 1.0F: 从0上升到1
            // 1.0F → 2.0F: 从1下降到0
            float t = animProgress <= 1.0F ? animProgress : 2.0F - animProgress;
            
            // eye 动画：Y轴位置上下移动（相对于模型定义的初始位置）
            this.eye.y = t;
            this.eye.yRot = 0.0F;
            this.eye.xRot = 0.0F;
            
            // body 动画：Y轴缩放变化
            float bodyScaleY = 1.0F + t * 0.1F;
            this.body.yScale = bodyScaleY;
            this.body.xRot = 0.0F;
            this.body.yRot = 0.0F;
            this.body.y = 0.0F;
            
            // kuangjia 跟随 body 动画
            this.kuangjia.yRot = 0.0F;
            this.kuangjia.xRot = 0.0F;
            this.kuangjia.y = 0.0F;
            
            // hair 动画：Y轴位置上下移动
            this.hair.y = t;
            this.hair.yRot = 0.0F;
            
            // roof 保持不动
            this.roof.y = 0.0F;
            this.roof.yRot = 0.0F;
            this.roof.xRot = 0.0F;
            
            // kuangjia 保持不动
            this.kuangjia.yRot = 0.0F;
            this.kuangjia.xRot = 0.0F;
            this.kuangjia.y = 0.0F;
            
        } else {
            // ==================== look 动画（观察四周） ====================
            // 触发观察四周行为时播放
            // 对应 animations/mercury_xigua_creature_look.java 中的 look 动画
            //
            // 动画内容：
            // - eye: 旋转（0→12.5°→-12.5°→0）+ Y轴位置跳动，长度2.5F
            // - body: 旋转（0→12.5°→-12.5°→0）+ Y轴位置跳动，长度2.5F
            // - hair: 旋转（0→12.5°→-12.5°→0）+ Y轴位置跳动，长度2.5F
            // - roof: Y轴位置跳动，长度2.5F
            
            // 计算动画进度 (0.0 到 2.5)
            float animProgress = ageInTicks % 2.5F;
            
            // 旋转动画关键帧：
            // 0.0F: 0°
            // 0.25F: 12.5°
            // 1.0F: 12.5°
            // 1.25F: -12.5°
            // 2.25F: -12.5°
            // 2.5F: 0°
            float maxRotation = 12.5F * ((float)Math.PI / 180F);
            float rotation;
            
            if (animProgress <= 0.25F) {
                // 0.0F → 0.25F: 0° → 12.5°
                rotation = (animProgress / 0.25F) * maxRotation;
            } else if (animProgress <= 1.0F) {
                // 0.25F → 1.0F: 保持 12.5°
                rotation = maxRotation;
            } else if (animProgress <= 1.25F) {
                // 1.0F → 1.25F: 12.5° → -12.5°
                rotation = maxRotation - ((animProgress - 1.0F) / 0.25F) * (maxRotation * 2);
            } else if (animProgress <= 2.25F) {
                // 1.25F → 2.25F: 保持 -12.5°
                rotation = -maxRotation;
            } else {
                // 2.25F → 2.5F: -12.5° → 0°
                rotation = -maxRotation + ((animProgress - 2.25F) / 0.25F) * maxRotation;
            }
            
            // eye 旋转
            this.eye.yRot = rotation;
            
            // body 旋转
            this.body.yRot = rotation;
            this.body.xRot = 0.0F;
            
            // kuangjia 跟随 body 一起旋转
            this.kuangjia.yRot = rotation;
            this.kuangjia.xRot = 0.0F;
            
            // hair 旋转
            this.hair.yRot = rotation;
            
            // Y轴位置跳动动画关键帧：
            // 0.0F: 0
            // 0.125F: 2
            // 0.25F: 0
            // 1.0F: 0
            // 1.125F: 2
            // 1.25F: 0
            // 2.25F: 0
            // 2.375F: 2
            // 2.5F: 0
            float jump;
            if (animProgress <= 0.125F) {
                jump = (animProgress / 0.125F) * 2.0F;
            } else if (animProgress <= 0.25F) {
                jump = 2.0F - ((animProgress - 0.125F) / 0.125F) * 2.0F;
            } else if (animProgress <= 1.125F) {
                jump = 0.0F;
            } else if (animProgress <= 1.25F) {
                jump = ((animProgress - 1.125F) / 0.125F) * 2.0F;
            } else if (animProgress <= 2.375F) {
                jump = 0.0F;
            } else {
                jump = ((animProgress - 2.375F) / 0.125F) * 2.0F;
            }
            
            // 应用Y轴跳动
            this.eye.y = jump;
            this.body.y = -1.0F + jump;  // 在 PartPose 初始偏移基础上添加动画
            this.hair.y = -1.0F + jump;  // 在 PartPose 初始偏移基础上添加动画
            this.roof.y = jump;
            this.kuangjia.y = jump;
            
            // body 缩放重置
            this.body.yScale = 1.0F;
        }
    }
    
    /**
     * 获取根部件
     * HierarchicalModel 要求实现此方法
     */
    @Override
    public @NotNull ModelPart root() {
        return this.root;
    }
    
    /**
     * 将模型部件渲染到屏幕
     * 1.21.1 中正确的 renderToBuffer 方法签名
     */
    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, 
                               int packedLight, int packedOverlay, int packedColor) {
        // 只渲染 zhuangshi，它会自动渲染所有子部件（eye、body、hair、roof、kuangjia）
        this.zhuangshi.render(poseStack, vertexConsumer, packedLight, packedOverlay, packedColor);
    }
}

/**
 * 模型层注册类
 */
class ModModelLayers {
    public static final ModelLayerLocation MERCURYXIGUA_CREATURE = MercuryxiguaCreatureModel.LAYER_LOCATION;
}