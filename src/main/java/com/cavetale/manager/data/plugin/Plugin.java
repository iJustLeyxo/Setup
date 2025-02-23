package com.cavetale.manager.data.plugin;

import com.cavetale.manager.data.*;
import com.cavetale.manager.download.*;
import com.cavetale.manager.data.build.*;
import com.cavetale.manager.parser.Flag;
import com.cavetale.manager.parser.InputException;
import com.cavetale.manager.util.Util;
import com.cavetale.manager.util.console.Console;
import com.cavetale.manager.util.console.Style;
import com.cavetale.manager.util.console.Type;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.util.LinkedList;
import java.util.List;

/**
 * List of available plugins
 */
public enum Plugin implements Provider {
    ADVENTURE, // TODO: Status?
    ADVICE_ANIMALS(Parent.of("com.winthier")), // TODO: Status?
    A_F_K,
    ANTI_POPUP(Util.uriOf("https://github.com/KaspianDev/AntiPopup/releases/download/7d0370f/AntiPopup-10.1.jar"), Ver.of("10.1")),
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
    OPEN_INV(Util.uriOf("https://github.com/Jikoo/OpenInv/releases/latest/download/OpenInv.jar"), Ver.of("5.1.6")),
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
    PROTOCOL_LIB(Util.uriOf("https://github.com/dmulloy2/ProtocolLib/releases/latest/download/ProtocolLib.jar"), Ver.of("5.3.0")),
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
    WORLD_EDIT(Util.uriOf("https://dev.bukkit.org/projects/worldedit/files/6013130/download"), Ver.of("7.3.10-Beta-1")),
    WORLD_MARKER,
    WORLDS(Parent.of("com.winthier")),
    X_MAS;

    private final @NotNull Source source;
    private final @NotNull Plugin[] plugins;
    private final @NotNull String[] refs;

    private @NotNull Sel sel = Sel.OFF;
    // TODO: Linked?
    private final @NotNull List<String> installations = new LinkedList<>();

    Plugin(@NotNull String @NotNull ... aliases) {
        this(Ver.DEFAULT);
    }

    Plugin(@NotNull Parent parent, @NotNull String @NotNull ... aliases) {
        this(parent, Ver.DEFAULT);
    }

    Plugin(@NotNull Ref ref, @NotNull String @NotNull ... aliases) {
        this(Parent.DEFAULT, ref, Ver.DEFAULT);
    }

    Plugin(@NotNull Ver ver, @NotNull String @NotNull ... aliases) {
        this(Parent.DEFAULT, ver);
    }

    Plugin(@NotNull Group group, @NotNull Ref ref, @NotNull String @NotNull ... aliases) {
        this(group, ref, Ver.DEFAULT);
    }

    Plugin(@NotNull Parent parent, @NotNull Ver ver, @NotNull String @NotNull ... aliases) {
        this.refs = new String[aliases.length + 1];
        this.refs[0] = Util.capsToCamel(this.name());
        System.arraycopy(aliases, 0, this.refs, 1, aliases.length);

        this.source = new Jenkins(Job.of(this.displayName()), Group.of(parent, this.displayName().toLowerCase()), Ref.of(this.displayName().toLowerCase()), ver);
        this.plugins = new Plugin[]{this};
    }

    Plugin(@NotNull Parent parent, @NotNull Ref ref, @NotNull Ver ver, @NotNull String @NotNull ... aliases) {
        this.refs = new String[aliases.length + 1];
        this.refs[0] = Util.capsToCamel(this.name());
        System.arraycopy(aliases, 0, this.refs, 1, aliases.length);

        this.source = new Jenkins(Job.of(this.displayName()), Group.of(parent, ref), ref, ver);
        this.plugins = new Plugin[]{this};
    }

    Plugin(@NotNull Group group, @NotNull Ref ref, @NotNull Ver ver, @NotNull String @NotNull ... aliases) {
        this.refs = new String[aliases.length + 1];
        this.refs[0] = Util.capsToCamel(this.name());
        System.arraycopy(aliases, 0, this.refs, 1, aliases.length);

        this.source = new Jenkins(Job.of(this.displayName()), group, ref, ver);
        this.plugins = new Plugin[]{this};
    }

    Plugin(@NotNull URI uri, @NotNull Ver ver, @NotNull String @NotNull ... aliases) {
        this.refs = new String[aliases.length + 1];
        this.refs[0] = Util.capsToCamel(this.name());
        System.arraycopy(aliases, 0, this.refs, 1, aliases.length);

        this.source = new Source.Other(uri, ver);
        this.plugins = new Plugin[]{this};
    }

    public @NotNull String displayName() {
        return this.refs[0];
    }

    @Override
    public @NotNull String toString() {
        return this.displayName();
    }

    @Override
    public @NotNull Plugin[] plugins() {
        return this.plugins;
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

    public void reset() {
        this.sel = Sel.OFF;
    }

    public void clearInstallations() {
        this.installations.clear();
    }

    public void addInstallation(@NotNull String file) {
        this.installations.add(file);
    }

    public boolean isInstalled() {
        return !this.installations.isEmpty();
    }

    public void install() {
        Console.log(Type.INFO, "Installing " + this.displayName() + " plugin");
        if (this.isInstalled()) {
            if (!Console.log(Type.INFO, Style.WARN, " skipped (already installed)\n")) {
                Console.log(Type.WARN, "Installing " + this.displayName() + " plugin skipped (already installed)\n");
            }
            return;
        }
        try {
            String file = this.displayName() + "-" + this.source.ver() + ".jar";
            Util.download(this.source.uri(), new File(Plugins.FOLDER, file));
            this.installations.add(file);
            Console.log(Type.INFO, Style.DONE, " done\n");
        } catch (IOException e) {
            if (!Console.log(Type.INFO, Style.ERR, " failed (" + e.getMessage() + ")\n")) {
                Console.log(Type.ERR, "Installing " + this.displayName() + " plugin failed (" + e.getMessage() + ")\n");
            }
            if (Flag.ERROR.isSelected()) Console.log(Type.REQUESTED, e);
        }
    }

    public void update() {
        Console.log(Type.INFO, "Updating " + this.displayName() + " plugin");

        File folder = Plugins.FOLDER; // Uninstall plugin
        for (String fileName : this.installations) {
            File file = new File(folder, fileName);
            if (!Files.isSymbolicLink(file.toPath())) {
                if (file.delete()) continue;
                if (!Console.log(Type.EXTRA, Style.ERR, " failed - failed to delete " + file + "\n")) {
                    Console.log(Type.ERR, "Updating " + this.displayName() + " plugin failed - failed to delete " + file + "\n");
                }
            } else if (!Console.log(Type.EXTRA, Style.ERR, " failed - skipped " + file + " (linked)\n")) {
                Console.log(Type.ERR, "Updating " + this.displayName() + " plugin failed - skipped " + file + " (linked)\n");
            }
            return;
        }
        this.installations.clear();

        try { // Install plugin
            String file = this.displayName() + "-" + this.source.ver() + ".jar";
            Util.download(this.source.uri(), new File(Plugins.FOLDER, file));
            this.addInstallation(file);
            Console.log(Type.INFO, Style.DONE, " done\n");
        } catch (IOException e) {
            if (!Console.log(Type.INFO, Style.ERR, " failed - failed to download (" + e.getMessage() + ")\n")) {
                Console.log(Type.ERR, "Updating " + this.displayName() + " plugin failed - failed to download (" + e.getMessage() + ")\n");
            }
            if (Flag.ERROR.isSelected()) Console.log(Type.REQUESTED, e);
        }
    }

    public void uninstall() {
        File folder = Plugins.FOLDER;
        for (String fileName : this.installations) {
            Console.log(Type.INFO, "Uninstalling " + fileName);
            File file = new File(folder, fileName);
            if (!Files.isSymbolicLink(file.toPath())) {
                if (file.delete()) {
                    this.installations.remove(fileName);
                    Console.log(Type.INFO, Style.DONE, " done\n");
                } else if (!Console.log(Type.EXTRA, Style.ERR, " failed\n")) {
                    Console.log(Type.ERR, "Uninstalling " + file + " plugin failed\n");
                }
            } else if (!Console.log(Type.EXTRA, Style.WARN, " skipped (linked)\n")) {
                Console.log(Type.WARN, "Uninstalling " + file + " plugin skipped (linked)\n");
            }
        }
    }

    public static @NotNull Plugin get(@NotNull String ref) throws Plugin.PluginNotFoundException {
        for (Plugin p : Plugin.values()) if (p.displayName().equalsIgnoreCase(ref)) return p;
        throw new Plugin.PluginNotFoundException(ref);
    }

    public static @NotNull Plugin get(@NotNull File file) throws Plugin.NotAPluginException, Plugin.PluginNotFoundException {
        String ref = file.getName().toLowerCase();
        if (!file.getPath().endsWith(".jar")) throw new Plugin.NotAPluginException(file);
        int verStart = ref.indexOf("-");
        if (verStart < 0) verStart = ref.length() - 1;
        int extStart = ref.indexOf(".");
        if (extStart < 0) extStart = ref.length() - 1;
        int endStart = Math.min(verStart, extStart);
        ref = ref.substring(0, endStart);
        for (Plugin p : Plugin.values()) if (ref.equalsIgnoreCase(p.displayName())) return p;
        throw new Plugin.PluginNotFoundException(ref);
    }

    public static class PluginNotFoundException extends InputException {
        public PluginNotFoundException(@NotNull String ref) {
            super("Plugin \"" + ref + "\" not found. Did you mean to use -\"P\" for --path?");
        }
    }

    public static class NotAPluginException extends DataException {
        public NotAPluginException(@NotNull File file) {
            super(file.getName() + " is not a plugin");
        }
    }

    public static class URIError extends DataError {
        public URIError(@NotNull String uri, @NotNull Throwable cause) {
            super("Faulty url \n" + uri + ": " + cause.getMessage(), cause);
        }
    }
}
