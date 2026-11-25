/*
 * Based on NeoForge's LootModifier.
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */
package com.aetherteam.aetherii.nitrogen.loot.modifiers;

import com.mojang.datafixers.Products;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

/**
 * Base global loot modifier that checks its loot conditions before modifying drops.
 */
public abstract class LootModifier implements IGlobalLootModifier {
    protected final LootItemCondition[] conditions;
    protected final int priority;

    public static <T extends LootModifier> Products.P2<RecordCodecBuilder.Mu<T>, LootItemCondition[], Integer> codecStart(RecordCodecBuilder.Instance<T> instance) {
        return instance.group(
            LOOT_CONDITIONS_CODEC.fieldOf("conditions").forGetter(modifier -> modifier.conditions),
            Codec.INT.optionalFieldOf("priority", 0).forGetter(modifier -> modifier.priority)
        );
    }

    protected LootModifier(LootItemCondition[] conditions, int priority) {
        this.conditions = conditions;
        this.priority = priority;
    }

    protected LootModifier(LootItemCondition[] conditions) {
        this(conditions, 0);
    }

    @Override
    public final ObjectArrayList<ItemStack> apply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        for (LootItemCondition condition : this.conditions) {
            if (!condition.test(context)) {
                return generatedLoot;
            }
        }
        return this.doApply(generatedLoot, context);
    }

    @Override
    public int priority() {
        return this.priority;
    }

    protected abstract ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context);
}
