package net.emirikol.recall.item;

import net.emirikol.recall.RecallMod;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.CustomModelDataComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
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
		
		switch(this.getStatus(stack)) {
			case UNBOUND:
				this.useUnbound(playerEntity, stack);
				break;
			case RECALL:
				this.useRecall(playerEntity, stack);
				break;
			case RETURN:
				this.useReturn(playerEntity, stack);
				break;
		}
		
		return ActionResult.SUCCESS;
	}
	
	public void useUnbound(PlayerEntity playerEntity, ItemStack stack) {
		// Play a sound.
		playerEntity.playSound(SoundEvents.BLOCK_AMETHYST_BLOCK_CHIME, 1.0F, 1.0F);
		
		// Change to a tome of recall.
		stack.set(RecallMod.SCROLL_TYPE_COMPONENT, 1);

		// Store the player's current coordinates.
		BlockPos currentPos = playerEntity.getBlockPos();
		stack.set(RecallMod.COORD_COMPONENT, currentPos);		
		
		// Give it the correct model.
		CustomModelDataComponent component = new CustomModelDataComponent(List.of(), List.of(), List.of("recall"), List.of());
		stack.set(DataComponentTypes.CUSTOM_MODEL_DATA, component);
	}
	
	public void useRecall(PlayerEntity playerEntity, ItemStack stack) {
		// Play a sound.
		playerEntity.playSound(SoundEvents.BLOCK_PORTAL_TRAVEL, 0.15F, 1.5F);
		
		// Small cooldown.
		playerEntity.getItemCooldownManager().set(stack, 40);
		
		// Make a note of the player's current coordinates, which will be needed to return.
		BlockPos returnPos = playerEntity.getBlockPos();
		
		// Teleport the player to the coordinates stored in the tome.
		BlockPos telePos = stack.get(RecallMod.COORD_COMPONENT);
		playerEntity.setPos(telePos.getX(), telePos.getY(), telePos.getZ());		
		
		// Back up the recall coordinates so they can be restored later.
		stack.set(RecallMod.COORD_BACKUP_COMPONENT, telePos);
		
		// Change to a tome of return.
		stack.set(RecallMod.SCROLL_TYPE_COMPONENT, 2);
		
		// Store the return coordinates.
		stack.set(RecallMod.COORD_COMPONENT, returnPos);
		
		// Give it the correct model.
		CustomModelDataComponent component = new CustomModelDataComponent(List.of(), List.of(), List.of("return"), List.of());
		stack.set(DataComponentTypes.CUSTOM_MODEL_DATA, component);
	}
	
	public void useReturn(PlayerEntity playerEntity, ItemStack stack) {
		// Play a sound.
		playerEntity.playSound(SoundEvents.BLOCK_PORTAL_TRAVEL, 0.15F, 1.5F);
		
		// Small cooldown.
		playerEntity.getItemCooldownManager().set(stack, 40);

		// Teleport the player to the coordinates stored in the tome.
		BlockPos telePos = stack.get(RecallMod.COORD_COMPONENT);
		playerEntity.setPos(telePos.getX(), telePos.getY(), telePos.getZ());

		// Change to a tome of recall.
		stack.set(RecallMod.SCROLL_TYPE_COMPONENT, 1);

		// Retrieve the backed up recall coordinates and store them.
		BlockPos recallPos = stack.remove(RecallMod.COORD_BACKUP_COMPONENT);
		stack.set(RecallMod.COORD_COMPONENT, recallPos);
		
		// Give it the correct model.
		CustomModelDataComponent component = new CustomModelDataComponent(List.of(), List.of(), List.of("recall"), List.of());
		stack.set(DataComponentTypes.CUSTOM_MODEL_DATA, component);
	}

	@Override
	public Text getName(ItemStack stack) {
		switch(this.getStatus(stack)) {
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
		
		switch(this.getStatus(stack)) {
			case UNBOUND:
				tooltip.add(Text.translatable("item.recall.recall_tome.unbound_tooltip"));
				break;
			case RECALL:
				tooltip.add(Text.translatable("item.recall.recall_tome.recall_tooltip"));
				
				pos = stack.get(RecallMod.COORD_COMPONENT);
				coord_str = String.format("(x=%d, y=%d, z=%d)", pos.getX(), pos.getY(), pos.getZ());
				tooltip.add(Text.literal(coord_str).formatted(Formatting.DARK_PURPLE));
				break;
			case RETURN:
				tooltip.add(Text.translatable("item.recall.recall_tome.return_tooltip"));
				
				pos = stack.get(RecallMod.COORD_COMPONENT);
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