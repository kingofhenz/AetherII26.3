/*
 * Based on NeoForge's IGlobalLootModifier.
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */
package com.aetherteam.aetherii.nitrogen.loot.modifiers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

import java.util.List;
import java.util.function.Function;

/**
 * A global loot modifier, applied to the drops of every loot table.
 */
public interface IGlobalLootModifier {
    Codec<IGlobalLootModifier> DIRECT_CODEC = Codec.lazyInitialized(() -> NitrogenLootModifiers.GLOBAL_LOOT_MODIFIERS.byNameCodec()
        .dispatch(IGlobalLootModifier::codec, Function.identity()));

    Codec<LootItemCondition[]> LOOT_CONDITIONS_CODEC = LootItemCondition.DIRECT_CODEC.listOf()
        .xmap(list -> list.toArray(LootItemCondition[]::new), List::of);

    ObjectArrayList<ItemStack> apply(ObjectArrayList<ItemStack> generatedLoot, LootContext context);

    MapCodec<? extends IGlobalLootModifier> codec();

    default int priority() {
        return 0;
    }
}
