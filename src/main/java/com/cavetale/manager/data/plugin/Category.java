package com.cavetale.manager.data.plugin;

import com.cavetale.manager.parser.InputException;
import com.cavetale.manager.util.console.Console;
import com.cavetale.manager.util.console.Style;
import com.cavetale.manager.util.console.Type;
import com.cavetale.manager.util.console.XCode;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * Plugin categories, used to group plugins by purpose / usage
 */
public enum Category implements Provider {
    GLOBAL("Global plugins"),
    BUILD("Plugins for build servers"),
    CORE("Essential plugins"),
    CREATIVE("Plugins for creative servers"),
    DEPRECATED("Deprecated plugins"),
    EVENT("Plugins for events without a dedicated server"),
    HOME("Plugins for home servers"),
    HUB("Plugins for hub servers"),
    MINE("Plugins for mine servers"),
    MINI_GAME("minigame", "Plugins for mini game servers"),
    SEASONAL("Seasonal event plugins"),
    SURVIVAL("Plugins for survival servers"),
    UTIL("Optional utility plugins"),
    WORLD_GEN("worldgen", "World generation plugins");

    public final @NotNull String ref;
    public final @NotNull String info;

    Category(@NotNull String info) {
        this.ref = this.name().toLowerCase();
        this.info = info;
    }

    Category(@NotNull String ref, @NotNull String info) {
        this.ref = ref;
        this.info = info;
    }

    @Override
    public Set<Plugin> plugins() {
        Set<Plugin> result = new HashSet<>();
        for (Plugin p : Plugin.values()) {
            if (Arrays.asList(p.categories).contains(this)) result.add(p);
        }
        return result;
    }

    @Override
    public @NotNull String toString() {
        return this.ref;
    }

    public static Category get(@NotNull String ref) throws NotFoundException {
        for (Category c : values()) {
            if (c.ref.equalsIgnoreCase(ref)) return c;
        }
        throw new NotFoundException(ref);
    }

    public static void list() {
        Console.sep();
        Console.log(Type.REQUESTED, Style.CATEGORY, XCode.BOLD +
                "-------------------------------------- " +
                "Categories -------------------------------------\n");
        Console.logF(Type.REQUESTED, Style.CATEGORY, "%-16s | %-68s\n", "Category", "Info");
        Console.log(Type.REQUESTED, Style.CATEGORY, "--------------------------------------------" +
                "-------------------------------------------\n");
        ArrayList<Category> categories = new ArrayList<>(List.of(Category.values()));
        Collections.sort(categories);
        for (Category c : categories) {
            Console.logF(Type.REQUESTED, Style.CATEGORY, "%-16s | %-68s\n", c.ref, c.info);
        }
    }

    public static final class NotFoundException extends InputException {
        public NotFoundException(@NotNull String name) {
            super("Category \"" + name + "\" not found");
        }
    }
}