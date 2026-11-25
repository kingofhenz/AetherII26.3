package com.aetherteam.aetherii.nitrogen.data.providers;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootSubProvider;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.functions.CopyNameFunction;

import java.util.concurrent.CompletableFuture;

/**
 * Fabric version of Nitrogen's block loot helper provider.
 */
public abstract class NitrogenBlockLootSubProvider extends FabricBlockLootSubProvider {
    public NitrogenBlockLootSubProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    public void dropNone(Block block) {
        this.add(block, noDrop());
    }

    public void dropWithFortune(Block block, Item drop) {
        this.add(block, (result) -> this.createOreDrop(result, drop));
    }

    public LootTable.Builder droppingNameableBlockEntityTable(Block block) {
        return LootTable.lootTable().withPool(this.applyExplosionCondition(block, LootPool.lootPool()
            .add(LootItem.lootTableItem(block)
                .apply(CopyNameFunction.copyName(LootContext.BlockEntityTarget.BLOCK_ENTITY))))
        );
    }

    /**
     * Drops the block itself with silk touch or shears (including any tool in the {@code c:tools/shear} tag, via
     * {@link #hasShears()} overrides), otherwise the given entry.
     */
    public LootTable.Builder createForgeSilkTouchOrShearsDispatchTable(HolderGetter<Item> items, Block block, LootPoolEntryContainer.Builder<?> builder) {
        return this.createSilkTouchOrShearsDispatchTable(block, builder);
    }
}
