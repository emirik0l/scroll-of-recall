package net.emirikol.recall.item;

import net.emirikol.recall.RecallMod;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.world.World;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.CustomModelDataComponent;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;

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
				return this.useUnbound(playerEntity, stack);
			case RECALL:
				return this.useRecall(playerEntity, stack);
			case RETURN:
				return this.useReturn(playerEntity, stack);
		}
		
		return ActionResult.SUCCESS;
	}
	
	public ActionResult useUnbound(PlayerEntity playerEntity, ItemStack stack) {
		// TODO - play a sound
		
		// Create a scroll of recall.
		ItemStack newStack = new ItemStack(RecallMod.RECALL_SCROLL, 1);
		newStack.set(RecallMod.SCROLL_TYPE_COMPONENT, 1);
		
		// TODO - store the coordinates of your current location
		
		// Give it the correct model.
		CustomModelDataComponent component = new CustomModelDataComponent(List.of(), List.of(), List.of("recall"), List.of());
		newStack.set(DataComponentTypes.CUSTOM_MODEL_DATA, component);
		
		// Give it to the player.
		PlayerInventory inventory = playerEntity.getInventory();
		inventory.offerOrDrop(newStack);
		inventory.markDirty();
		
		// Decrement the old stack.
		stack.decrement(1);
		
		return ActionResult.SUCCESS;
	}
	
	public ActionResult useRecall(PlayerEntity playerEntity, ItemStack stack) {
		// TODO - play a sound
		
		// TODO - make a note of the player's current coordinates ("return coordinates")
		
		// TODO - teleport the player to the coordinates stored in the scroll
		
		// Create a scroll of return.
		ItemStack newStack = new ItemStack(RecallMod.RECALL_SCROLL, 1);
		newStack.set(RecallMod.SCROLL_TYPE_COMPONENT, 2);
		
		// TODO - store the return coordinates
		
		// Give it the correct model.
		CustomModelDataComponent component = new CustomModelDataComponent(List.of(), List.of(), List.of("return"), List.of());
		newStack.set(DataComponentTypes.CUSTOM_MODEL_DATA, component);
		
		// Give it to the player.
		PlayerInventory inventory = playerEntity.getInventory();
		inventory.offerOrDrop(newStack);
		inventory.markDirty();
		
		// Decrement the old stack.
		stack.decrement(1);
		
		return ActionResult.SUCCESS;
	}
	
	public ActionResult useReturn(PlayerEntity playerEntity, ItemStack stack) {
		// TODO - play a sound
		
		// TODO - teleport the player to the coordinates stored in the scroll
		
		// Decrement the stack.
		stack.decrement(1);
		
		return ActionResult.SUCCESS;
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
		switch(this.getStatus(stack)) {
			case UNBOUND:
				tooltip.add(Text.translatable("item.recall.recall_scroll.unbound_tooltip"));
				break;
			case RECALL:
				tooltip.add(Text.translatable("item.recall.recall_scroll.recall_tooltip"));
				break;
			case RETURN:
				tooltip.add(Text.translatable("item.recall.recall_scroll.return_tooltip"));
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