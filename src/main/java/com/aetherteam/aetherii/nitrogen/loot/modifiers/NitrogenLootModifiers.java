package com.aetherteam.aetherii.nitrogen.loot.modifiers;

import com.aetherteam.aetherii.AetherII;
import com.mojang.serialization.MapCodec;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;

/**
 * Registry of global loot modifier codecs, plus the hook that loads and applies global loot modifiers on Fabric.
 */
public final class NitrogenLootModifiers {
    public static final ResourceKey<Registry<MapCodec<? extends IGlobalLootModifier>>> GLOBAL_LOOT_MODIFIERS_KEY =
        ResourceKey.createRegistryKey(Identifier.fromNamespaceAndPath(AetherII.MODID, "global_loot_modifier_serializers"));

    public static final Registry<MapCodec<? extends IGlobalLootModifier>> GLOBAL_LOOT_MODIFIERS =
        FabricRegistryBuilder.create(GLOBAL_LOOT_MODIFIERS_KEY).buildAndRegister();

    private NitrogenLootModifiers() {
    }

    public static void init() {
        GlobalLootModifierManager.init();
    }
}
