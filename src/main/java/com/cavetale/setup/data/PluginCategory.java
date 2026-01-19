package com.cavetale.setup.data;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;

import static com.cavetale.setup.data.Entry.SelectType.AUTO;

/** Plugin category element class. */
public final class PluginCategory extends Entry {
    /** Contained plugins. */
    public final @NotNull Plugin @NotNull [] plugins;

    /** Creates a plugin category from raw data. */
    private PluginCategory(@NotNull Raw raw) {
        super(raw);
        if (raw.plugins == null) throw new RuntimeException("Plugins of \"" + ref + "\" not set");
        plugins = new Plugin[raw.plugins.length];
        int i = 0;
        for (String p : raw.plugins) plugins[i++] = Plugin.get(p);
    }

    @Override
    public void selectAs(@NotNull Entry.SelectType type) {
        super.selectAs(type);
        for (Plugin p : plugins) p.selectAs(AUTO);
    }

    @Override
    public boolean isInstalled() {
        for (Plugin p : plugins) if (!p.isInstalled()) return false;
        return true;
    }



    /** Resets data. */
    public static void resetAll() {
        for (PluginCategory c : get()) c.reset();
    }

    /** Loaded data. */
    private static @NotNull PluginCategory @Nullable [] pluginCategories = null;

    /** Load plugin category data if necessary. */
    public static @NotNull PluginCategory @NotNull [] get() {
        if (pluginCategories == null) {
            Raw[] raw = load("plugin_categories", Raw[].class);
            pluginCategories = new PluginCategory[raw.length];
            int i = 0;
            for (Raw r : raw) pluginCategories[i++] = new PluginCategory(r);
        }

        return pluginCategories;
    }

    /** Raw data. */
    protected static final class Raw extends Entry.Raw {
        public String[] plugins;
    }

    /** Gets the matching element. */
    public static @NotNull PluginCategory get(@NotNull String ref) {
        return get(ref, get());
    }

    /** Queries plugin categories by similarity to a search term. */
    public static @NotNull Set<PluginCategory> search(@NotNull String search) {
        return search(search, get());
    }

    /** Queries selected elements. */
    public static @NotNull Set<PluginCategory> selected() {
        return selected(get());
    }

    /** Queries installed elements. */
    public static @NotNull Set<PluginCategory> installed() {
        Set<PluginCategory> result = new HashSet<>();
        for (PluginCategory c : get())
            if (c.isInstalled()) result.add(c);
        return result;
    }
}
