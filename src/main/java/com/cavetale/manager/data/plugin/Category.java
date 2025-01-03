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
    Global("Global plugins"),
    Build("Plugins for build servers"),
    Core("Essential plugins"),
    Creative("Plugins for creative servers"),
    Deprecated("Deprecated plugins"),
    Event("Plugins for events without a dedicated server"),
    Home("Plugins for home servers"),
    Hub("Plugins for hub servers"),
    Mine("Plugins for mine servers"),
    MiniGame("minigame", "Plugins for mini game servers"),
    Seasonal("Seasonal event plugins"),
    Survival("Plugins for survival servers"),
    Util("Optional utility plugins"),
    WorldGen("World generation plugins");

    public final @NotNull String info;

    Category(@NotNull String info) {
        this.info = info;
    }

    Category(@NotNull String ref, @NotNull String info) {
        this.info = info;
    }

    @Override
    public Set<Plugin> plugins() {
        Set<Plugin> result = new HashSet<>();
        for (Plugin p : Plugin.values()) if (Arrays.asList(p.categories).contains(this)) result.add(p);
        return result;
    }

    @Override
    public @NotNull String toString() {
        return this.name();
    }

    public static @NotNull Category get(@NotNull String ref) throws NotFoundException {
        for (Category c : values()) if (c.name().equalsIgnoreCase(ref)) return c;
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
        for (Category c : categories) Console.logF(Type.REQUESTED, Style.CATEGORY, "%-16s | %-68s\n", c.name(), c.info);
    }

    public static final class NotFoundException extends InputException {
        public NotFoundException(@NotNull String name) {
            super("Category \"" + name + "\" not found");
        }
    }
}