package net.emirikol.recall.item;

import net.emirikol.recall.RecallMod;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.CustomModelDataComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
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
	
	enum RecallType {
		UNBOUND,
		RECALL,
		RETURN
	}
}