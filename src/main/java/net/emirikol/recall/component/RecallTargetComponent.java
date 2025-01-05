package net.emirikol.recall.component;

import net.minecraft.world.World;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.math.BlockPos;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record RecallTargetComponent(BlockPos pos, RegistryKey<World> world) {
	public static final Codec<RecallTargetComponent> CODEC = RecordCodecBuilder.create(builder -> builder.group(
        BlockPos.CODEC.fieldOf("pos").forGetter(RecallTargetComponent::pos),
        World.CODEC.fieldOf("world").forGetter(RecallTargetComponent::world)
    ).apply(builder, RecallTargetComponent::new));
	
	public static RecallTargetComponent fromPlayer(PlayerEntity playerEntity) {
		BlockPos currentPos = playerEntity.getBlockPos();
		RegistryKey<World> currentWorld = playerEntity.getWorld().getRegistryKey();
		return new RecallTargetComponent(currentPos, currentWorld);
	}
}