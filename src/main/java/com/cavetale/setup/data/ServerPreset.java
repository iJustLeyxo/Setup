package com.cavetale.setup.data;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;

import static com.cavetale.setup.data.Entry.SelectType.AUTO;

/** Server preset element class. */
public final class ServerPreset extends Entry {
    /** Contained plugin categories. */
    public final @NotNull PluginCategory @NotNull [] pluginCategories;

    /** Contained plugins. */
    public final @NotNull Plugin @NotNull [] plugins;

    /** Creates a server preset from raw data. */
    private ServerPreset(@NotNull Raw raw) {
        super(raw);

        if (raw.plugin_categories == null) pluginCategories = new PluginCategory[0];
        else {
            pluginCategories = new PluginCategory[raw.plugin_categories.length];
            int i = 0;
            for (String c : raw.plugin_categories)
                pluginCategories[i++] = PluginCategory.get(c);
        }

        if (raw.plugins == null) plugins = new Plugin[0];
        else {
            plugins = new Plugin[raw.plugins.length];
            int j = 0;
            for (String p : raw.plugins) plugins[j++] = Plugin.get(p);
        }
    }

    @Override
    public void selectAs(@NotNull Entry.SelectType type) {
        super.selectAs(type);
        for (PluginCategory c : pluginCategories) c.selectAs(AUTO);
    }

    @Override
    public boolean isInstalled() {
        for (PluginCategory c : pluginCategories) if (!c.isInstalled()) return false;
        for (Plugin p : plugins) if (!p.isInstalled()) return false;
        return true;
    }



    /** Resets data. */
    public static void resetAll() {
        for (ServerPreset s : get()) s.reset();
    }

    /** Loaded data. */
    private static @NotNull ServerPreset @Nullable [] serverPresets = null;

    /** Load server preset data if necessary. */
    public static @NotNull ServerPreset @NotNull [] get() {
        if (serverPresets == null) {
            Raw[] raw = load("server_presets", Raw[].class);
            serverPresets = new ServerPreset[raw.length];
            int i = 0;
            for (Raw r : raw) serverPresets[i++] = new ServerPreset(r);
        }

        return serverPresets;
    }

    /** Raw data. */
    protected static final class Raw extends Entry.Raw {
        public String[] plugins;
        public String[] plugin_categories;
    }

    /** Gets the matching element. */
    public static @NotNull ServerPreset get(@NotNull String ref) {
        return get(ref, get());
    }

    /** Queries plugin categories by similarity to a search term. */
    public static @NotNull Set<ServerPreset> search(@NotNull String search) {
        return search(search, get());
    }

    /** Queries selected elements. */
    public static @NotNull Set<ServerPreset> selected() {
        return selected(get());
    }

    /** Queries installed elements. */
    public static @NotNull Set<ServerPreset> installed() {
        Set<ServerPreset> result = new HashSet<>();
        for (ServerPreset s : get())
            if (s.isInstalled()) result.add(s);
        return result;
    }
}
