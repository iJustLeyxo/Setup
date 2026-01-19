package com.cavetale.setup.data;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.Set;

/** Plugin element class. */
public final class Plugin extends Source {
    /** Plugins directory. */
    public static final @NotNull File PLUGINS_DIR = new File("plugins");

    /** Creates element from raw (loaded) data. */
    private Plugin(@NotNull Raw raw) {
        super(raw);
    }

    @Override
    protected void load() {
        loadInstallations();
    }



    /** Resets data. */
    public static void resetAll() {
        for (Plugin p : get()) p.reset();
        unknown = null;
    }

    /** Loaded data. */
    private static @NotNull Plugin @Nullable [] plugins = null;

    /** Load plugins data if necessary. */
    public static @NotNull Plugin @NotNull [] get() {
        if (plugins == null) {
            Raw[] raw = load("plugins", Raw[].class);
            plugins = new Plugin[raw.length];
            int i = 0;
            for (Raw r : raw) plugins[i++] = new Plugin(r);
        }

        return plugins;
    }

    /** Summary of unknown elements. */
    private static @Nullable Source.Unknown unknown = null;

    /** Loads installed elements. */
    public static void loadInstallations() {
        unknown = load(PLUGINS_DIR, get());
    }

    @Override
    protected @NotNull File dir() {
        return PLUGINS_DIR;
    }

    /** Gets the matching element. */
    public static @NotNull Plugin get(@NotNull String ref) {
        return get(ref, get());
    }

    /** Gets the associated element. */
    public static @NotNull Plugin get(@NotNull File file) {
        return get(file, get());
    }

    /** Gets similar elements to the search term. */
    public static @NotNull Set<Plugin> search(@NotNull String search) {
        return search(search, get());
    }

    /** Queries selected elements. */
    public static @NotNull Set<Plugin> selected() {
        return selected(get());
    }

    /** Queries installed elements. */
    public static @NotNull Set<Plugin> installed() {
        return selected(null, true, get());
    }

    /** Queries uninstalled elements. */
    public static @NotNull Set<Plugin> uninstalled() {
        return selected(null, false, get());
    }

    /** Queries matching (installed and selected) elements. */
    public static @NotNull Set<Plugin> matching() {
        return selected(true, true, get());
    }

    /** Queries superfluous (installed and not selected) elements. */
    public static @NotNull Set<Plugin> superfluous() {
        return selected(false, true, get());
    }

    /** Queries all missing (not installed but selected) elements. */
    public static @NotNull Set<Plugin> missing() {
        return selected(true, false, get());
    }

    /** Queries all unknown elements. */
    public static @NotNull Source.Unknown unknown() {
        if (unknown == null) loadInstallations();
        return unknown;
    }
}
