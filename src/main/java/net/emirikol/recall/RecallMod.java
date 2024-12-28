package net.emirikol.recall;

import net.emirikol.recall.item.*;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;

import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registry;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.component.ComponentType;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import com.mojang.serialization.Codec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RecallMod implements ModInitializer {
	public static final String MOD_ID = "recall";
	
	public static RecallScroll RECALL_SCROLL;
	public static ComponentType<Integer> SCROLL_TYPE_COMPONENT;
	public static ComponentType<BlockPos> COORD_COMPONENT;

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {		
		// Set up item settings.
		Identifier recall_scroll_id = Identifier.of(MOD_ID, "recall_scroll");
		RegistryKey<Item> recall_scroll_key = RegistryKey.of(RegistryKeys.ITEM, recall_scroll_id);
		Item.Settings recall_scroll_settings = new Item.Settings().useItemPrefixedTranslationKey().registryKey(recall_scroll_key);
		
		// Initialise objects.
		RECALL_SCROLL = new RecallScroll(recall_scroll_settings);
		SCROLL_TYPE_COMPONENT = ComponentType.<Integer>builder().codec(Codec.INT).build();
		COORD_COMPONENT = ComponentType.<BlockPos>builder().codec(BlockPos.CODEC).build();
		
		// Add items to item groups.
		ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register((itemGroup) -> itemGroup.add(RECALL_SCROLL));
		
		// Register objects.
		Registry.register(Registries.ITEM, recall_scroll_id, RECALL_SCROLL);
		Registry.register(Registries.DATA_COMPONENT_TYPE, Identifier.of(MOD_ID, "scroll_type_component"), SCROLL_TYPE_COMPONENT);
		Registry.register(Registries.DATA_COMPONENT_TYPE, Identifier.of(MOD_ID, "coord_component"), COORD_COMPONENT);
	}
}