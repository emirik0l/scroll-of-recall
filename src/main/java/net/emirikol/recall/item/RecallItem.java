package net.emirikol.recall.item;

import net.emirikol.recall.RecallMod;
import net.emirikol.recall.component.*;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.CustomModelDataComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.TeleportTarget;
import net.minecraft.world.World;

import java.util.List;

public abstract class RecallItem extends Item {
	public RecallItem(Settings settings) {
		super(settings);
	}
	
	@Override
	public ActionResult use(World world, PlayerEntity playerEntity, Hand hand) {
		ItemStack stack = playerEntity.getStackInHand(hand);
		
		switch(this.getRecallType(stack)) {
			case UNBOUND:
				this.useUnbound(playerEntity, stack);
				break;
			case RECALL:
				this.useRecall(world, playerEntity, stack);
				break;
			case RETURN:
				this.useReturn(world, playerEntity, stack);
				break;
		}
		
		return ActionResult.SUCCESS;
	}
	
	public abstract void useUnbound(PlayerEntity playerEntity, ItemStack stack);
	public abstract void useRecall(World world, PlayerEntity playerEntity, ItemStack stack);
	public abstract void useReturn(World world, PlayerEntity playerEntity, ItemStack stack);

	public void doTeleport(World world, PlayerEntity playerEntity, ItemStack stack) {
		RecallTargetComponent target = stack.get(RecallMod.TARGET_COMPONENT);
		
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
		Entity teleportedEntity = playerEntity.teleportTo(teleportTarget);
		
		// Cancel out velocity.
		if (teleportedEntity != null) {
			teleportedEntity.fallDistance = 0;
		}
	}

	public RecallType getRecallType(ItemStack stack) {
		int recallType = stack.getOrDefault(RecallMod.SCROLL_TYPE_COMPONENT, 0);
		
		switch (recallType) {
			case 0:
				return RecallType.UNBOUND;
			case 1:
				return RecallType.RECALL;
			case 2:
				return RecallType.RETURN;
			default:
				return RecallType.UNBOUND;
		}
	}
	
	public ItemStack setRecallType(ItemStack stack, RecallType recallType) {
		switch (recallType) {
			case RecallType.UNBOUND:
				stack.set(RecallMod.SCROLL_TYPE_COMPONENT, 0);
				stack.set(DataComponentTypes.CUSTOM_MODEL_DATA, new CustomModelDataComponent(List.of(), List.of(), List.of("unbound"), List.of()));
				break;
			case RecallType.RECALL:
				stack.set(RecallMod.SCROLL_TYPE_COMPONENT, 1);
				stack.set(DataComponentTypes.CUSTOM_MODEL_DATA, new CustomModelDataComponent(List.of(), List.of(), List.of("recall"), List.of()));
				break;
			case RecallType.RETURN:
				stack.set(RecallMod.SCROLL_TYPE_COMPONENT, 2);
				stack.set(DataComponentTypes.CUSTOM_MODEL_DATA, new CustomModelDataComponent(List.of(), List.of(), List.of("return"), List.of()));
				break;
			default:
				stack.set(RecallMod.SCROLL_TYPE_COMPONENT, 0);
				stack.set(DataComponentTypes.CUSTOM_MODEL_DATA, new CustomModelDataComponent(List.of(), List.of(), List.of("unbound"), List.of()));
				break;
		}		
		
		return stack;
	}
	
	public enum RecallType {
		UNBOUND,
		RECALL,
		RETURN
	}
}