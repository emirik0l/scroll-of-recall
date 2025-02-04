package net.emirikol.recall.item;

import net.emirikol.recall.RecallMod;
import net.emirikol.recall.component.*;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

public class RecallTome extends RecallItem {
	public RecallTome(Settings settings) {
		super(settings);
	}
	
	@Override
	public void useUnbound(PlayerEntity playerEntity, ItemStack stack) {
		// Play a sound.
		playerEntity.playSound(SoundEvents.BLOCK_AMETHYST_BLOCK_CHIME, 1.0F, 1.0F);
		
		// Change to a tome of recall.
		stack = this.setRecallType(stack, RecallType.RECALL);

		// Store the player's current location.
		RecallTargetComponent target = RecallTargetComponent.fromPlayer(playerEntity);
		stack.set(RecallMod.TARGET_COMPONENT, target);
	}
	
	@Override
	public void useRecall(World world, PlayerEntity playerEntity, ItemStack stack) {
		// Play a sound.
		playerEntity.playSound(SoundEvents.BLOCK_PORTAL_TRAVEL, 0.15F, 1.5F);
		
		// Make a note of the player's current coordinates, which will be needed to return.
		RecallTargetComponent returnTarget = RecallTargetComponent.fromPlayer(playerEntity);
		
		// Teleport the player to the coordinates stored in the tome.
		this.doTeleport(world, playerEntity, stack);
		
		// Back up the recall coordinates so they can be restored later.
		RecallTargetComponent target = stack.get(RecallMod.TARGET_COMPONENT);
		stack.set(RecallMod.TARGET_BACKUP_COMPONENT, target);
		
		// Change to a tome of return.
		stack = this.setRecallType(stack, RecallType.RETURN);
		
		// Store the return coordinates.
		stack.set(RecallMod.TARGET_COMPONENT, returnTarget);
		
		// Small cooldown.
		playerEntity.getItemCooldownManager().set(stack, 40);
	}
	
	@Override
	public void useReturn(World world, PlayerEntity playerEntity, ItemStack stack) {
		// Play a sound.
		playerEntity.playSound(SoundEvents.BLOCK_PORTAL_TRAVEL, 0.15F, 1.5F);

		// Teleport the player to the coordinates stored in the tome.
		this.doTeleport(world, playerEntity, stack);

		// Change to a tome of recall.
		stack = this.setRecallType(stack, RecallType.RECALL);

		// Retrieve the backed up recall coordinates and store them.
		RecallTargetComponent recallTarget = stack.remove(RecallMod.TARGET_BACKUP_COMPONENT);
		stack.set(RecallMod.TARGET_COMPONENT, recallTarget);
		
		// Small cooldown.
		playerEntity.getItemCooldownManager().set(stack, 40);
	}

	@Override
	public Text getName(ItemStack stack) {
		switch(this.getRecallType(stack)) {
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
		
		switch(this.getRecallType(stack)) {
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
}