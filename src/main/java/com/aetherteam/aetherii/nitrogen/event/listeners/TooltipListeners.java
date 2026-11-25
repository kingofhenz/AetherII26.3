package com.aetherteam.aetherii.nitrogen.event.listeners;

import com.aetherteam.aetherii.nitrogen.Nitrogen;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraft.client.Minecraft;
import net.minecraft.locale.Language;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Adds per-item ability tooltips ({@code <item>.nitrogen_internals.ability.tooltip.<n>}) under an item's name.
 */
public final class TooltipListeners {
    public static Map<Holder<Item>, TooltipPredicate> PREDICATES = new HashMap<>();

    private TooltipListeners() {
    }

    public static void init() {
        ItemTooltipCallback.EVENT.register((stack, context, flag, components) ->
            addAbilityTooltips(Minecraft.getInstance().player, stack, components, context));
    }

    public static void addAbilityTooltips(@Nullable Player player, ItemStack stack, List<Component> components, Item.TooltipContext context) {
        for (int i = 1; i <= 5; i++) {
            String string = stack.getItem().getDescriptionId() + "." + Nitrogen.MODID + ".ability.tooltip." + i;
            if (Language.getInstance().has(string)) {
                Component component = Component.translatable(string);
                TooltipPredicate predicate = PREDICATES.get(stack.typeHolder());
                if (predicate != null) {
                    component = predicate.override(player, stack, components, context, component);
                }
                components.add(Math.min(i, components.size()), component);
            }
        }
    }

    @FunctionalInterface
    public interface TooltipPredicate {
        Component override(@Nullable Player player, ItemStack stack, List<Component> components, Item.TooltipContext context, Component component);
    }
}
