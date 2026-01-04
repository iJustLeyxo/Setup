package com.cavetale.setup.data.plugin;

import com.cavetale.setup.console.CustomFlag;
import com.cavetale.setup.console.CustomStyle;
import com.cavetale.setup.console.container.ServerContents;
import com.cavetale.setup.data.Sel;
import com.cavetale.setup.util.Util;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static com.cavetale.setup.data.plugin.Category.BASE;
import static com.cavetale.setup.data.plugin.Category.SURVIVAL;
import static link.l_pf.cmdlib.shell.Code.Std.BOLD;
import static link.l_pf.cmdlib.shell.Shell.STDIO;

/**
 * Servers, used to group plugins by installed server
 */
public enum Server implements Provider {
    BUILD("Plugins for build servers", BASE, SURVIVAL, Category.BUILD,
        Category.HOME
    ),

    CLASSIC("Plugins for classic servers", BASE, SURVIVAL, Category.BUILD),

    CREATIVE("Plugins for creative servers", BASE, Category.CREATIVE,
        Category.BUILD, Plugin.ENEMY, Plugin.FESTIVAL, Plugin.PICTIONARY,
        Plugin.RACE, Plugin.RESIDENT
    ),

    EVENT("Plugins for event servers", BASE, Plugin.WORLDS),

    HUB("Plugins for hub servers", BASE, SURVIVAL, Category.BUILD,
        Category.HUB, Plugin.STRUCTURE, Plugin.EXTREME_GRASS_GROWING,
        Plugin.KING_OF_THE_LADDER, Plugin.RED_GREEN_LIGHT
    ),

    MINE("Plugins for mine servers", BASE, SURVIVAL, Category.BUILD,
        Category.MINE
    ),

    VOID("Plugins for void servers", BASE);

    private final @NotNull String name;
    private final @NotNull String info;
    private final @NotNull Server[] servers;
    private final @NotNull Category[] categories;
    private final @NotNull Plugin[] plugins;

    private @Nullable Sel sel = null;
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

    public void revert() {
        this.sel = null;
        this.inst = null;
    }

    //= Selection ==

    public void target() {
        this.sel = Sel.TARGET;
    }

    public void select() {
        if (this.sel != Sel.TARGET) this.sel = Sel.ON;
    }

    public void deselect() {
        this.sel = Sel.OFF;
    }

    public boolean isSelected() {
        if (this.sel == null) Server.selected();
        return this.sel != Sel.OFF;
    }

    //= Installation ==

    public boolean isInstalled() {
        if (this.inst == null) this.checkInstalled();
        return this.inst;
    }

    private void checkInstalled() {
        this.inst = true;

        for (Plugin p : this.plugins) if (!p.isInstalled()) {
            this.inst = false;
            return;
        }

        for (Category c : this.categories) if (!c.isInstalled()) {
            this.inst = false;
            return;
        }
    }

    //= Finder ==

    public static @NotNull Server get(@NotNull String ref) throws NotFoundException {
        for (Server s : Server.values()) if (s.displayName().equalsIgnoreCase(ref)) return s;
        throw new NotFoundException(ref);
    }

    //= Indexing ==

    private static @Nullable List<Server> selected = null;
    private static @Nullable List<Server> installed = null;

    public static @NotNull List<Server> selected() {
        if (Server.selected != null) return Server.selected; // Update not necessary

        // Update selected
        STDIO.debug("Reloading selected servers");
        for (Server s : Server.values()) s.deselect();
        Server.selected = new LinkedList<>();

        ServerContents servers = (ServerContents) CustomFlag.SERVER.container();
        if (CustomFlag.INSTALLED.isSelected()) {
            for (Server s : Server.installed()) s.target();
            STDIO.debug("Selecting installed servers");
        } else if (CustomFlag.ALL.isSelected() || (CustomFlag.SERVER.isSelected() && servers.isEmpty())) { // Select all
            STDIO.debug("Selecting all servers");
            for (Server s : Server.values()) s.target();
        } else {
            STDIO.debug("Selecting servers ", servers.contents());
            for (Server s : servers.contents()) s.target(); // Select by server
        }

        for (Server s : Server.values()) if (s.isSelected()) Server.selected.add(s); // Update selection
        return Server.selected;
    }

    public static @NotNull List<Server> installed() {
        if (Server.installed != null) return Server.installed; // Update not necessary

        // Update installed
        STDIO.debug("Reloading installed servers");
        Server.installed = new LinkedList<>();
        for (Server s : Server.values()) if (s.isInstalled()) Server.installed.add(s); // Update installation
        return Server.installed;
    }

    public static void reset() {
        STDIO.debug("Resetting servers");
        for (Server s : Server.values()) s.revert();
        Server.selected = null;
        Server.installed = null;
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

    //= Cosmetics ==

    public static void requestAll() {
        if (Server.values().length == 0) {
            STDIO.log(CustomStyle.SERVER, BOLD, "No servers available");
            return;
        }

        STDIO.list(CustomStyle.SERVER, Server.values().length +
                " server(s) available", (Object[]) Server.values());
    }

    public static void listSelected() {
        if (Server.selected().isEmpty()) {
            STDIO.log(CustomStyle.SERVER, BOLD, "No servers selected");
            return;
        }

        STDIO.list(CustomStyle.SERVER, Server.selected().size() +
                " server(s) selected", Server.selected().toArray());
    }

    public static void requestInstalled() {
        if (Server.installed().isEmpty()) {
            STDIO.log(CustomStyle.SERVER, BOLD, "No servers installed");
            return;
        }

        STDIO.list(CustomStyle.SERVER, Server.installed().size() +
                " server(s) installed", Server.installed().toArray());
    }

    public static class NotFoundException extends Exception {
        public NotFoundException(@NotNull String ref) {
            super("Server \"" + ref + "\" not found. Did you mean to use \"-" + CustomFlag.SOFTWARE.ref() + "\" for \"--" + CustomFlag.SOFTWARE.reference() + "\"?");
        }
    }
}