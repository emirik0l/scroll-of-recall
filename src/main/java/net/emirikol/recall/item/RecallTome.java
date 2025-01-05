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

public class RecallTome extends Item {
	public RecallTome(Settings settings) {
		super(settings);
	}
	
	@Override
	public ActionResult use(World world, PlayerEntity playerEntity, Hand hand) {
		ItemStack stack = playerEntity.getStackInHand(hand);
		
		switch(this.getTomeType(stack)) {
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
		
		// Change to a tome of recall.
		stack = this.setTomeType(stack, TomeType.RECALL);

		// Store the player's current location.
		RecallTargetComponent target = RecallTargetComponent.fromPlayer(playerEntity);
		stack.set(RecallMod.TARGET_COMPONENT, target);
	}
	
	public void useRecall(World world, PlayerEntity playerEntity, ItemStack stack) {
		// Play a sound.
		playerEntity.playSound(SoundEvents.BLOCK_PORTAL_TRAVEL, 0.15F, 1.5F);
		
		// Make a note of the player's current coordinates, which will be needed to return.
		BlockPos returnPos = playerEntity.getBlockPos();
		RegistryKey<World> returnWorld = playerEntity.getWorld().getRegistryKey();
		
		// Teleport the player to the coordinates stored in the tome.
		RecallTargetComponent target = stack.get(RecallMod.TARGET_COMPONENT);
		RecallTeleport.doTeleport(world, playerEntity, target);
		
		// Back up the recall coordinates so they can be restored later.
		stack.set(RecallMod.TARGET_BACKUP_COMPONENT, target);
		
		// Change to a tome of return.
		stack = this.setTomeType(stack, TomeType.RETURN);
		
		// Store the return coordinates.
		RecallTargetComponent newTarget = new RecallTargetComponent(returnPos, returnWorld);
		stack.set(RecallMod.TARGET_COMPONENT, newTarget);
		
		// Small cooldown.
		playerEntity.getItemCooldownManager().set(stack, 40);
	}
	
	public void useReturn(World world, PlayerEntity playerEntity, ItemStack stack) {
		// Play a sound.
		playerEntity.playSound(SoundEvents.BLOCK_PORTAL_TRAVEL, 0.15F, 1.5F);

		// Teleport the player to the coordinates stored in the tome.
		RecallTargetComponent target = stack.get(RecallMod.TARGET_COMPONENT);
		RecallTeleport.doTeleport(world, playerEntity, target);

		// Change to a tome of recall.
		stack = this.setTomeType(stack, TomeType.RECALL);

		// Retrieve the backed up recall coordinates and store them.
		RecallTargetComponent recallTarget = stack.remove(RecallMod.TARGET_BACKUP_COMPONENT);
		stack.set(RecallMod.TARGET_COMPONENT, recallTarget);
		
		// Small cooldown.
		playerEntity.getItemCooldownManager().set(stack, 40);
	}

	@Override
	public Text getName(ItemStack stack) {
		switch(this.getTomeType(stack)) {
			case UNBOUND:
				return Text.translatable("item.recall.recall_tome.unbound_name");
			case RECALL:
				return Text.translatable("item.recall.recall_tome.recall_name");
			case RETURN:
				return Text.translatable("item.recall.recall_tome.return_name");
			default:
				return super.getName(stack);
		}
	}
	
	@Override
	public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
		BlockPos pos;
		String coord_str;
		
		switch(this.getTomeType(stack)) {
			case UNBOUND:
				tooltip.add(Text.translatable("item.recall.recall_tome.unbound_tooltip"));
				break;
			case RECALL:
				tooltip.add(Text.translatable("item.recall.recall_tome.recall_tooltip"));
				
				pos = stack.get(RecallMod.TARGET_COMPONENT).pos();
				coord_str = String.format("(x=%d, y=%d, z=%d)", pos.getX(), pos.getY(), pos.getZ());
				tooltip.add(Text.literal(coord_str).formatted(Formatting.DARK_PURPLE));
				break;
			case RETURN:
				tooltip.add(Text.translatable("item.recall.recall_tome.return_tooltip"));
				
				pos = stack.get(RecallMod.TARGET_COMPONENT).pos();
				coord_str = String.format("(x=%d, y=%d, z=%d)", pos.getX(), pos.getY(), pos.getZ());
				tooltip.add(Text.literal(coord_str).formatted(Formatting.DARK_PURPLE));
				break;
		}
	}
	
	public TomeType getTomeType(ItemStack stack) {
		int tomeType = stack.getOrDefault(RecallMod.SCROLL_TYPE_COMPONENT, 0);
		
		switch (tomeType) {
			case 0:
				return TomeType.UNBOUND;
			case 1:
				return TomeType.RECALL;
			case 2:
				return TomeType.RETURN;
			default:
				return TomeType.UNBOUND;
		}
	}
	
	public ItemStack setTomeType(ItemStack stack, TomeType tomeType) {
		switch (tomeType) {
			case TomeType.UNBOUND:
				stack.set(RecallMod.SCROLL_TYPE_COMPONENT, 0);
				stack.set(DataComponentTypes.CUSTOM_MODEL_DATA, new CustomModelDataComponent(List.of(), List.of(), List.of("unbound"), List.of()));
				break;
			case TomeType.RECALL:
				stack.set(RecallMod.SCROLL_TYPE_COMPONENT, 1);
				stack.set(DataComponentTypes.CUSTOM_MODEL_DATA, new CustomModelDataComponent(List.of(), List.of(), List.of("recall"), List.of()));
				break;
			case TomeType.RETURN:
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
	
	enum TomeType {
		UNBOUND,
		RECALL,
		RETURN
	}
}