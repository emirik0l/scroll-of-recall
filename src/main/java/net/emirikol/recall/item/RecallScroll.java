package net.emirikol.recall.item;

import net.emirikol.recall.RecallMod;
import net.emirikol.recall.component.*;
import net.emirikol.recall.util.*;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.CustomModelDataComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.RegistryKey;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

public class RecallScroll extends Item {
	public RecallScroll(Settings settings) {
		super(settings);
	}

	@Override
	public ActionResult use(World world, PlayerEntity playerEntity, Hand hand) {
		ItemStack stack = playerEntity.getStackInHand(hand);
		
		switch(this.getStatus(stack)) {
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
	
	public void useUnbound(PlayerEntity playerEntity, ItemStack stack) {
		// Play a sound.
		playerEntity.playSound(SoundEvents.BLOCK_AMETHYST_BLOCK_CHIME, 1.0F, 1.0F);
		
		// Create a scroll of recall.
		ItemStack newStack = new ItemStack(RecallMod.RECALL_SCROLL, 1);
		newStack.set(RecallMod.SCROLL_TYPE_COMPONENT, 1);
		
		// Store the player's current location.
		RecallTargetComponent target = RecallTargetComponent.fromPlayer(playerEntity);
		newStack.set(RecallMod.TARGET_COMPONENT, target);
		
		// Give it the correct model.
		CustomModelDataComponent component = new CustomModelDataComponent(List.of(), List.of(), List.of("recall"), List.of());
		newStack.set(DataComponentTypes.CUSTOM_MODEL_DATA, component);
		
		// Give it to the player.
		PlayerInventory inventory = playerEntity.getInventory();
		inventory.offerOrDrop(newStack);
		inventory.markDirty();
		
		// Decrement the stack.
		stack.decrement(1);
	}
	
	public void useRecall(World world, PlayerEntity playerEntity, ItemStack stack) {
		// Play a sound.
		playerEntity.playSound(SoundEvents.BLOCK_PORTAL_TRAVEL, 0.15F, 1.5F);
		
		// Make a note of the player's current coordinates, which will be needed to return.
		BlockPos returnPos = playerEntity.getBlockPos();
		RegistryKey<World> returnWorld = playerEntity.getWorld().getRegistryKey();
		
		// Teleport the player to the coordinates stored in the scroll.
		RecallTargetComponent target = stack.get(RecallMod.TARGET_COMPONENT);
		RecallTeleport.doTeleport(world, playerEntity, target);
		
		// Create a scroll of return.
		ItemStack newStack = new ItemStack(RecallMod.RECALL_SCROLL, 1);
		newStack.set(RecallMod.SCROLL_TYPE_COMPONENT, 2);
		
		// Store the return coordinates.
		RecallTargetComponent newTarget = new RecallTargetComponent(returnPos, returnWorld);
		newStack.set(RecallMod.TARGET_COMPONENT, newTarget);
		
		// Give it the correct model.
		CustomModelDataComponent component = new CustomModelDataComponent(List.of(), List.of(), List.of("return"), List.of());
		newStack.set(DataComponentTypes.CUSTOM_MODEL_DATA, component);
		
		// Give it to the player.
		PlayerInventory inventory = playerEntity.getInventory();
		inventory.offerOrDrop(newStack);
		inventory.markDirty();
		
		// Small cooldown.
		playerEntity.getItemCooldownManager().set(stack, 40);
		
		// Decrement the stack.
		stack.decrement(1);		
	}
	
	public void useReturn(World world, PlayerEntity playerEntity, ItemStack stack) {
		// Play a sound.
		playerEntity.playSound(SoundEvents.BLOCK_PORTAL_TRAVEL, 0.15F, 1.5F);
		
		// Teleport the player to the coordinates stored in the scroll.
		RecallTargetComponent target = stack.get(RecallMod.TARGET_COMPONENT);
		RecallTeleport.doTeleport(world, playerEntity, target);
		
		// Small cooldown.
		playerEntity.getItemCooldownManager().set(stack, 40);
		
		// Decrement the stack.
		stack.decrement(1);
	}
	
	@Override
	public Text getName(ItemStack stack) {
		switch(this.getStatus(stack)) {
			case UNBOUND:
				return Text.translatable("item.recall.recall_scroll.unbound_name");
			case RECALL:
				return Text.translatable("item.recall.recall_scroll.recall_name");
			case RETURN:
				return Text.translatable("item.recall.recall_scroll.return_name");
			default:
				return super.getName(stack);
		}
	}
	
	@Override
	public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
		BlockPos pos;
		String coord_str;
		
		switch(this.getStatus(stack)) {
			case UNBOUND:
				tooltip.add(Text.translatable("item.recall.recall_scroll.unbound_tooltip"));
				break;
			case RECALL:
				tooltip.add(Text.translatable("item.recall.recall_scroll.recall_tooltip"));
				
				pos = stack.get(RecallMod.TARGET_COMPONENT).pos();
				coord_str = String.format("(x=%d, y=%d, z=%d)", pos.getX(), pos.getY(), pos.getZ());
				tooltip.add(Text.literal(coord_str).formatted(Formatting.DARK_PURPLE));
				break;
			case RETURN:
				tooltip.add(Text.translatable("item.recall.recall_scroll.return_tooltip"));
				
				pos = stack.get(RecallMod.TARGET_COMPONENT).pos();
				coord_str = String.format("(x=%d, y=%d, z=%d)", pos.getX(), pos.getY(), pos.getZ());
				tooltip.add(Text.literal(coord_str).formatted(Formatting.DARK_PURPLE));
				break;
		}
	}
	
	public Status getStatus(ItemStack stack) {
		int scroll_type = stack.getOrDefault(RecallMod.SCROLL_TYPE_COMPONENT, 0);
		
		switch (scroll_type) {
			case 0:
				return Status.UNBOUND;
			case 1:
				return Status.RECALL;
			case 2:
				return Status.RETURN;
			default:
				return Status.UNBOUND;
		}
	}
	
	enum Status {
		UNBOUND,
		RECALL,
		RETURN
	}
}