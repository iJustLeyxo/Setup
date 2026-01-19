package com.cavetale.setup.data;

import com.cavetale.setup.cmd.CustomFlag;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import static java.lang.Math.max;
import static java.lang.Math.min;

/** Base for handled data entries. */
public abstract class Entry {
    /** Unique reference. */
    public final @NotNull String ref;
    /** Unique aliases. */
    public final @NotNull String @NotNull [] aliases;

    /** Selection state. */
    private @Nullable Entry.SelectType selected = null;

    /** Create from raw data. */
    protected Entry(@NotNull Raw raw) {
        ref = raw.ref;
        aliases = raw.aliases == null ? new String[0] : raw.aliases;

        if (ref == null) throw new RuntimeException("Entry ref not set");
    }

    /** Reset state. */
    public void reset() {
        this.selected = null;
    }

    @Override
    public final @NotNull String toString() {
        return ref;
    }

    @Override
    public final boolean equals(@Nullable Object o) {
        if (o instanceof Entry e) return ref.equalsIgnoreCase(e.ref);
        return false;
    }

    /** Whether selected. */
    public final boolean isSelected() {
        return
                selected != null ||
                CustomFlag.ALL.isSelected() ||
                isInstalled() && CustomFlag.INSTALLED.isSelected();
    }

    /** Sets to selected. */
    public void selectAs(@NotNull SelectType type) {
        if (selected == null || type == SelectType.USER) selected = type;
    }

    /** Selection type. */
    public enum SelectType {
        /** Manually selected by user. */
        USER,
        /** Automatically selected by parent. */
        AUTO
    }

    /** {@code true} only if installed. */
    public abstract boolean isInstalled();



    /** Generic raw data loader. */
    protected static <D> D load(@NotNull String name, @NotNull Class<D> type) {
        String file = name + ".json";

        try {
            return new ObjectMapper().readValue(Entry.class.getResource(file), type);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load \"" + file + "\"", e);
        }
    }

    /** Generic raw data. */
    protected static abstract sealed class Raw
            permits Source.Raw, PluginCategory.Raw, ServerPreset.Raw {
        public String ref;
        public String[] aliases;
    }

    /** Gets the matching entry. */
    protected static <E extends Entry> @NotNull E get(
        @NotNull String ref,
        @NotNull E @NotNull [] elements
    ) {
        for (E e : elements) {
            if (e.ref.equalsIgnoreCase(ref)) return e;
            for (String a : e.aliases) if (a.equalsIgnoreCase(ref)) return e;
        }

        throw new UnknownException(ref);
    }

    /** Thrown if a requested entry does not exist. */
    public static final class UnknownException extends RuntimeException {
        public UnknownException(@NotNull String ref) {
            super("Unknown reference \"" + ref + "\"");
        }
    }

    /** Minimum similarity for search results. */
    private static final double MAX_DISTANCE = 0.5;

    /** Gets similar entries to the search term. */
    protected static <E extends Entry> @NotNull Set<E> search(
        @NotNull String search,
        @NotNull E @NotNull [] elements
    ) {
        Set<E> result = new HashSet<>();

        for (E e : elements) {
            if (distance(search, e.ref) <= MAX_DISTANCE) result.add(e);
            else for (String a : e.aliases)
                if (distance(search, a) <= MAX_DISTANCE) {
                    result.add(e);
                    break;
                }
        }

        return result;
    }

    /** Damerau-Levenshtein-distance (normalized). */
    public static double distance(@NotNull String one, @NotNull String two) {
        char[] a = one.toCharArray();
        char[] b = two.toCharArray();
        int[][] d = new int[a.length + 1][b.length + 1];
        for (int i = 0; i <= a.length; i++) d[i][0] = i;
        for (int i = 0; i <= b.length; i++) d[0][i] = i;

        for (int i = 1; i <= a.length; i++)
            for (int j = 1; j <= b.length; j++) {
                int cost = a[i - 1] == b[j - 1] ? 0 : 1;
                d[i][j] = min(
                        min(
                            d[i - 1][j] + 1,
                            d[i][j - 1] + 1
                        ),
                        d[i - 1][j - 1] + cost
                );

                if (i <= 1 || j <= 1) continue;
                if (a[i - 1] == b[j - 2] && a[i - 2] == b[j - 1])
                    d[i][j] = min(d[i][j], d[i - 2][j - 2] + cost);
            }

        return (double) d[a.length][b.length] / max(1, max(a.length, b.length));
    }

    /** Queries selected entries. */
    protected static <E extends Entry> @NotNull Set<E> selected(
        @NotNull E @NotNull [] entries
    ) {
        Set<E> result = new HashSet<>();
        for (E e : entries)
            if (e.isSelected()) result.add(e);
        return result;
    }
}
