package com.cavetale.setup.data;

import com.cavetale.setup.cmd.CustomFlag;
import com.cavetale.setup.cmd.CustomStyle;
import com.cavetale.setup.cmd.ServerContents;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static com.cavetale.setup.data.PluginCategory.BASE;
import static com.cavetale.setup.data.PluginCategory.SURVIVAL;
import static link.l_pf.cmdlib.shell.Code.Std.BOLD;
import static link.l_pf.cmdlib.shell.Shell.STDIO;

/**
 * Servers, used to group plugins by installed server
 */
public enum PluginServer implements Provider {
    BUILD("Plugins for build servers", BASE, SURVIVAL, PluginCategory.BUILD,
        PluginCategory.HOME
    ),

    CLASSIC("Plugins for classic servers", BASE, SURVIVAL, PluginCategory.BUILD),

    CREATIVE("Plugins for creative servers", BASE, PluginCategory.CREATIVE,
        PluginCategory.BUILD, Plugin.ENEMY, Plugin.FESTIVAL, Plugin.PICTIONARY,
        Plugin.RACE, Plugin.RESIDENT
    ),

    EVENT("Plugins for event servers", BASE, Plugin.WORLDS),

    HUB("Plugins for hub servers", BASE, SURVIVAL, PluginCategory.BUILD,
        PluginCategory.HUB, Plugin.STRUCTURE, Plugin.EXTREME_GRASS_GROWING,
        Plugin.KING_OF_THE_LADDER, Plugin.RED_GREEN_LIGHT
    ),

    MINE("Plugins for mine servers", BASE, SURVIVAL, PluginCategory.BUILD,
        PluginCategory.MINE
    ),

    VOID("Plugins for void servers", BASE);

    private final @NotNull String name;
    private final @NotNull String info;
    private final @NotNull PluginServer[] servers;
    private final @NotNull PluginCategory[] categories;
    private final @NotNull Plugin[] plugins;

    private @Nullable Sel sel = null;
    private @Nullable Boolean inst = null;

    PluginServer(@NotNull String info, @NotNull Provider @NotNull ... providers) {
        this.name = Data.capsToCamel(this.name());
        this.info = info;

        Set<PluginServer> servers = new HashSet<>();
        Set<PluginCategory> categories = new HashSet<>();
        for (Provider p : providers) {
            if (p instanceof PluginServer s) servers.add(s);
            else if (p instanceof PluginCategory c) categories.add(c);
        }
        this.servers = servers.toArray(new PluginServer[]{});
        this.categories = categories.toArray(new PluginCategory[]{});

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

    public @NotNull PluginServer[] servers() {
        return this.servers;
    }

    public @NotNull PluginCategory[] categories() {
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
        if (this.sel == null) PluginServer.selected();
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

        for (PluginCategory c : this.categories) if (!c.isInstalled()) {
            this.inst = false;
            return;
        }
    }

    //= Finder ==

    public static @NotNull PluginServer get(@NotNull String ref) throws NotFoundException {
        for (PluginServer s : PluginServer.values()) if (s.displayName().equalsIgnoreCase(ref)) return s;
        throw new NotFoundException(ref);
    }

    //= Indexing ==

    private static @Nullable List<PluginServer> selected = null;
    private static @Nullable List<PluginServer> installed = null;

    public static @NotNull List<PluginServer> selected() {
        if (PluginServer.selected != null) return PluginServer.selected; // Update not necessary

        // Update selected
        STDIO.debug("Reloading selected servers");
        for (PluginServer s : PluginServer.values()) s.deselect();
        PluginServer.selected = new LinkedList<>();

        ServerContents servers = (ServerContents) CustomFlag.SERVER.container();
        if (CustomFlag.INSTALLED.isSelected()) {
            for (PluginServer s : PluginServer.installed()) s.target();
            STDIO.debug("Selecting installed servers");
        } else if (CustomFlag.ALL.isSelected() || (CustomFlag.SERVER.isSelected() && servers.isEmpty())) { // Select all
            STDIO.debug("Selecting all servers");
            for (PluginServer s : PluginServer.values()) s.target();
        } else {
            STDIO.debug("Selecting servers ", servers.contents());
            for (PluginServer s : servers.contents()) s.target(); // Select by server
        }

        for (PluginServer s : PluginServer.values()) if (s.isSelected()) PluginServer.selected.add(s); // Update selection
        return PluginServer.selected;
    }

    public static @NotNull List<PluginServer> installed() {
        if (PluginServer.installed != null) return PluginServer.installed; // Update not necessary

        // Update installed
        STDIO.debug("Reloading installed servers");
        PluginServer.installed = new LinkedList<>();
        for (PluginServer s : PluginServer.values()) if (s.isInstalled()) PluginServer.installed.add(s); // Update installation
        return PluginServer.installed;
    }

    public static void reset() {
        STDIO.debug("Resetting servers");
        for (PluginServer s : PluginServer.values()) s.revert();
        PluginServer.selected = null;
        PluginServer.installed = null;
    }

    public static @NotNull List<PluginServer> get(@Nullable Boolean installed, @Nullable Boolean selected) {
        List<PluginServer> servers = new LinkedList<>();
        for (PluginServer s : PluginServer.values()) {
            if ((installed == null || installed == s.isInstalled()) &&
                    (selected == null || selected == s.isSelected())) {
                servers.add(s);
            }
        }
        return servers;
    }

    //= Cosmetics ==

    public static void requestAll() {
        if (PluginServer.values().length == 0) {
            STDIO.log(CustomStyle.SERVER, BOLD, "No servers available");
            return;
        }

        STDIO.list(CustomStyle.SERVER, PluginServer.values().length +
                " server(s) available", (Object[]) PluginServer.values());
    }

    public static void listSelected() {
        if (PluginServer.selected().isEmpty()) {
            STDIO.log(CustomStyle.SERVER, BOLD, "No servers selected");
            return;
        }

        STDIO.list(CustomStyle.SERVER, PluginServer.selected().size() +
                " server(s) selected", PluginServer.selected().toArray());
    }

    public static void requestInstalled() {
        if (PluginServer.installed().isEmpty()) {
            STDIO.log(CustomStyle.SERVER, BOLD, "No servers installed");
            return;
        }

        STDIO.list(CustomStyle.SERVER, PluginServer.installed().size() +
                " server(s) installed", PluginServer.installed().toArray());
    }

    public static class NotFoundException extends Exception {
        public NotFoundException(@NotNull String ref) {
            super("Server \"" + ref + "\" not found. Did you mean to use \"-" + CustomFlag.SOFTWARE.ref() + "\" for \"--" + CustomFlag.SOFTWARE.reference() + "\"?");
        }
    }
}