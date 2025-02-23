package com.cavetale.manager.data.plugin;

import com.cavetale.manager.data.Sel;
import com.cavetale.manager.parser.Flag;
import com.cavetale.manager.parser.InputException;
import com.cavetale.manager.parser.Parser;
import com.cavetale.manager.parser.container.ServerContainer;
import com.cavetale.manager.util.Util;
import com.cavetale.manager.util.console.Console;
import com.cavetale.manager.util.console.Style;
import com.cavetale.manager.util.console.Type;
import com.cavetale.manager.util.console.Code;
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

    //= Static ==

    // TODO: Only resolve when requested
    private static final @NotNull List<Server> selected = new LinkedList<>();
    private static final @NotNull List<Server> installed = new LinkedList<>();

    public static @NotNull Server get(@NotNull String ref) throws NotFoundException {
        for (Server s : Server.values()) if (s.displayName().equalsIgnoreCase(ref)) return s;
        throw new NotFoundException(ref);
    }

    public static void reloadSelected(@NotNull Parser parser) {
        Console.log(Type.EXTRA, "Reloading selected servers\n");
        for (Server s : Server.values()) s.reset(); // Reset selections
        Server.selected.clear();

        ServerContainer servers = (ServerContainer) Flag.SERVER.container();
        if (Flag.INSTALLED.isSelected()) {
            for (Server s : Server.installed) s.target();
            Console.log(Type.DEBUG, "Selecting installed servers\n");
        } else if (Flag.ALL.isSelected() || (Flag.SERVER.isSelected() && servers.isEmpty())) { // Select all
            Console.log(Type.DEBUG, "Selecting all servers\n");
            for (Server s : Server.values()) s.target();
        } else {
            Console.log(Type.DEBUG, "Selecting servers " + servers.get() + "\n");
            for (Server s : servers.get()) s.target(); // Select by server
        }

        for (Server s : Server.values()) if (s.isSelected()) Server.selected.add(s); // Update selection
    }

    public static void reloadInstallations() {
        Console.log(Type.EXTRA, "Reloading installed servers\n");
        Server.installed.clear(); // Reset installations

        for (Server s : Server.values()) if (s.isInstalled()) Server.installed.add(s); // Update installation
    }

    public static @NotNull List<Server> get(@Nullable Boolean installed, @Nullable Boolean selected) {
        List<Server> servers = new LinkedList<>();
        for (Server s : Server.values()) {
            if ((installed == null || installed == s.isInstalled()) &&
                    (selected == null || selected == s.isSelected())) {
                servers.add(s);
            }
        }
        return servers;
    }

    public static @NotNull List<Server> installed() {
        return Server.installed;
    }

    public static @NotNull List<Server> selected() {
        return Server.selected;
    }

    public static void summarize() {
        if (!Server.selected.isEmpty()) Server.summarizeSelected();
        else if (!Server.installed.isEmpty()) Server.summarizeInstalled();
        else {
            Console.sep();
            Console.log(Type.REQUESTED, Style.SERVER, Code.BOLD + "No servers selected or installed\n");
        }
    }

    private static void summarizeSelected() {
        Console.sep();
        List<Server> selected = Server.selected;
        Console.logL(Type.REQUESTED, Style.SELECT, selected.size() +
                " server(s) selected", 4, 21, selected.toArray());
        selected = Server.get(true, true);
        if (!selected.isEmpty()) {
            Console.sep();
            Console.logL(Type.REQUESTED, Style.INSTALL, selected.size() +
                    " server(s) installed", 4, 21, selected.toArray());
        }
        selected = Server.get(true, false);
        if (!selected.isEmpty()) {
            Console.sep();
            Console.logL(Type.REQUESTED, Style.SUPERFLUOUS, selected.size() +
                    " server(s) superfluous", 4, 21, selected.toArray());
        }
        selected = Server.get(false, true);
        if (!selected.isEmpty()) {
            Console.sep();
            Console.logL(Type.REQUESTED, Style.MISSING, selected.size() +
                    " server(s) missing", 4, 21, selected.toArray());
        }
    }

    private static void summarizeInstalled() {
        Console.sep();
        Console.logL(Type.REQUESTED, Style.INSTALL, Server.installed.size() + " server(s) installed", 4, 21, Server.installed.toArray());
    }

    public static void listSelected() {
        if (Server.selected.isEmpty()) {
            Console.sep();
            Console.log(Type.REQUESTED, Style.SERVER, Code.BOLD + "No servers selected\n");
            return;
        }

        Console.sep();
        Console.logL(Type.REQUESTED, Style.SERVER, Server.selected.size() + " server(s) selected", 4, 21, Server.selected.toArray());
    }

    public static void listInstalled() {
        if (Server.installed.isEmpty()) {
            Console.sep();
            Console.log(Type.REQUESTED, Style.SERVER, Code.BOLD + "No servers installed\n");
            return;
        }

        Console.sep();
        Console.logL(Type.REQUESTED, Style.SERVER, Server.installed.size() + " server(s) installed", 4, 21, Server.installed.toArray());
    }

    public static void shortList() {
        Console.sep();
        Console.logL(Type.REQUESTED, Style.SERVER, Server.values().length + " server(s) available", 4, 21, (Object[]) Server.values());
    }

    public static void longList() {
        Console.sep();
        Console.log(Type.REQUESTED, Style.SERVER, Code.BOLD +
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