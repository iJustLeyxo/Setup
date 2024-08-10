package com.cavetale.manager.data;

import com.cavetale.manager.parser.InputException;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public enum PluginCategory implements PluginProvider {
    GLOBAL("Global plugins"),
    BUILD("Plugins for build serverSoftware"),
    CORE("Essential plugins"),
    CREATIVE("Plugins for creative serverSoftware"),
    DEPRECATED("Deprecated plugins"),
    EVENT("Plugins for events without a dedicated server"),
    HOME("Plugins for home serverSoftware"),
    HUB("Plugins for hub serverSoftware"),
    MINE("Plugins for mine serverSoftware"),
    MINI_GAME("minigame", "Plugins for mini game serverSoftware"),
    SEASONAL("Seasonal event plugins"),
    SURVIVAL("Plugins for survival serverSoftware"),
    UTIL("Optional utility plugins"),
    WORLD_GEN("worldgen", "World generation plugins");

    public final @NotNull String ref;
    public final @NotNull String info;

    PluginCategory(@NotNull String info) {
        this.ref = this.name().toLowerCase();
        this.info = info;
    }

    PluginCategory(@NotNull String ref, @NotNull String info) {
        this.ref = ref;
        this.info = info;
    }

    @Override
    public Set<Plugin> plugins() {
        Set<Plugin> result = new HashSet<>();
        for (Plugin p : Plugin.values()) {
            if (Arrays.asList(p.categories).contains(this)) {
                result.add(p);
            }
        }
        return result;
    }

    @Override
    public @NotNull String toString() {
        return this.ref;
    }

    public static PluginCategory get(@NotNull String ref) throws NotFoundException {
        for (PluginCategory c : values()) {
            if (c.ref.equalsIgnoreCase(ref)) {
                return c;
            }
        }
        throw new NotFoundException(ref);
    }

    public static final class NotFoundException extends InputException {
        public NotFoundException(@NotNull String name) {
            super("Category \"" + name + "\" not found");
        }
    }
}