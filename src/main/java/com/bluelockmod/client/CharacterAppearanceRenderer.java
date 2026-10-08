package com.bluelockmod.client;

import com.bluelockmod.BlueLockMod;
import com.bluelockmod.player.BodyBuild;
import com.bluelockmod.player.HairStyle;
import com.bluelockmod.player.PlayerProfileProvider;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = BlueLockMod.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class CharacterAppearanceRenderer {
    private static final ResourceLocation HAIR_TEXTURE = new ResourceLocation(BlueLockMod.MOD_ID, "textures/entity/hair.png");
    private static final ModelPart[] HAIR = buildHair();
    private static final ModelPart EYES = buildEyes();
    private CharacterAppearanceRenderer() {}

    private static ModelPart buildEyes() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        root.addOrReplaceChild("left", CubeListBuilder.create().texOffs(0, 0).addBox(-2.7F, -4.5F, -4.15F, 1.4F, 1.2F, 0.35F), PartPose.ZERO);
        root.addOrReplaceChild("right", CubeListBuilder.create().texOffs(0, 0).addBox(1.3F, -4.5F, -4.15F, 1.4F, 1.2F, 0.35F), PartPose.ZERO);
        return LayerDefinition.create(mesh, 8, 8).bakeRoot();
    }

    private static ModelPart[] buildHair() {
        ModelPart[] result = new ModelPart[HairStyle.values().length];
        for (HairStyle style : HairStyle.values()) {
            MeshDefinition mesh = new MeshDefinition();
            PartDefinition root = mesh.getRoot();
            switch (style) {
                case SHORT -> root.addOrReplaceChild("hair", CubeListBuilder.create().texOffs(0, 0).addBox(-4.5F, -9.2F, -4.5F, 9F, 2.5F, 9F), PartPose.ZERO);
                case SPIKY -> {
                    root.addOrReplaceChild("top", CubeListBuilder.create().texOffs(0, 0).addBox(-4.5F, -9.3F, -4.5F, 9F, 2F, 9F), PartPose.ZERO);
                    root.addOrReplaceChild("spike1", CubeListBuilder.create().texOffs(0, 12).addBox(-3.5F, -11F, -2F, 3F, 3F, 3F), PartPose.ZERO);
                    root.addOrReplaceChild("spike2", CubeListBuilder.create().texOffs(0, 12).addBox(0.5F, -11.5F, -2F, 3F, 3.5F, 3F), PartPose.ZERO);
                }
                case LONG -> {
                    root.addOrReplaceChild("top", CubeListBuilder.create().texOffs(0, 0).addBox(-4.5F, -9.3F, -4.5F, 9F, 2.5F, 9F), PartPose.ZERO);
                    root.addOrReplaceChild("left", CubeListBuilder.create().texOffs(24, 0).addBox(-5F, -7.5F, -3.5F, 2F, 9F, 7F), PartPose.ZERO);
                    root.addOrReplaceChild("right", CubeListBuilder.create().texOffs(24, 16).addBox(3F, -7.5F, -3.5F, 2F, 9F, 7F), PartPose.ZERO);
                }
                case MOHAWK -> {
                    root.addOrReplaceChild("strip", CubeListBuilder.create().texOffs(0, 0).addBox(-1.5F, -11F, -4F, 3F, 4F, 8F), PartPose.ZERO);
                    root.addOrReplaceChild("front", CubeListBuilder.create().texOffs(0, 12).addBox(-1.5F, -9F, -5F, 3F, 2F, 3F), PartPose.ZERO);
                }
                case UNDERCUT -> root.addOrReplaceChild("top", CubeListBuilder.create().texOffs(0, 0).addBox(-3.8F, -10F, -4F, 7.6F, 2.5F, 7.5F), PartPose.ZERO);
                case WAVE -> {
                    root.addOrReplaceChild("top", CubeListBuilder.create().texOffs(0, 0).addBox(-4.7F, -9.4F, -4.2F, 9.4F, 3F, 8.5F), PartPose.ZERO);
                    root.addOrReplaceChild("front", CubeListBuilder.create().texOffs(0, 12).addBox(-4F, -8F, -5F, 8F, 2.5F, 2F), PartPose.ZERO);
                }
                case AFRO -> root.addOrReplaceChild("afro", CubeListBuilder.create().texOffs(0, 0).addBox(-5.3F, -10.3F, -5.3F, 10.6F, 7F, 10.6F), PartPose.ZERO);
            }
            result[style.ordinal()] = LayerDefinition.create(mesh, 64, 32).bakeRoot();
        }
        return result;
    }

    @SubscribeEvent
    public static void scaleBody(RenderPlayerEvent.Pre event) {
        if (!(event.getEntity() instanceof AbstractClientPlayer player)) return;
        player.getCapability(PlayerProfileProvider.CAPABILITY).ifPresent(profile -> {
            BodyBuild build = profile.bodyBuild();
            event.getPoseStack().scale(build.widthScale(), build.heightScale(), build.widthScale());
        });
    }

    @SubscribeEvent
    public static void render(RenderPlayerEvent.Post event) {
        if (!(event.getEntity() instanceof AbstractClientPlayer player)) return;
        player.getCapability(PlayerProfileProvider.CAPABILITY).ifPresent(profile -> {
            PoseStack pose = event.getPoseStack();
            PlayerRenderer renderer = event.getRenderer();
            pose.pushPose();
            renderer.getModel().head.translateAndRotate(pose);
            ModelPart hair = HAIR[profile.hairStyle().ordinal()];
            MultiBufferSource buffers = event.getMultiBufferSource();
            VertexConsumer consumer = buffers.getBuffer(RenderType.entityCutoutNoCull(HAIR_TEXTURE));
            int color = profile.hairColor();
            hair.render(pose, consumer, event.getPackedLight(), net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY,
                    ((color >> 16) & 255) / 255F, ((color >> 8) & 255) / 255F, (color & 255) / 255F, 1.0F);
            int eye = profile.eyeColor();
            EYES.render(pose, consumer, event.getPackedLight(), net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY,
                    ((eye >> 16) & 255) / 255F, ((eye >> 8) & 255) / 255F, (eye & 255) / 255F, 1.0F);
            pose.popPose();
        });
    }

}
