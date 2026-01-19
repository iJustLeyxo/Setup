package com.cavetale.setup.data;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.Set;

/** Server software element class. */
public final class ServerSoftware extends Source {
    /** Server directory. */
    public static final @NotNull File SERVER_DIR = new File(".");

    /** Creates element from raw (loaded) data. */
    private ServerSoftware(@NotNull Raw raw) {
        super(raw);
    }

    @Override
    protected void load() {
        loadInstallations();
    }

    @Override
    protected @NotNull File dir() {
        return SERVER_DIR;
    }



    /** Resets data. */
    public static void resetAll() {
        for (ServerSoftware s : get()) s.reset();
        unknown = null;
    }

    /** Loaded data. */
    private static @NotNull ServerSoftware @Nullable [] serverSoftware = null;

    /** Load server software data if necessary. */
    public static @NotNull ServerSoftware @NotNull [] get() {
        if (serverSoftware == null) {
            Raw[] raw = load("server_software", Raw[].class);
            serverSoftware = new ServerSoftware[raw.length];
            int i = 0;
            for (Raw r : raw) serverSoftware[i++] = new ServerSoftware(r);
        }

        return serverSoftware;
    }

    /** Summary of unknown elements. */
    private static @Nullable Source.Unknown unknown = null;

    /** Loads installed elements. */
    public static void loadInstallations() {
        unknown = load(SERVER_DIR, get());
    }

    /** Gets the matching element. */
    public static @NotNull ServerSoftware get(@NotNull String ref) {
        return get(ref, get());
    }

    /** Gets the associated element. */
    public static @NotNull ServerSoftware get(@NotNull File file) {
        return get(file, get());
    }

    /** Gets similar elements to the search term. */
    public static @NotNull Set<ServerSoftware> search(@NotNull String search) {
        return search(search, get());
    }

    /** Queries selected elements. */
    public static @NotNull Set<ServerSoftware> selected() {
        return selected(get());
    }

    /** Queries installed elements. */
    public static @NotNull Set<ServerSoftware> installed() {
        return selected(null, true, get());
    }

    /** Queries uninstalled elements. */
    public static @NotNull Set<ServerSoftware> uninstalled() {
        return selected(null, false, get());
    }

    /** Queries matching (installed and selected) elements. */
    public static @NotNull Set<ServerSoftware> matching() {
        return selected(true, true, get());
    }

    /** Queries superfluous (installed and not selected) elements. */
    public static @NotNull Set<ServerSoftware> superfluous() {
        return selected(false, true, get());
    }

    /** Queries all missing (not installed but selected) elements. */
    public static @NotNull Set<ServerSoftware> missing() {
        return selected(true, false, get());
    }

    /** Queries all unknown elements. */
    public static @NotNull Source.Unknown unknown() {
        if (unknown == null) loadInstallations();
        return unknown;
    }
}
