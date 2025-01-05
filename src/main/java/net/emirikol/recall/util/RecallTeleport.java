package net.emirikol.recall.util;

import net.emirikol.recall.component.*;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.TeleportTarget;
import net.minecraft.world.World;

public class RecallTeleport {
	public static void doTeleport(World world, PlayerEntity playerEntity, RecallTargetComponent target) {
		// Teleportation is server side only.
		if (world.isClient) { return; }
		
		// Retrieve target information from custom component.
		BlockPos targetPos = target.pos();
		RegistryKey<World> targetWorld = target.world();
		
		// Convert the target BlockPos into a Vec3d.
		Vec3d targetVec = new Vec3d(targetPos.getX(), targetPos.getY(), targetPos.getZ());
		
		// Attempt to retrieve the target world from the server.
		ServerWorld serverWorld = world.getServer().getWorld(targetWorld);
		if (serverWorld == null) { return; }
		
		// Perform the teleport.
		TeleportTarget teleportTarget = new TeleportTarget(serverWorld, targetVec, Vec3d.ZERO, 0, 0, TeleportTarget.NO_OP);
		playerEntity.teleportTo(teleportTarget);
	}
}