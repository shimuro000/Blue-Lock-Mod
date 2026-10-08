package com.bluelockmod.client;

import com.bluelockmod.ai.FootballAIEntity;
import com.bluelockmod.animation.FootballAnimation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Advanced client presentation layer for football movement.
 *
 * The server still owns action triggers. Continuous locomotion is derived locally
 * from replicated entity movement, so it does not require per-frame packets.
 */
@Mod.EventBusSubscriber(modid = "bluelockmod", value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class ClientAnimationManager {
    private static final Map<UUID, State> STATES = new ConcurrentHashMap<>();
    private static final Map<UUID, MotionSample> MOTION = new ConcurrentHashMap<>();
    private static final ThreadLocal<Deque<PoseSnapshot>> POSES = ThreadLocal.withInitial(ArrayDeque::new);

    private ClientAnimationManager() {}

    public static void play(UUID entityId, FootballAnimation animation, int duration, float intensity) {
        STATES.put(entityId, new State(animation, duration, intensity));
    }

    @SubscribeEvent
    public static void clientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (Minecraft.getInstance().level == null) {
            STATES.clear();
            MOTION.clear();
            return;
        }
        STATES.entrySet().removeIf(entry -> {
            entry.getValue().age++;
            return entry.getValue().age >= entry.getValue().duration;
        });
        if (STATES.size() > 512) STATES.clear();
        if (MOTION.size() > 512) MOTION.clear();
    }

    @SubscribeEvent
    public static void playerPre(RenderPlayerEvent.Pre event) {
        if (!(event.getEntity() instanceof AbstractClientPlayer player)) return;
        HumanoidModel<?> model = event.getRenderer().getModel();
        PoseSnapshot snapshot = PoseSnapshot.capture(model);
        POSES.get().push(snapshot);
        applyAdvanced(model, player, event.getPartialTick());
    }

    @SubscribeEvent
    public static void playerPost(RenderPlayerEvent.Post event) {
        restore();
    }

    @SubscribeEvent
    public static void livingPre(RenderLivingEvent.Pre<?, ?> event) {
        if (!(event.getEntity() instanceof FootballAIEntity ai)) return;
        if (!(event.getRenderer().getModel() instanceof HumanoidModel<?> model)) return;
        POSES.get().push(PoseSnapshot.capture(model));
        applyAdvanced(model, ai, event.getPartialTick());
    }

    @SubscribeEvent
    public static void livingPost(RenderLivingEvent.Post<?, ?> event) {
        if (event.getEntity() instanceof FootballAIEntity) restore();
    }

    private static void applyAdvanced(HumanoidModel<?> model, LivingEntity entity, float partialTick) {
        float motionBlend = updateMotion(entity, partialTick);
        applyLocomotion(model, entity, partialTick, motionBlend);

        State state = STATES.get(entity.getUUID());
        if (state != null) applyAction(model, state);
    }

    private static float updateMotion(LivingEntity entity, float partialTick) {
        Vec3 velocity = entity.getDeltaMovement();
        double horizontal = Math.sqrt(velocity.x * velocity.x + velocity.z * velocity.z);
        float speed = Mth.clamp((float) (horizontal / 0.23D), 0.0F, 1.65F);
        if (entity.isSprinting()) speed = Math.max(speed, 1.0F);
        if (entity.isCrouching()) speed *= 0.62F;

        MotionSample sample = MOTION.computeIfAbsent(entity.getUUID(), id -> new MotionSample());
        sample.speed = Mth.lerp(0.35F, sample.speed, speed);
        sample.yawDelta = Mth.lerp(0.30F, sample.yawDelta, Mth.wrapDegrees(entity.getYRot() - entity.yRotO));
        sample.phase += (0.20F + sample.speed * 0.95F) * (entity.isSprinting() ? 1.18F : 1.0F);
        if (sample.phase > Mth.TWO_PI) sample.phase -= Mth.TWO_PI;
        return Mth.clamp(sample.speed, 0.0F, 1.35F);
    }

    private static void applyLocomotion(HumanoidModel<?> model, LivingEntity entity, float partialTick, float blend) {
        MotionSample sample = MOTION.get(entity.getUUID());
        if (sample == null) return;

        float phase = sample.phase + partialTick * (0.16F + blend * 0.75F);
        float walkWave = Mth.sin(phase);
        float oppositeWave = Mth.sin(phase + Mth.PI);
        float locomotion = Mth.clamp(blend, 0.0F, 1.0F);
        float run = Mth.clamp((blend - 0.45F) / 0.75F, 0.0F, 1.0F);
        float sprint = entity.isSprinting() ? 1.0F : 0.0F;

        // Natural idle breathing, kept subtle so it does not fight vanilla idle motion.
        float breathing = Mth.sin((entity.tickCount + partialTick) * 0.09F) * 0.018F * (1.0F - locomotion);
        model.body.xRot += breathing;
        model.head.xRot += breathing * 0.35F;

        // Forward locomotion: legs and opposite arm swing.
        model.rightLeg.xRot += oppositeWave * 0.48F * locomotion + oppositeWave * 0.16F * run;
        model.leftLeg.xRot += walkWave * 0.48F * locomotion + walkWave * 0.16F * run;
        model.rightArm.xRot += walkWave * 0.24F * locomotion + walkWave * 0.28F * run;
        model.leftArm.xRot += oppositeWave * 0.24F * locomotion + oppositeWave * 0.28F * run;

        // Sprint posture and longer stride.
        model.body.xRot += -0.10F * sprint * run;
        model.rightLeg.xRot += oppositeWave * 0.16F * sprint * run;
        model.leftLeg.xRot += walkWave * 0.16F * sprint * run;
        model.rightArm.xRot += walkWave * 0.10F * sprint * run;
        model.leftArm.xRot += oppositeWave * 0.10F * sprint * run;

        // Strafing and direction changes. Local right vector is derived from yaw.
        Vec3 velocity = entity.getDeltaMovement();
        float yaw = entity.getYRot() * Mth.DEG_TO_RAD;
        float localForward = (float) (velocity.x * -Mth.sin(yaw) + velocity.z * Mth.cos(yaw));
        float localStrafe = (float) (velocity.x * Mth.cos(yaw) + velocity.z * Mth.sin(yaw));
        float strafe = Mth.clamp(localStrafe * 4.5F, -1.0F, 1.0F);
        float forward = Mth.clamp(localForward * 4.5F, -1.0F, 1.0F);
        model.body.yRot += strafe * 0.11F * locomotion;
        model.rightArm.zRot += strafe * -0.10F * locomotion;
        model.leftArm.zRot += strafe * 0.10F * locomotion;
        model.rightLeg.yRot += strafe * 0.10F * locomotion;
        model.leftLeg.yRot += strafe * -0.10F * locomotion;

        // Braking / acceleration lean based on forward velocity.
        model.body.xRot += -forward * 0.055F * locomotion;

        // Quick turns get a short counter-rotation instead of a stiff snap.
        float turn = Mth.clamp(sample.yawDelta / 28.0F, -1.0F, 1.0F);
        model.body.yRot += turn * 0.14F * (0.35F + locomotion * 0.65F);
        model.head.yRot += turn * 0.05F;

        // Football dribble gait: alternating shoulder/hip movement while carrying the ball.
        State state = STATES.get(entity.getUUID());
        if (state != null && state.animation == FootballAnimation.DRIBBLE) {
            float dribble = Mth.sin(phase * 1.55F);
            model.body.yRot += dribble * 0.10F;
            model.rightLeg.xRot += dribble * 0.13F;
            model.leftLeg.xRot -= dribble * 0.13F;
            model.rightArm.zRot -= dribble * 0.07F;
            model.leftArm.zRot += dribble * 0.07F;
        }
    }

    private static void applyAction(HumanoidModel<?> model, State state) {
        float t = state.progress();
        float pulse = easeInOut(Mth.sin(t * Mth.PI));
        float wave = Mth.sin(t * Mth.TWO_PI);
        float intensity = state.intensity;

        switch (state.animation) {
            case CONTROL -> {
                float settle = Mth.sin(t * Mth.PI);
                model.body.xRot += -0.13F * settle * intensity;
                model.rightArm.xRot += -0.40F * settle * intensity;
                model.leftArm.xRot += -0.26F * settle * intensity;
                model.rightLeg.xRot += 0.22F * settle * intensity;
                model.leftLeg.xRot -= 0.10F * settle * intensity;
            }
            case PASS -> {
                float windup = phaseCurve(t, 0.0F, 0.45F);
                float follow = phaseCurve(t, 0.35F, 1.0F);
                model.body.yRot += 0.25F * pulse * intensity;
                model.rightArm.xRot += (-0.78F * windup + 0.55F * follow) * intensity;
                model.rightArm.zRot += -0.18F * pulse * intensity;
                model.leftArm.xRot += 0.22F * pulse * intensity;
                model.rightLeg.xRot += (0.48F * windup - 0.82F * follow) * intensity;
                model.leftLeg.xRot += (-0.18F * windup + 0.25F * follow) * intensity;
            }
            case SHOOT -> {
                float windup = phaseCurve(t, 0.0F, 0.52F);
                float strike = phaseCurve(t, 0.35F, 0.78F);
                float follow = phaseCurve(t, 0.62F, 1.0F);
                model.body.xRot += (0.08F * windup - 0.15F * follow) * intensity;
                model.body.yRot += 0.12F * pulse * intensity;
                model.rightArm.xRot += -0.62F * windup + 0.72F * follow;
                model.leftArm.xRot += 0.30F * windup - 0.20F * follow;
                model.rightLeg.xRot += (-0.35F * windup - 1.12F * strike + 0.75F * follow) * intensity;
                model.leftLeg.xRot += (0.30F * windup + 0.18F * strike - 0.22F * follow) * intensity;
            }
            case DRIBBLE -> {
                model.body.yRot += 0.10F * wave * intensity;
                model.rightArm.xRot += -0.28F * wave * intensity;
                model.leftArm.xRot += 0.28F * wave * intensity;
            }
            case SKILL -> {
                model.body.yRot += 0.34F * wave * intensity;
                model.body.xRot += -0.06F * pulse * intensity;
                model.rightArm.xRot += -0.78F * pulse * intensity;
                model.leftArm.xRot += -0.52F * pulse * intensity;
                model.rightLeg.xRot += 0.30F * wave * intensity;
                model.leftLeg.xRot -= 0.30F * wave * intensity;
            }
            case FLOW -> {
                float waveIn = Mth.sin(t * Mth.PI);
                model.body.xRot += -0.10F * waveIn * intensity;
                model.body.yRot += 0.12F * wave * intensity;
                model.rightArm.xRot += -0.42F * waveIn * intensity;
                model.leftArm.xRot += -0.42F * waveIn * intensity;
                model.rightArm.zRot += -0.18F * waveIn * intensity;
                model.leftArm.zRot += 0.18F * waveIn * intensity;
            }
            case AWAKENING -> {
                float burst = Mth.sin(t * Mth.PI);
                model.body.xRot += -0.16F * burst * intensity;
                model.body.yRot += 0.20F * wave * intensity;
                model.rightArm.xRot += -0.68F * burst * intensity;
                model.leftArm.xRot += -0.68F * burst * intensity;
                model.rightArm.zRot += -0.24F * burst * intensity;
                model.leftArm.zRot += 0.24F * burst * intensity;
                model.rightLeg.xRot += 0.20F * wave * intensity;
                model.leftLeg.xRot -= 0.20F * wave * intensity;
            }
        }
    }

    private static float phaseCurve(float t, float start, float end) {
        if (t <= start) return 0.0F;
        if (t >= end) return 1.0F;
        float x = (t - start) / Math.max(0.001F, end - start);
        return easeInOut(x);
    }

    private static float easeInOut(float x) {
        x = Mth.clamp(x, 0.0F, 1.0F);
        return x * x * (3.0F - 2.0F * x);
    }

    private static void restore() {
        Deque<PoseSnapshot> stack = POSES.get();
        if (!stack.isEmpty()) stack.pop().restore();
    }

    private static final class State {
        private final FootballAnimation animation;
        private final int duration;
        private final float intensity;
        private int age;

        private State(FootballAnimation animation, int duration, float intensity) {
            this.animation = animation;
            this.duration = Math.max(1, duration);
            this.intensity = intensity;
        }

        private float progress() {
            return Mth.clamp((age + 0.5F) / duration, 0.0F, 1.0F);
        }
    }

    private static final class MotionSample {
        private float speed;
        private float yawDelta;
        private float phase;
    }

    private static final class PoseSnapshot {
        private final Map<ModelPart, float[]> parts = new IdentityHashMap<>();

        private static PoseSnapshot capture(HumanoidModel<?> model) {
            PoseSnapshot s = new PoseSnapshot();
            capture(s, model.head);
            capture(s, model.body);
            capture(s, model.rightArm);
            capture(s, model.leftArm);
            capture(s, model.rightLeg);
            capture(s, model.leftLeg);
            return s;
        }

        private static void capture(PoseSnapshot s, ModelPart part) {
            s.parts.put(part, new float[]{part.xRot, part.yRot, part.zRot});
        }

        private void restore() {
            parts.forEach((part, r) -> {
                part.xRot = r[0];
                part.yRot = r[1];
                part.zRot = r[2];
            });
        }
    }
}
