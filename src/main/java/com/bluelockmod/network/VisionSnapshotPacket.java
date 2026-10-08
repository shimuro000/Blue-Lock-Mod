package com.bluelockmod.network;

import com.bluelockmod.client.ClientVisionState;
import com.bluelockmod.vision.VisionLevel;
import com.bluelockmod.vision.VisionTarget;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

public final class VisionSnapshotPacket {
    private final VisionLevel level;
    private final boolean spatialRead;
    private final List<VisionTarget> targets;
    public VisionSnapshotPacket(VisionLevel level, boolean spatialRead, List<VisionTarget> targets) { this.level = level; this.spatialRead = spatialRead; this.targets = List.copyOf(targets); }
    public void encode(FriendlyByteBuf buf) {
        buf.writeEnum(level); buf.writeBoolean(spatialRead); buf.writeVarInt(targets.size());
        for (VisionTarget target : targets) {
            buf.writeUUID(target.id()); buf.writeUtf(target.name(), 32); buf.writeDouble(target.distance()); buf.writeDouble(target.bearing());
            buf.writeBoolean(target.teammate()); buf.writeBoolean(target.predicted()); buf.writeBoolean(target.threat()); buf.writeDouble(target.projectedDistance()); buf.writeDouble(target.projectedBearing());
        }
    }
    public static VisionSnapshotPacket decode(FriendlyByteBuf buf) {
        VisionLevel level = buf.readEnum(VisionLevel.class); boolean spatialRead = buf.readBoolean(); int count = Math.min(20, buf.readVarInt());
        List<VisionTarget> targets = new ArrayList<>();
        for (int i=0;i<count;i++) targets.add(new VisionTarget(buf.readUUID(), buf.readUtf(32), buf.readDouble(), buf.readDouble(), buf.readBoolean(), buf.readBoolean(), buf.readBoolean(), buf.readDouble(), buf.readDouble()));
        return new VisionSnapshotPacket(level, spatialRead, targets);
    }
    public static void handle(VisionSnapshotPacket packet, Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get(); ctx.enqueueWork(() -> ClientVisionState.set(packet.level, packet.spatialRead, packet.targets)); ctx.setPacketHandled(true);
    }
    public VisionLevel level() { return level; }
    public boolean spatialRead() { return spatialRead; }
    public List<VisionTarget> targets() { return targets; }
}
