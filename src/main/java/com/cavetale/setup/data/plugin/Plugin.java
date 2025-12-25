package com.cavetale.setup.data.plugin;

import com.cavetale.setup.console.CustomFlag;
import com.cavetale.setup.console.CustomStyle;
import com.cavetale.setup.console.container.PluginContents;
import com.cavetale.setup.data.DataException;
import com.cavetale.setup.data.Installable;
import com.cavetale.setup.data.Sel;
import com.cavetale.setup.download.Jenkins;
import com.cavetale.setup.download.Source;
import com.cavetale.setup.util.Util;
import link.l_pf.cmdlib.io.Code;
import link.l_pf.cmdlib.io.Type;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.nio.file.Files;
import java.util.LinkedList;
import java.util.List;

import static com.cavetale.setup.download.Jenkins.Mod.*;
import static link.l_pf.cmdlib.io.Console.SYSIO;

/**
 * List of available plugins
 */
public enum Plugin implements Provider, Installable {
    ADVENTURE, // TODO: Status?
    ADVICE_ANIMALS(new Parent("com.winthier")), // TODO: Status?
    A_F_K,
    ANTI_POPUP(new Source("https://github.com/KaspianDev/AntiPopup/releases/download/7d0370f/AntiPopup-10.1.jar", "10.1")),
    AREA,
    ARMOR_STAND_EDITOR(new ParentVersion("io.github.rypofalem", "1.17-25")),
    AUCTION,
    BANS(new Parent("com.winthier")),
    BINGO,
    BLOCK_CLIP,
    BLOCK_TRIGGER,
    CAPTURE_THE_FLAG,
    CAVES,
    CHAIR,
    CHAT(new Parent("com.winthier")),
    CHESS,
    CHRISTMAS, // TODO: Status?
    COLORFALL(new Parent("io.github.feydk")),
    CONNECT(new Parent("com.winthier")),
    // TODO: ConnectCore Status?
    CORE,
    COUNTDOWN(new ParentVersion("com.winthier", "0.1")),
    @Deprecated
    CRAFT_BAY(new ParentVersion("com.winthier", "2.26-SNAPSHOT")), // TODO: Status?
    CREATIVE(new Parent("com.winthier")),
    CULL_MOB,
    DECORATOR(new Parent("com.winthier")),
    DUNGEONS,
    DUSK(new ParentVersion("com.winthier", "0.1")),
    EASTER,
    EDITOR,
    ELECTION,
    ENDERBALL,
    ENEMY,
    EXPLOITS(new Parent("com.winthier")),
    EXTREME_GRASS_GROWING("EGG"),
    FAM,
    FAST_LEAF_DECAY(new Version("1.0-SNAPSHOT")),
    FESTIVAL,
    FLAT_GENERATOR,
    FLY,
    FREE_HAT,
    GOLDEN_TICKET,
    @Deprecated
    HALLOWEEN,
    HIDE_AND_SEEK,
    HOME,
    HOPPER_FILTER(new Parent("com.winthier")),
    HOT_SWAP,
    INVENTORY,
    @Deprecated
    INVISIBLE_ITEM_FRAMES,
    ITEM_STORE(new Parent("com.winthier")),
    KEEP_INVENTORY(new Parent("com.winthier")),
    KING_OF_THE_LADDER(new Artifact("kotl"), "KOTL"),
    // TODO: KingOfTheRing Status?
    KIT(new ParentVersion("com.winthier", "0.1")),
    @Deprecated
    LINK_PORTAL(new Parent("com.winthier")),
    MAGIC_MAP,
    MAIL(new Parent("com.winthier")),
    MAP_LOAD,
    MASS_STORAGE,
    MAYPOLE(new ParentVersion("com.winthier", "0.1")),
    MEMBER_LIST,
    MENU,
    MERCHANT,
    MINIVERSE,
    MOB_ARENA,
    MONEY,
    MYTEMS,
    OPEN_INV(new Source("https://github.com/Jikoo/OpenInv/releases/latest/download/OpenInv.jar", "5.1.6")),
    OVERBOARD,
    PERM(new Parent("com.winthier")),
    PHOTOS(new Parent("com.winthier")),
    PICTIONARY("CavePaint"),
    PLAYER_CACHE(new Parent("com.winthier")),
    PLAYER_INFO(new Parent("com.winthier")),
    PLUG_INFO,
    POCKET_MOB,
    POSTER,
    PROTECT(new Parent("com.winthier")),
    PROTOCOL_LIB(new Source("https://github.com/dmulloy2/ProtocolLib/releases/latest/download/ProtocolLib.jar", "5.3.0")),
    P_V_P_ARENA,
    QUIDDITCH,
    @Deprecated
    QUIZ(new Parent("com.winthier")),
    RACE,
    @Deprecated
    RAID,
    RANDOM_PLAYER_HEAD(new GroupArtifact("com.winthier.rph", "random-player-head"), "RPH"),
    RED_GREEN_LIGHT("RedLightGreenLight", "RGL", "RLGL"),
    RESIDENT,
    RESOURCE(new ParentVersion("com.winthier", "0.1")),
    RESOURCE_PACK,
    RULES(new Parent("com.winthier")),
    SERVER,
    @Deprecated
    SERVER_STATUS,
    SHOP(new Parent("com.winthier")),
    SHUTDOWN(new Parent("com.winthier")),
    SIDEBAR,
    SIGN_SPY,
    SKILLS,
    SKYBLOCK,
    SPAWN(new Parent("com.winthier")),
    SPIKE,
    SPLEEF(new Parent("com.winthier")),
    S_Q_L(new Parent("com.winthier")),
    STAR_BOOK(new Parent("com.winthier")),
    STOP_RAIN(new Parent("com.winthier")),
    STREAMER,
    STRUCTURE,
    SURVIVAL_GAMES,
    TELEVATOR,
    TETRIS,
    TICKET(new Parent("com.winthier")),
    TINFOIL(new ParentVersion("com.winthier", "0.1")),
    TITLE(new Parent("com.winthier")),
    TOO_MANY_ENTITIES(new ParentVersion("com.winthier", "0.1")),
    T_P_A,
    TREES,
    TUTOR,
    VERTIGO(new Parent("io.github.feydk")),
    VOID_GENERATOR,
    VOTE,
    WALL(new Parent("com.winthier")),
    WARDROBE,
    WARP,
    WATCHMAN,
    WINDICATOR,
    WIN_TAG,
    WORLD_EDIT(new Source("https://dev.bukkit.org/projects/worldedit/files/6013130/download", "7.3.10-Beta-1")),
    WORLD_MARKER,
    WORLDS(new Parent("com.winthier")),
    X_MAS;

    /** Plugin references. */
    private final @NotNull String[] refs;

    /** Plugin download source. */
    private final @NotNull Source source;

    /** Plugins that are contained by this structure. */
    private final @NotNull Plugin[] plugins;

    /** Plugin selection state. */
    private @Nullable Sel sel = null;
    // TODO: Linked?

    /** Plugin installation state. */
    private @Nullable List<String> inst = null;

    /**
     * Creates a new plugin.
     * @param aliases The plugin aliases.
     */
    Plugin(@NotNull String @NotNull ... aliases) {
        // Gather and format all references
        this.refs = new String[aliases.length + 1];
        this.refs[0] = Util.capsToCamel(this.name());
        System.arraycopy(aliases, 0, this.refs, 1, aliases.length);

        this.source = new Jenkins(this).toSource();
        this.plugins = new Plugin[]{this};
    }

    /**
     * Creates a new plugin.
     * @param mod The jenkins source modifications.
     * @param aliases The plugin aliases.
     */
    Plugin(@NotNull Jenkins.Mod mod, @NotNull String @NotNull ... aliases) {
        // Gather and format all references
        this.refs = new String[aliases.length + 1];
        this.refs[0] = Util.capsToCamel(this.name());
        System.arraycopy(aliases, 0, this.refs, 1, aliases.length);

        this.source = new Jenkins(this, mod).toSource();
        this.plugins = new Plugin[]{this};
    }

    /**
     * Creates a new plugin.
     * @param source The download source.
     * @param aliases The plugin aliases.
     */
    Plugin(@NotNull Source source, @NotNull String @NotNull ... aliases) {
        // Gather and format all references
        this.refs = new String[aliases.length + 1];
        this.refs[0] = Util.capsToCamel(this.name());
        System.arraycopy(aliases, 0, this.refs, 1, aliases.length);

        this.source = source;
        this.plugins = new Plugin[]{this};
    }

    @Override
    public @NotNull String displayName() {
        return this.refs[0];
    }

    @Override
    public @NotNull Source source() {
        return this.source;
    }

    @Override
    public @NotNull File downloads() {
        return Plugin.FOLDER;
    }

    @Override
    public @NotNull String toString() {
        return this.displayName();
    }

    @Override
    public @NotNull Plugin[] plugins() {
        return this.plugins;
    }

    /** Resets the plugin. */
    public void revert() {
        this.sel = null;
        this.inst = null;
    }

    //= Selection ==

    /** Sets this plugins' selection state to target. */
    public void target() {
        this.sel = Sel.TARGET;
    }

    /** Sets this plugins' selection state to active. */
    public void select() {
        this.sel = Sel.ON;
    }

    /** Deselects this plugin. */
    public void deselect() {
        this.sel = Sel.OFF;
    }

    /**
     * Returns whether this plugin is selected.
     * @return {@code true} if and only if this plugin is selected.
     */
    public boolean isSelected() {
        if (this.sel == null) Plugin.selected();
        return this.sel != Sel.OFF;
    }

    //= Installation ==

    /**
     * Returns whether this plugin is selected.
     * @return {@code true} if and only if this plugin is selected.
     */
    public @NotNull List<String> installations() {
        if (this.inst == null) Plugin.installed();
        return this.inst;
    }

    @Override
    public boolean isInstalled() {
        return !this.installations().isEmpty();
    }

    //= Finder ==

    /**
     * Finds the plugin matching a specific reference.
     * @param ref The reference of the plugin to find.
     * @return The plugin - if found.
     * @throws Plugin.NotFoundException If no matching plugin was found.
     */
    public static @NotNull Plugin get(@NotNull String ref) throws NotFoundException {
        for (Plugin p : Plugin.values()) if (p.displayName().equalsIgnoreCase(ref)) return p;
        throw new NotFoundException(ref);
    }

    /**
     * Finds the plugin matching a specific reference.
     * @param file The reference of the plugin to find.
     * @return The plugin - if found.
     * @throws Plugin.NotFoundException If no matching plugin was found.
     */
    public static @NotNull Plugin get(@NotNull File file) throws Plugin.NotAPluginException, NotFoundException {
        String ref = file.getName().toLowerCase();
        if (!file.getPath().endsWith(".jar")) throw new Plugin.NotAPluginException(file);
        int verStart = ref.indexOf("-");
        if (verStart < 0) verStart = ref.length() - 1;
        int extStart = ref.indexOf(".");
        if (extStart < 0) extStart = ref.length() - 1;
        int endStart = Math.min(verStart, extStart);
        ref = ref.substring(0, endStart);
        for (Plugin p : Plugin.values()) if (ref.equalsIgnoreCase(p.displayName())) return p;
        throw new NotFoundException(ref);
    }

    //= Indexing ==

    /** Plugin installation folder. */
    public static final @NotNull File FOLDER = new File("plugins/");

    /** List of selected plugins. */
    private static @Nullable List<Plugin> selected = null;

    /** List of installed plugins. */
    private static @Nullable List<Plugin> installed = null;

    /** List of linked plugins. */
    private static @Nullable List<String> linked = null;

    /** List of unknown plugins. */
    private static @Nullable List<String> unknown = null;

    /**
     * Returns the selected plugins. Determines them beforehand if necessary.
     * @return A list of selected plugins.
     */
    public static @NotNull List<Plugin> selected() {
        if (Plugin.selected != null) return Plugin.selected; // Update not necessary

        // Update selection
        SYSIO.debug("Loading selected plugins\n");
        for (Plugin p : Plugin.values()) p.deselect();
        Plugin.selected = new LinkedList<>();

        PluginContents plugins = (PluginContents) CustomFlag.PLUGIN.container();
        if (CustomFlag.INSTALLED.isSelected()) {
            SYSIO.debug("Selecting installed plugins\n");
            for (Plugin p : Plugin.installed()) p.target();
        } else if (CustomFlag.ALL.isSelected() ||  (CustomFlag.PLUGIN.isSelected() && plugins.isEmpty())) { // Select all
            SYSIO.debug("Selecting all plugins\n");
            for (Plugin p : Plugin.values()) p.target();
        } else {
            SYSIO.debug("Selecting plugins " + plugins.contents() + "\n");
            for (Plugin p : plugins.contents()) p.target(); // Select by plugin

            for (Category c : Category.values()) if (c.isSelected()) for (Plugin p : c.plugins()) p.select(); // Select by category

            for (Server s : Server.values()) if (s.isSelected()) for (Plugin p : s.plugins()) p.select(); // Select by server
        }

        for (Plugin p : Plugin.values()) if (p.isSelected()) Plugin.selected.add(p); // Update selection
        return Plugin.selected;
    }

    /**
     * Returns installed plugins. Determines them beforehand if necessary.
     * @return A list of installed plugins.
     */
    public static @NotNull List<Plugin> installed() {
        if (Plugin.installed != null) return Plugin.installed; // Update not necessary

        // Update installation
        Plugin.loadInstalled();
        assert Plugin.installed != null;
        return Plugin.installed;
    }

    /**
     * Returns linked plugins. Determines them beforehand if necessary.
     * @return A list of linked plugins.
     */
    public static @NotNull List<String> linked() {
        if (Plugin.linked != null) return Plugin.linked; // Update not necessary

        // Update installation
        Plugin.loadInstalled();
        assert Plugin.linked != null;
        return Plugin.linked;
    }

    /**
     * Returns unknown plugins. Determines them beforehand if necessary.
     * @return A list of unknown plugins.
     */
    public static @NotNull List<String> unknown() {
        if (Plugin.unknown != null) return Plugin.unknown; // Update not necessary

        // Update installation
        Plugin.loadInstalled();
        assert Plugin.unknown != null;
        return Plugin.unknown;
    }

    private static void loadInstalled() {
        // Update installation
        SYSIO.debug("Loading installed plugins\n");
        for (Plugin p : Plugin.values()) p.inst = new LinkedList<>(); // Reset installations
        Plugin.installed = new LinkedList<>();
        Plugin.linked = new LinkedList<>();
        Plugin.unknown = new LinkedList<>();

        File folder = new File("plugins/"); // Scan installations
        File[] files = folder.listFiles();
        if (files == null) return;
        for (File f : files) {
            if (f.isDirectory()) continue;
            Plugin p = null;
            try {
                p = Plugin.get(f);
            } catch (Plugin.NotAPluginException e) {
                continue;
            } catch (NotFoundException ignored) {}
            if (Files.isSymbolicLink(f.toPath())) Plugin.linked.add(f.getName());
            else {
                if (p == null) {
                    Plugin.unknown.add(f.getName());
                    SYSIO.warn("Unknown plugin " + f.getName());
                } else p.installations().add(f.getName());
            }
        }

        for (Plugin p : Plugin.values()) if (p.isInstalled()) Plugin.installed.add(p); // Update installation
    }

    public static void reset() {
        SYSIO.debug("Resetting plugins\n");
        for (Plugin p : Plugin.values()) p.revert();
        Plugin.selected = null;
        Plugin.installed = null;
        Plugin.linked = null;
        Plugin.unknown = null;
    }

    public static @NotNull List<Plugin> get(@Nullable Boolean installed, @Nullable Boolean selected) {
        List<Plugin> plugins = new LinkedList<>();
        for (Plugin p : Plugin.values()) {
            if ((installed == null || installed == p.isInstalled()) &&
                    (selected == null || selected == p.isSelected())) {
                plugins.add(p);
            }
        }
        return plugins;
    }

    //= Cosmetics ==

    /** List all plugins. */
    public static void requestAll() {
        SYSIO.separate();

        if (Plugin.values().length == 0) {
            SYSIO.requested(CustomStyle.PLUGIN.toString() + Code.BOLD + "No plugins available\n");
            return;
        }

        SYSIO.list(Type.REQUESTED, CustomStyle.PLUGIN, Plugin.values().length +
                " plugin(s) available", 4, 21, (Object[]) Plugin.values());
    }

    /** List selected plugins. */
    public static boolean listSelected() {
        List<Plugin> selected = Plugin.selected();
        if (selected.isEmpty()) return false;
        SYSIO.separate();
        SYSIO.list(Type.REQUESTED, CustomStyle.PLUGIN, selected.size() +
                " plugin(s) selected", 4, 21, selected.toArray());
        return true;
    }

    /** List installed plugins. */
    public static boolean listInstalled() {
        List<Plugin> plugins = Plugin.installed();
        if (plugins.isEmpty()) return false;
        SYSIO.separate();
        SYSIO.list(Type.REQUESTED, CustomStyle.INSTALL, plugins.size() +
                " plugin(s) installed", 4, 21, plugins.toArray());
        return true;
    }

    public static void requestInstalled() {
        SYSIO.separate();

        if (Plugin.installed().isEmpty()) {
            SYSIO.requested(CustomStyle.PLUGIN.toString() + Code.BOLD + "No plugins installed\n");
            return;
        }

        SYSIO.list(Type.REQUESTED, CustomStyle.PLUGIN, Plugin.installed().size() +
                " plugin(s) installed", 4, 21, Plugin.installed().toArray());
    }

    /** List lniked plugins. */
    public static boolean listLinked() {
        List<String> linked = Plugin.linked();
        if (linked.isEmpty()) return false;
        SYSIO.separate();
        SYSIO.list(Type.REQUESTED, CustomStyle.LINK, linked.size() +
                " plugin(s) linked", 4, 21, linked.toArray());
        return true;
    }

    /** List unknown plugins. */
    public static boolean listUnknown() {
        List<String> unknown = Plugin.unknown();
        if (unknown.isEmpty()) return false;
        SYSIO.separate();
        SYSIO.list(Type.REQUESTED, CustomStyle.UNKNOWN, unknown.size() +
                " plugin(s) unknown", 4, 21, unknown.toArray());
        return true;
    }

    //= Exceptions ==

    /** Exception that is thrown if a requested plugin could not be found. */
    public static class NotFoundException extends Exception {
        /**
         * Creates a new plugin not found exception.
         * @param ref The reference that could not be matched.
         */
        public NotFoundException(@NotNull String ref) {
            super("Plugin \"" + ref + "\" not found. Did you mean to use -\"P\" for --path?");
        }
    }

    /** Exception that is thrown if a file is not a plugin. */
    public static class NotAPluginException extends DataException {
        /**
         * Creates a new not a plugin exception.
         * @param file The file that is not a plugin.
         */
        public NotAPluginException(@NotNull File file) {
            super(file.getName() + " is not a plugin");
        }
    }
}
