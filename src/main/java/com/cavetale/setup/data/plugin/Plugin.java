package com.cavetale.setup.data.plugin;

import com.cavetale.setup.data.DataError;
import com.cavetale.setup.data.DataException;
import com.cavetale.setup.data.Installable;
import com.cavetale.setup.data.Sel;
import com.cavetale.setup.data.build.*;
import com.cavetale.setup.download.Source;
import com.cavetale.setup.download.Ver;
import com.cavetale.setup.console.CustomFlag;
import com.cavetale.setup.console.container.PluginContents;
import com.cavetale.setup.util.Util;
import com.cavetale.setup.console.CustomStyle;
import io.github.ijustleyxo.jclix.io.Code;
import io.github.ijustleyxo.jclix.io.Type;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.net.URI;
import java.nio.file.Files;
import java.util.LinkedList;
import java.util.List;

import static io.github.ijustleyxo.jclix.io.Console.SYSIO;

/**
 * List of available plugins
 */
public enum Plugin implements Provider, Installable {
    ADVENTURE, // TODO: Status?
    ADVICE_ANIMALS(Parent.of("com.winthier")), // TODO: Status?
    A_F_K,
    ANTI_POPUP(Installable.uriOf("https://github.com/KaspianDev/AntiPopup/releases/download/7d0370f/AntiPopup-10.1.jar"), Ver.of("10.1")),
    AREA,
    ARMOR_STAND_EDITOR(Parent.of("io.github.rypofalem"), Ver.of("1.17-25")),
    AUCTION,
    BANS(Parent.of("com.winthier")),
    BINGO,
    BLOCK_CLIP,
    BLOCK_TRIGGER,
    CAPTURE_THE_FLAG,
    CAVES,
    CHAIR,
    CHAT(Parent.of("com.winthier")),
    CHESS,
    CHRISTMAS, // TODO: Status?
    COLORFALL(Parent.of("io.github.feydk")),
    CONNECT(Parent.of("com.winthier")),
    // TODO: ConnectCore Status?
    CORE,
    COUNTDOWN(Parent.of("com.winthier"), Ver.of("0.1")),
    @Deprecated
    CRAFT_BAY(Parent.of("com.winthier"), Ver.of("2.26-SNAPSHOT")), // TODO: Status?
    CREATIVE(Parent.of("com.winthier")),
    CULL_MOB,
    DECORATOR(Parent.of("com.winthier")),
    DUNGEONS,
    DUSK(Parent.of("com.winthier"), Ver.of("0.1")),
    EASTER,
    EDITOR,
    ELECTION,
    ENDERBALL,
    ENEMY,
    EXPLOITS(Parent.of("com.winthier")),
    EXTREME_GRASS_GROWING("EGG"),
    FAM,
    FAST_LEAF_DECAY(Ver.of("1.0-SNAPSHOT")),
    FESTIVAL,
    FLAT_GENERATOR,
    FLY,
    FREE_HAT,
    GOLDEN_TICKET,
    @Deprecated
    HALLOWEEN,
    HIDE_AND_SEEK,
    HOME,
    HOPPER_FILTER(Parent.of("com.winthier")),
    HOT_SWAP,
    INVENTORY,
    @Deprecated
    INVISIBLE_ITEM_FRAMES,
    ITEM_STORE(Parent.of("com.winthier")),
    KEEP_INVENTORY(Parent.of("com.winthier")),
    KING_OF_THE_LADDER(Ref.of("kotl"), "KOTL"),
    // TODO: KingOfTheRing Status?
    KIT(Parent.of("com.winthier"), Ver.of("0.1")),
    @Deprecated
    LINK_PORTAL(Parent.of("com.winthier")),
    MAGIC_MAP,
    MAIL(Parent.of("com.winthier")),
    MAP_LOAD,
    MASS_STORAGE,
    MAYPOLE(Parent.of("com.winthier"), Ver.of("0.1")),
    MEMBER_LIST,
    MENU,
    MERCHANT,
    MINIVERSE,
    MOB_ARENA,
    MONEY,
    MYTEMS,
    OPEN_INV(Installable.uriOf("https://github.com/Jikoo/OpenInv/releases/latest/download/OpenInv.jar"), Ver.of("5.1.6")),
    OVERBOARD,
    PERM(Parent.of("com.winthier")),
    PHOTOS(Parent.of("com.winthier")),
    PICTIONARY("CavePaint"),
    PLAYER_CACHE(Parent.of("com.winthier")),
    PLAYER_INFO(Parent.of("com.winthier")),
    PLUG_INFO,
    POCKET_MOB,
    POSTER,
    PROTECT(Parent.of("com.winthier")),
    PROTOCOL_LIB(Installable.uriOf("https://github.com/dmulloy2/ProtocolLib/releases/latest/download/ProtocolLib.jar"), Ver.of("5.3.0")),
    P_V_P_ARENA,
    QUIDDITCH,
    @Deprecated
    QUIZ(Parent.of("com.winthier")),
    RACE,
    @Deprecated
    RAID,
    RANDOM_PLAYER_HEAD(Group.of("com.winthier.rph"), Ref.of("random-player-head"), Ver.of("0.1-SNAPSHOT"), "RPH"),
    RED_GREEN_LIGHT("RedLightGreenLight", "RGL", "RLGL"),
    RESIDENT,
    RESOURCE(Parent.of("com.winthier"), Ver.of("0.1")),
    RESOURCE_PACK,
    RULES(Parent.of("com.winthier")),
    SERVER,
    @Deprecated
    SERVER_STATUS,
    SHOP(Parent.of("com.winthier")),
    SHUTDOWN(Parent.of("com.winthier")),
    SIDEBAR,
    SIGN_SPY,
    SKILLS,
    SKYBLOCK,
    SPAWN(Parent.of("com.winthier")),
    SPIKE,
    SPLEEF(Parent.of("com.winthier")),
    S_Q_L(Parent.of("com.winthier")),
    STAR_BOOK(Parent.of("com.winthier")),
    STOP_RAIN(Parent.of("com.winthier")),
    STREAMER,
    STRUCTURE,
    SURVIVAL_GAMES,
    TELEVATOR,
    TETRIS,
    TICKET(Parent.of("com.winthier")),
    TINFOIL(Parent.of("com.winthier"), Ver.of("0.1")),
    TITLE(Parent.of("com.winthier")),
    TOO_MANY_ENTITIES(Parent.of("com.winthier"), Ver.of("0.1")),
    T_P_A,
    TREES,
    TUTOR,
    VERTIGO(Parent.of("io.github.feydk")),
    VOID_GENERATOR,
    VOTE,
    WALL(Parent.of("com.winthier")),
    WARDROBE,
    WARP,
    WATCHMAN,
    WINDICATOR,
    WIN_TAG,
    WORLD_EDIT(Installable.uriOf("https://dev.bukkit.org/projects/worldedit/files/6013130/download"), Ver.of("7.3.10-Beta-1")),
    WORLD_MARKER,
    WORLDS(Parent.of("com.winthier")),
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
        this(Ver.DEFAULT, aliases);
    }

    /**
     * Creates a new plugin.
     * @param parent The package parent.
     * @param aliases The plugin aliases.
     */
    Plugin(@NotNull Parent parent, @NotNull String @NotNull ... aliases) {
        this(parent, Ver.DEFAULT, aliases);
    }

    /**
     * Creates a new plugin.
     * @param ref The package reference.
     * @param aliases The plugin aliases.
     */
    Plugin(@NotNull Ref ref, @NotNull String @NotNull ... aliases) {
        this(Parent.DEFAULT, ref, Ver.DEFAULT, aliases);
    }

    /**
     * Creates a new plugin.
     * @param ver The package version.
     * @param aliases The plugin aliases.
     */
    Plugin(@NotNull Ver ver, @NotNull String @NotNull ... aliases) {
        this(Parent.DEFAULT, ver, aliases);
    }

    /**
     * Creates a new plugin.
     * @param group The package group.
     * @param ref The package reference.
     * @param aliases The plugin aliases.
     */
    Plugin(@NotNull Group group, @NotNull Ref ref, @NotNull String @NotNull ... aliases) {
        this(group, ref, Ver.DEFAULT, aliases);
    }

    /**
     * Creates a new plugin.
     * @param parent The package parent.
     * @param ver The package version.
     * @param aliases The plugin aliases.
     */
    Plugin(@NotNull Parent parent, @NotNull Ver ver, @NotNull String @NotNull ... aliases) {
        this.refs = new String[aliases.length + 1];
        this.refs[0] = Util.capsToCamel(this.name());
        System.arraycopy(aliases, 0, this.refs, 1, aliases.length);

        this.source = new Jenkins(Job.of(this.displayName()), Group.of(parent, this.displayName().toLowerCase()), Ref.of(this.displayName().toLowerCase()), ver);
        this.plugins = new Plugin[]{this};
    }

    /**
     * Creates a new plugin.
     * @param parent The package parent.
     * @param ref The package reference.
     * @param ver The package version.
     * @param aliases The plugin aliases.
     */
    Plugin(@NotNull Parent parent, @NotNull Ref ref, @NotNull Ver ver, @NotNull String @NotNull ... aliases) {
        this.refs = new String[aliases.length + 1];
        this.refs[0] = Util.capsToCamel(this.name());
        System.arraycopy(aliases, 0, this.refs, 1, aliases.length);

        this.source = new Jenkins(Job.of(this.displayName()), Group.of(parent, ref), ref, ver);
        this.plugins = new Plugin[]{this};
    }

    /**
     * Creates a new plugin.
     * @param group The package group.
     * @param ref The package reference.
     * @param ver The package version.
     * @param aliases The plugin aliases.
     */
    Plugin(@NotNull Group group, @NotNull Ref ref, @NotNull Ver ver, @NotNull String @NotNull ... aliases) {
        this.refs = new String[aliases.length + 1];
        this.refs[0] = Util.capsToCamel(this.name());
        System.arraycopy(aliases, 0, this.refs, 1, aliases.length);

        this.source = new Jenkins(Job.of(this.displayName()), group, ref, ver);
        this.plugins = new Plugin[]{this};
    }

    /**
     * Creates a new plugin.
     * @param uri The download location.
     * @param ver The package version.
     * @param aliases The plugin aliases.
     */
    Plugin(@NotNull URI uri, @NotNull Ver ver, @NotNull String @NotNull ... aliases) {
        this.refs = new String[aliases.length + 1];
        this.refs[0] = Util.capsToCamel(this.name());
        System.arraycopy(aliases, 0, this.refs, 1, aliases.length);

        this.source = new Source.Other(uri, ver);
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

    /** Exception that is thrown if a uri is invalid. */
    public static class URIError extends DataError {
        /**
         * Creates a new uri error.
         * @param uri The invalid uri.
         * @param cause The original error.
         */
        public URIError(@NotNull String uri, @NotNull Throwable cause) {
            super("Faulty url \n" + uri + ": " + cause.getMessage(), cause);
        }
    }
}
