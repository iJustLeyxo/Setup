package com.cavetale.manager.data.plugin;

import com.cavetale.manager.parser.InputException;
import com.cavetale.manager.util.console.Console;
import com.cavetale.manager.util.console.Style;
import com.cavetale.manager.util.console.Type;
import com.cavetale.manager.util.console.XCode;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static com.cavetale.manager.data.plugin.Category.Base;
import static com.cavetale.manager.data.plugin.Category.Survival;

/**
 * Servers, used to group plugins by installed server
 */
public enum Server implements Provider {
    Build("Plugins for build servers", Base, Survival, Category.Build, Category.Home),
    Classic("Plugins for classic servers", Base, Survival, Category.Build),
    Creative("Plugins for creative servers", Base, Category.Creative, Category.Build,
            Plugin.Enemy, Plugin.Festival, Plugin.Pictionary, Plugin.Race, Plugin.Resident),
    Event("Plugins for event servers", Base, Plugin.Worlds),
    Hub("Plugins for hub servers", Base, Survival, Category.Build, Category.Hub,
            Plugin.Structure, Plugin.ExtremeGrassGrowing, Plugin.KingOfTheLadder, Plugin.RedGreenLight),
    Mine("Plugins for mine servers", Base, Survival, Category.Build, Category.Mine),
    Void("Plugins for void servers", Base);

    // TODO: Explicit
    private final @NotNull String info;
    private final @NotNull Server[] servers;
    private final @NotNull Category[] categories;
    private final @NotNull Plugin[] plugins;

    private boolean selected = false;
    private boolean installed = false;

    Server(@NotNull String info, @NotNull Provider @NotNull ... providers) {
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

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public boolean isSelected() {
        return this.selected;
    }

    public void setInstalled() {
        this.installed = true;

        for (Plugin p : this.plugins) {
            if (!p.isInstalled()) {
                this.installed = false;
                return;
            }
        }

        for (Category c : this.categories) {
            if (!c.isInstalled()) {
                this.installed = false;
                return;
            }
        }
    }

    public boolean isInstalled() {
        return this.installed;
    }

    public static @NotNull Server get(@NotNull String ref) throws NotFoundException {
        for (Server s : Server.values()) if (s.name().equalsIgnoreCase(ref)) return s;
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
            Console.logF(Type.REQUESTED, Style.SERVER, "%-16s | %-68s\n", s.name(), s.info);
        }
    }

    public static class NotFoundException extends InputException {
        public NotFoundException(@NotNull String ref) {
            super("Server \"" + ref + "\" not found. Did you mean to use -\"S\" for --software?");
        }
    }
}