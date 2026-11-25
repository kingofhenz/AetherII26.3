package com.aetherteam.aetherii.nitrogen.data.providers;

import com.aetherteam.aetherii.nitrogen.Nitrogen;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.structure.Structure;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

/**
 * Fabric version of Nitrogen's language provider: subclasses fill {@link #addTranslations()} using the
 * NeoForge-style {@code add(...)} helpers, which are forwarded to Fabric's translation builder.
 */
public abstract class NitrogenLanguageProvider extends FabricLanguageProvider {
    protected final String id;
    private TranslationBuilder builder;

    public NitrogenLanguageProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registries, String id) {
        super(output, "en_us", registries);
        this.id = id;
    }

    protected abstract void addTranslations();

    @Override
    public void generateTranslations(HolderLookup.Provider registries, TranslationBuilder builder) {
        this.builder = builder;
        this.addTranslations();
    }

    public void add(String key, String value) {
        this.builder.add(key, value);
    }

    public void add(Block key, String name) {
        this.add(key.getDescriptionId(), name);
    }

    public void addBlock(Supplier<? extends Block> key, String name) {
        this.add(key.get(), name);
    }

    public void add(Item key, String name) {
        this.add(key.getDescriptionId(), name);
    }

    public void addItem(Supplier<? extends Item> key, String name) {
        this.add(key.get(), name);
    }

    public void add(ItemStack key, String name) {
        this.add(key.getItem().getDescriptionId(), name);
    }

    public void add(MobEffect key, String name) {
        this.add(key.getDescriptionId(), name);
    }

    public void addEffect(Supplier<? extends MobEffect> key, String name) {
        this.add(key.get(), name);
    }

    public void add(EntityType<?> key, String name) {
        this.add(key.getDescriptionId(), name);
    }

    public void addEntityType(Supplier<? extends EntityType<?>> key, String name) {
        this.add(key.get(), name);
    }

    public void addPerItemAbilityTooltip(Item item, int index, String name) {
        this.add(item.getDescriptionId() + "." + Nitrogen.MODID + ".ability.tooltip." + index, name);
    }

    public void addPerItemAbilityTooltip(Item item, int index, String condition, String name) {
        this.add(item.getDescriptionId() + "." + Nitrogen.MODID + ".ability.tooltip." + index + "." + condition, name);
    }

    public void addDiscDesc(Supplier<? extends Item> key, String name) {
        this.add(key.get().getDescriptionId() + ".desc", name);
    }

    public void addTrim(String key, String name) {
        this.add("trim_material." + this.id + "." + key, name + " Material");
    }

    public void addEffectDesc(Supplier<? extends MobEffect> key, String name) {
        this.add(key.get().getDescriptionId() + ".description", name);
    }

    public void addDimension(ResourceKey<Level> dimension, String name) {
        this.add("dimension." + this.id + "." + dimension.identifier().getPath(), name);
    }

    public void addBiome(ResourceKey<Biome> biome, String name) {
        this.add("biome." + this.id + "." + biome.identifier().getPath(), name);
    }

    public void addStructure(ResourceKey<Structure> structure, String name) {
        this.add("structure." + this.id + "." + structure.identifier().getPath(), name);
    }

    public void addContainerType(Supplier<? extends MenuType<?>> key, String name) {
        Identifier location = BuiltInRegistries.MENU.getKey(key.get());
        if (location != null) {
            this.add("menu." + location.toString().replace(":", "."), name);
        }
    }

    public void addContainerType(String key, String name) {
        this.add("menu." + this.id + "." + key, name);
    }

    public void addCreativeTab(CreativeModeTab tab, String name) {
        if (tab.getDisplayName().getContents() instanceof TranslatableContents translatable) {
            this.add(translatable.getKey(), name);
        } else {
            this.add(tab.getDisplayName().getString(), name);
        }
    }

    public void addAdvancement(String key, String name) {
        this.add("advancement." + this.id + "." + key, name);
    }

    public void addAdvancementDesc(String key, String name) {
        this.add("advancement." + this.id + "." + key + ".desc", name);
    }

    public void addSubtitle(String category, String key, String name) {
        this.add("subtitles." + this.id + "." + category + "." + key, name);
    }

    public void addDeath(String key, String name) {
        this.add("death.attack." + this.id + "." + key, name);
    }

    public void addMenuText(String key, String name) {
        this.addGuiText("menu." + key, name);
    }

    public void addGuiText(String key, String name) {
        this.add("gui." + this.id + "." + key, name);
    }

    public void addGeneric(String key, String name) {
        this.add(this.id + "." + key, name);
    }

    public void addCommand(String key, String name) {
        this.add("commands." + this.id + "." + key, name);
    }

    public void addKeyInfo(String key, String name) {
        this.add("key." + this.id + "." + key, name);
    }

    public void addAccessoriesIdentifier(String key, String name) {
        this.add("accessories.slot." + this.id + "." + key, name);
    }

    public void addConfig(String prefix, String name) {
        this.add(this.id + ".configuration." + prefix, name);
    }

    public void addServerConfig(String prefix, String name) {
        this.add("config." + this.id + ".server." + prefix, name);
    }

    public void addCommonConfig(String prefix, String name) {
        this.add("config." + this.id + ".common." + prefix, name);
    }

    public void addClientConfig(String prefix, String name) {
        this.add("config." + this.id + ".client." + prefix, name);
    }

    public void addServerConfig(String prefix, String key, String name) {
        this.add("config." + this.id + ".server." + prefix + "." + key, name);
        this.add("config." + this.id + ".server." + prefix + "." + key + ".tooltip", name);
    }

    public void addCommonConfig(String prefix, String key, String name) {
        this.add("config." + this.id + ".common." + prefix + "." + key, name);
        this.add("config." + this.id + ".common." + prefix + "." + key + ".tooltip", name);
    }

    public void addClientConfig(String prefix, String key, String name) {
        this.add("config." + this.id + ".client." + prefix + "." + key, name);
        this.add("config." + this.id + ".client." + prefix + "." + key + ".tooltip", name);
    }

    public void addPackTitle(String packName, String description) {
        this.add("pack." + this.id + "." + packName + ".title", description);
    }

    public void addPackDescription(String packName, String description) {
        this.add("pack." + this.id + "." + packName + ".description", description);
    }

    public void addPatreonTier(String key, String name) {
        this.add(this.id + ".patreon.tier." + key, name);
    }
}
