package com.bluelockmod.football;

import com.bluelockmod.registry.ModEntities;
import com.bluelockmod.registry.ModItems;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;
import java.util.UUID;

public class FootballEntity extends Entity {
    private static final EntityDataAccessor<Boolean> CONTROLLED = SynchedEntityData.defineId(FootballEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> LAST_TOUCH_TICKS = SynchedEntityData.defineId(FootballEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Optional<UUID>> OWNER = SynchedEntityData.defineId(FootballEntity.class, EntityDataSerializers.OPTIONAL_UUID);
    private static final double MAX_SPEED = 2.25D;

    public FootballEntity(EntityType<? extends FootballEntity> type, Level level) { super(type, level); noPhysics = false; }
    public FootballEntity(Level level, double x, double y, double z) { this(ModEntities.FOOTBALL.get(), level); setPos(x, y, z); }

    @Override protected void defineSynchedData() {
        entityData.define(CONTROLLED, false);
        entityData.define(LAST_TOUCH_TICKS, 0);
        entityData.define(OWNER, Optional.empty());
    }

    @Override protected void readAdditionalSaveData(CompoundTag tag) {
        entityData.set(CONTROLLED, tag.getBoolean("Controlled"));
        entityData.set(LAST_TOUCH_TICKS, tag.getInt("LastTouchTicks"));
        if (tag.hasUUID("Owner")) entityData.set(OWNER, Optional.of(tag.getUUID("Owner")));
    }

    @Override protected void addAdditionalSaveData(CompoundTag tag) {
        tag.putBoolean("Controlled", isControlled());
        tag.putInt("LastTouchTicks", entityData.get(LAST_TOUCH_TICKS));
        owner().ifPresent(uuid -> tag.putUUID("Owner", uuid));
    }

    @Override public void tick() {
        super.tick();
        if (!level().isClientSide) {
            if (isControlled() && owner().isPresent()) {
                Entity holder = level() instanceof ServerLevel serverLevel ? serverLevel.getEntity(owner().get()) : level().getPlayerByUUID(owner().get());
                if (holder != null && holder.isAlive()) {
                    Vec3 target = holder.position().add(holder.getLookAngle().normalize().scale(1.05D)).add(0, 0.20D, 0);
                    setPos(target.x, target.y, target.z);
                    setDeltaMovement(Vec3.ZERO);
                } else clearControl();
            } else {
                Vec3 v = getDeltaMovement();
                if (v.lengthSqr() > MAX_SPEED * MAX_SPEED) setDeltaMovement(v.normalize().scale(MAX_SPEED));
                setDeltaMovement(getDeltaMovement().x * 0.985D, getDeltaMovement().y - 0.035D, getDeltaMovement().z * 0.985D);
                if (onGround()) setDeltaMovement(getDeltaMovement().x, getDeltaMovement().y * -0.35D, getDeltaMovement().z);
            }
            entityData.set(LAST_TOUCH_TICKS, Math.max(0, entityData.get(LAST_TOUCH_TICKS) - 1));
        }
        move(net.minecraft.world.entity.MoverType.SELF, getDeltaMovement());
    }

    public boolean isControlled() { return entityData.get(CONTROLLED); }
    public void setControlled(boolean controlled) { entityData.set(CONTROLLED, controlled); }
    public Optional<UUID> owner() { return entityData.get(OWNER); }
    public void control(UUID uuid) { entityData.set(OWNER, Optional.of(uuid)); entityData.set(CONTROLLED, true); setDeltaMovement(Vec3.ZERO); }
    public void clearControl() { entityData.set(OWNER, Optional.empty()); entityData.set(CONTROLLED, false); }
    public void kick(Vec3 velocity) { clearControl(); setDeltaMovement(velocity); entityData.set(LAST_TOUCH_TICKS, 8); }
    public ItemStack getItem() { return new ItemStack(ModItems.FOOTBALL.get()); }
}
