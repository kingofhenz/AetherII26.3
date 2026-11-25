package com.aetherteam.aetherii.world;

import com.aetherteam.aetherii.AetherII;
import com.aetherteam.aetherii.block.AetherIIBlocks;
import com.google.common.collect.ImmutableSet;
import net.minecraft.core.Holder;
import net.fabricmc.fabric.api.object.builder.v1.world.poi.PoiHelper;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Set;

public class AetherIIPoi {
    public static Holder.Reference<PoiType> AETHER_PORTAL;

    private static Set<BlockState> getBlockStates(Block block) {
        return ImmutableSet.copyOf(block.getStateDefinition().getPossibleStates());
    }

    public static void init() {
        // PoiHelper also maps the block states to the POI type (vanilla's PoiTypes#registerBlockStates). A plain
        // Registry.register skips that, so portals were never recorded and every trip built a new portal.
        Identifier id = Identifier.fromNamespaceAndPath(AetherII.MODID, "aether_portal");
        PoiHelper.register(id, 0, 1, getBlockStates(AetherIIBlocks.AETHER_PORTAL));
        AETHER_PORTAL = BuiltInRegistries.POINT_OF_INTEREST_TYPE.getOrThrow(ResourceKey.create(Registries.POINT_OF_INTEREST_TYPE, id));
    }
}