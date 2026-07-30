// Made with Blockbench 5.1.6
// Exported for Minecraft version 1.17 or later with Mojang mappings
// Paste this class into your mod and generate all required imports


public class model<T extends Entity> extends EntityModel<T> {
	// This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation("modid", "model"), "main");
	private final ModelPart zhuangshi;
	private final ModelPart hair;
	private final ModelPart roof;
	private final ModelPart eye;
	private final ModelPart body;
	private final ModelPart kuangjia;

	public model(ModelPart root) {
		this.zhuangshi = root.getChild("zhuangshi");
		this.hair = this.zhuangshi.getChild("hair");
		this.roof = this.zhuangshi.getChild("roof");
		this.eye = root.getChild("eye");
		this.body = root.getChild("body");
		this.kuangjia = this.body.getChild("kuangjia");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition zhuangshi = partdefinition.addOrReplaceChild("zhuangshi", CubeListBuilder.create(), PartPose.offset(0.5F, 24.0F, -0.5F));

		PartDefinition hair = zhuangshi.addOrReplaceChild("hair", CubeListBuilder.create().texOffs(22, 26).addBox(-0.5F, -9.0F, 0.0F, 1.0F, 2.0F, 0.0F, new CubeDeformation(0.0F))
		.texOffs(8, 35).addBox(-1.5F, -10.0F, 0.0F, 1.0F, 1.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(-0.5F, 0.0F, 0.5F));

		PartDefinition roof = zhuangshi.addOrReplaceChild("roof", CubeListBuilder.create().texOffs(0, 0).addBox(-7.0F, 0.0F, -7.0F, 14.0F, 0.0F, 14.0F, new CubeDeformation(0.0F)), PartPose.offset(-0.5F, 0.0F, 0.5F));

		PartDefinition eye = partdefinition.addOrReplaceChild("eye", CubeListBuilder.create().texOffs(14, 26).addBox(-3.0F, -6.0F, -4.0F, 2.0F, 2.0F, 0.0F, new CubeDeformation(0.0F))
		.texOffs(18, 26).addBox(1.0F, -6.0F, -4.0F, 2.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));

		PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 14).addBox(-3.0F, -7.0F, -3.0F, 6.0F, 6.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));

		PartDefinition kuangjia = body.addOrReplaceChild("kuangjia", CubeListBuilder.create().texOffs(28, 34).addBox(-4.0F, -9.0F, -4.0F, 1.0F, 8.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(0, 26).addBox(3.0F, -2.0F, -3.0F, 1.0F, 1.0F, 6.0F, new CubeDeformation(0.0F))
		.texOffs(0, 35).addBox(3.0F, -9.0F, -4.0F, 1.0F, 8.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(4, 35).addBox(3.0F, -9.0F, 3.0F, 1.0F, 8.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(14, 28).addBox(3.0F, -9.0F, -3.0F, 1.0F, 1.0F, 6.0F, new CubeDeformation(0.0F))
		.texOffs(32, 34).addBox(-4.0F, -9.0F, 3.0F, 1.0F, 8.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(28, 28).addBox(-3.0F, -2.0F, -4.0F, 6.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(28, 30).addBox(-3.0F, -2.0F, 3.0F, 6.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(28, 32).addBox(-3.0F, -9.0F, -4.0F, 6.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(0, 33).addBox(-3.0F, -9.0F, 3.0F, 6.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(24, 14).addBox(-4.0F, -2.0F, -3.0F, 1.0F, 1.0F, 6.0F, new CubeDeformation(0.0F))
		.texOffs(24, 21).addBox(-4.0F, -9.0F, -3.0F, 1.0F, 1.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 1.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 64, 64);
	}

	@Override
	public void setupAnim(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		zhuangshi.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		eye.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		body.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}
}