package net.emirikol.recall.item;

import net.emirikol.recall.RecallMod;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
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
		System.out.println("Scroll was used!"); //TODO
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