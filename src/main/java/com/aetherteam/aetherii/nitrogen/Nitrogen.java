package com.aetherteam.aetherii.nitrogen;

import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

/**
 * Constants for the parts of the Nitrogen library (The Aether Team, LGPL-3.0) that are bundled into The Aether II.
 * Nitrogen has no Fabric release for this Minecraft version, so the classes The Aether II uses live in this package.
 */
public final class Nitrogen {
    /**
     * Kept as Nitrogen's original id so existing translation keys (e.g. ability tooltips) stay the same.
     */
    public static final String MODID = "nitrogen_internals";
    public static final Logger LOGGER = LogUtils.getLogger();

    private Nitrogen() {
    }
}
