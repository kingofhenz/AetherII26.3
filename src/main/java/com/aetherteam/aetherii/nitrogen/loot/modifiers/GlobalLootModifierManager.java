package com.aetherteam.aetherii.nitrogen.loot.modifiers;

import com.aetherteam.aetherii.AetherII;
import com.aetherteam.aetherii.nitrogen.Nitrogen;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.serialization.JsonOps;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.fabricmc.fabric.api.resource.v1.ResourceLoader;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.RegistryOps;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

import java.io.Reader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Loads global loot modifiers in the same data layout NeoForge uses
 * ({@code data/<namespace>/loot_modifiers/global_loot_modifiers.json} listing entries, each stored at
 * {@code data/<namespace>/loot_modifiers/<path>.json}), and applies them to every loot table's drops.
 */
public final class GlobalLootModifierManager extends SimplePreparableReloadListener<Map<Identifier, JsonElement>> {
    public static final Identifier ID = Identifier.fromNamespaceAndPath(AetherII.MODID, "global_loot_modifiers");
    private static final String DIRECTORY = "loot_modifiers";
    private static final String INDEX = "global_loot_modifiers.json";

    private static volatile List<IGlobalLootModifier> modifiers = List.of();

    private HolderLookup.@Nullable Provider registries;

    private GlobalLootModifierManager() {
    }

    static void init() {
        ResourceLoader.get(PackType.SERVER_DATA).registerReloadListener(ID, new GlobalLootModifierManager());
        LootTableEvents.MODIFY_DROPS.register((table, context, drops) -> {
            List<IGlobalLootModifier> active = modifiers;
            if (active.isEmpty()) {
                return;
            }
            ObjectArrayList<ItemStack> stacks = new ObjectArrayList<>(drops);
            for (IGlobalLootModifier modifier : active) {
                stacks = modifier.apply(stacks, context);
            }
            drops.clear();
            drops.addAll(stacks);
        });
    }

    @Override
    public void prepareSharedState(PreparableReloadListener.SharedState sharedState) {
        this.registries = sharedState.get(ResourceLoader.REGISTRY_LOOKUP_KEY);
    }

    @Override
    protected Map<Identifier, JsonElement> prepare(ResourceManager resourceManager, ProfilerFiller profiler) {
        List<Identifier> entries = new ArrayList<>();
        for (String namespace : resourceManager.getNamespaces()) {
            Identifier indexLocation = Identifier.fromNamespaceAndPath(namespace, DIRECTORY + "/" + INDEX);
            for (Resource resource : resourceManager.getResourceStack(indexLocation)) {
                try (Reader reader = resource.openAsReader()) {
                    JsonObject index = JsonParser.parseReader(reader).getAsJsonObject();
                    if (index.has("replace") && index.get("replace").getAsBoolean()) {
                        entries.clear();
                    }
                    for (JsonElement element : index.getAsJsonArray("entries")) {
                        Identifier entry = Identifier.parse(element.getAsString());
                        entries.remove(entry);
                        entries.add(entry);
                    }
                } catch (Exception e) {
                    Nitrogen.LOGGER.error("Couldn't read global loot modifier list {} in data pack {}", indexLocation, resource.sourcePackId(), e);
                }
            }
        }

        Map<Identifier, JsonElement> data = new LinkedHashMap<>();
        for (Identifier entry : entries) {
            Identifier file = Identifier.fromNamespaceAndPath(entry.getNamespace(), DIRECTORY + "/" + entry.getPath() + ".json");
            Optional<Resource> resource = resourceManager.getResource(file);
            if (resource.isEmpty()) {
                Nitrogen.LOGGER.warn("Global loot modifier {} is listed but {} does not exist", entry, file);
                continue;
            }
            try (Reader reader = resource.get().openAsReader()) {
                data.put(entry, JsonParser.parseReader(reader));
            } catch (Exception e) {
                Nitrogen.LOGGER.error("Couldn't read global loot modifier {}", file, e);
            }
        }
        return data;
    }

    @Override
    protected void apply(Map<Identifier, JsonElement> data, ResourceManager resourceManager, ProfilerFiller profiler) {
        RegistryOps<JsonElement> ops = this.registries != null ? this.registries.createSerializationContext(JsonOps.INSTANCE) : null;
        List<IGlobalLootModifier> loaded = new ArrayList<>();
        data.forEach((id, json) -> (ops != null ? IGlobalLootModifier.DIRECT_CODEC.parse(ops, json) : IGlobalLootModifier.DIRECT_CODEC.parse(JsonOps.INSTANCE, json))
            .resultOrPartial(error -> Nitrogen.LOGGER.warn("Could not decode global loot modifier {}: {}", id, error))
            .ifPresent(loaded::add));
        loaded.sort(Comparator.comparingInt(IGlobalLootModifier::priority));
        modifiers = List.copyOf(loaded);
    }
}
