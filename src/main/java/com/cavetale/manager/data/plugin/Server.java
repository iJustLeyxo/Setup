package com.cavetale.manager.data.plugin;

import com.cavetale.manager.data.Sel;
import com.cavetale.manager.parser.InputException;
import com.cavetale.manager.util.Util;
import com.cavetale.manager.util.console.Console;
import com.cavetale.manager.util.console.Style;
import com.cavetale.manager.util.console.Type;
import com.cavetale.manager.util.console.XCode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static com.cavetale.manager.data.plugin.Category.BASE;
import static com.cavetale.manager.data.plugin.Category.SURVIVAL;

/**
 * Servers, used to group plugins by installed server
 */
public enum Server implements Provider {
    BUILD("Plugins for build servers", BASE, SURVIVAL, Category.BUILD, Category.HOME),
    CLASSIC("Plugins for classic servers", BASE, SURVIVAL, Category.BUILD),
    CREATIVE("Plugins for creative servers", BASE, Category.CREATIVE, Category.BUILD,
            Plugin.ENEMY, Plugin.FESTIVAL, Plugin.PICTIONARY, Plugin.RACE, Plugin.RESIDENT),
    EVENT("Plugins for event servers", BASE, Plugin.WORLDS),
    HUB("Plugins for hub servers", BASE, SURVIVAL, Category.BUILD, Category.HUB,
            Plugin.STRUCTURE, Plugin.EXTREME_GRASS_GROWING, Plugin.KING_OF_THE_LADDER, Plugin.RED_GREEN_LIGHT),
    MINE("Plugins for mine servers", BASE, SURVIVAL, Category.BUILD, Category.MINE),
    VOID("Plugins for void servers", BASE);

    private final @NotNull String name;
    private final @NotNull String info;
    private final @NotNull Server[] servers;
    private final @NotNull Category[] categories;
    private final @NotNull Plugin[] plugins;

    // TODO: Custom printing
    private @NotNull Sel sel = Sel.OFF;
    private @Nullable Boolean inst = null;

    Server(@NotNull String info, @NotNull Provider @NotNull ... providers) {
        this.name = Util.capsToCamel(this.name());
        this.info = info;

        Set<Server> servers = new HashSet<>();
        Set<Category> categories = new HashSet<>();
        for (Provider p : providers) {
            if (p instanceof Server s) servers.add(s);
            else if (p instanceof Category c) categories.add(c);
        }
        this.servers = servers.toArray(new Server[]{});
        this.categories = categories.toArray(new Category[]{});

        int len = 0; // Copy plugins
        for (Provider child : providers) len += child.plugins().length;
        this.plugins = new Plugin[len];
        len = 0;
        for (Provider child : providers) {
            System.arraycopy(child.plugins(), 0, this.plugins, len, child.plugins().length);
            len += child.plugins().length;
        }
    }

    public @NotNull String displayName() {
        return this.name;
    }

    @Override
    public @NotNull String toString() {
        return this.displayName();
    }

    public @NotNull Server[] servers() {
        return this.servers;
    }

    public @NotNull Category[] categories() {
        return this.categories;
    }

    @Override
    public @NotNull Plugin[] plugins() {
        return this.plugins;
    }

    public void reset() {
        this.sel = Sel.OFF;
        this.inst = null;
    }

    //= Selection ==

    public void target() {
        this.sel = Sel.TARGET;
    }

    public boolean isTargeted() {
        return this.sel == Sel.TARGET;
    }

    public void select() {
        if (this.sel == Sel.OFF) this.sel = Sel.ON;
    }

    public boolean isSelected() {
        return this.sel != Sel.OFF;
    }

    //= Installation ==

    public boolean isInstalled() {
        if (this.inst != null) return this.inst;
        return this.checkInstalled();
    }

    private boolean checkInstalled() {
        this.inst = true;

        for (Plugin p : this.plugins) if (!p.isInstalled()) {
            this.inst = false;
            return false;
        }

        for (Category c : this.categories) if (!c.isInstalled()) {
            this.inst = false;
            return false;
        }

        return true;
    }

    public static @NotNull Server get(@NotNull String ref) throws NotFoundException {
        for (Server s : Server.values()) if (s.displayName().equalsIgnoreCase(ref)) return s;
        throw new NotFoundException(ref);
    }

    public static void list() {
        Console.sep();
        Console.log(Type.REQUESTED, Style.SERVER, XCode.BOLD +
                "--------------------------------------- " +
                "Servers ---------------------------------------\n");
        Console.logF(Type.REQUESTED, Style.SERVER, "%-16s | %-68s\n", "Server", "Info");
        Console.log(Type.REQUESTED, Style.SERVER, "----------------------------------------------" +
                "-----------------------------------------\n");
        ArrayList<Server> servers = new ArrayList<>(List.of(Server.values()));
        Collections.sort(servers);
        for (Server s : servers) {
            Console.logF(Type.REQUESTED, Style.SERVER, "%-16s | %-68s\n", s.displayName(), s.info);
        }
    }

    public static class NotFoundException extends InputException {
        public NotFoundException(@NotNull String ref) {
            super("Server \"" + ref + "\" not found. Did you mean to use -\"S\" for --software?");
        }
    }
}