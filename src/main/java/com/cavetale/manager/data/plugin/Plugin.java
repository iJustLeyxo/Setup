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
    Adventure, // TODO: Status?
    AdviceAnimals(Parent.of("com.winthier")), // TODO: Status?
    AFK,
    AntiPopup(Util.uriOf("https://github.com/KaspianDev/AntiPopup/releases/download/7d0370f/AntiPopup-10.1.jar"), Ver.of("10.1")),
    Area,
    ArmorStandEditor(Parent.of("io.github.rypofalem"), Ver.of("1.17-25")),
    Auction,
    Bans(Parent.of("com.winthier")),
    Bingo,
    BlockClip,
    BlockTrigger,
    CaptureTheFlag,
    Caves,
    Chair,
    Chat(Parent.of("com.winthier")),
    Chess,
    Christmas, // TODO: Status?
    Colorfall(Parent.of("io.github.feydk")),
    Connect(Parent.of("com.winthier")),
    // TODO: ConnectCore Status?
    Core,
    Countdown(Parent.of("com.winthier"), Ver.of("0.1")),
    @Deprecated
    CraftBay(Parent.of("com.winthier"), Ver.of("2.26-SNAPSHOT")), // TODO: Status?
    Creative(Parent.of("com.winthier")),
    CullMob,
    Decorator(Parent.of("com.winthier")),
    Dungeons,
    Dusk(Parent.of("com.winthier"), Ver.of("0.1")),
    Easter,
    Editor,
    Election,
    Enderball,
    Enemy,
    Exploits(Parent.of("com.winthier")),
    ExtremeGrassGrowing("EGG"),
    Fam,
    FastLeafDecay(Ver.of("1.0-SNAPSHOT")),
    Festival,
    FlatGenerator,
    Fly,
    FreeHat,
    GoldenTicket,
    @Deprecated
    Halloween,
    HideAndSeek,
    Home,
    HopperFilter(Parent.of("com.winthier")),
    HotSwap,
    Inventory,
    @Deprecated
    InvisibleItemFrames,
    ItemStore(Parent.of("com.winthier")),
    KeepInventory(Parent.of("com.winthier")),
    KingOfTheLadder(Ref.of("kotl"), "KOTL"),
    // TODO: KingOfTheRing Status?
    Kit(Parent.of("com.winthier"), Ver.of("0.1")),
    @Deprecated
    LinkPortal(Parent.of("com.winthier")),
    MagicMap,
    Mail(Parent.of("com.winthier")),
    MapLoad,
    MassStorage,
    Maypole(Parent.of("com.winthier"), Ver.of("0.1")),
    MemberList,
    Menu,
    Merchant,
    Miniverse,
    MobArena,
    Money,
    Mytems,
    OpenInv(Util.uriOf("https://github.com/Jikoo/OpenInv/releases/download/5.1.4/OpenInv.jar"), Ver.of("5.1.4")),
    Overboard,
    Perm(Parent.of("com.winthier")),
    Photos(Parent.of("com.winthier")),
    Pictionary("CavePaint"),
    PlayerCache(Parent.of("com.winthier")),
    PlayerInfo(Parent.of("com.winthier")),
    PlugInfo,
    PocketMob,
    Poster,
    Protect(Parent.of("com.winthier")),
    ProtocolLib(Util.uriOf("https://github.com/dmulloy2/ProtocolLib/releases/download/5.3.0/ProtocolLib.jar"), Ver.of("4.7.1-SNAPSHOT")),
    PVPArena,
    Quidditch,
    @Deprecated
    Quiz(Parent.of("com.winthier")),
    Race,
    @Deprecated
    Raid,
    RandomPlayerHead(Group.of("com.winthier.rph"), Ref.of("random-player-head"), Ver.of("0.1-SNAPSHOT")),
    RedGreenLight("RedLightGreenLight", "RGL", "RLGL"),
    Resident,
    Resource(Parent.of("com.winthier"), Ver.of("0.1")),
    ResourcePack,
    Rules(Parent.of("com.winthier")),
    Server,
    @Deprecated
    ServerStatus,
    Shop(Parent.of("com.winthier")),
    Shutdown(Parent.of("com.winthier")),
    Sidebar,
    SignSpy,
    Skills,
    Skyblock,
    Spawn(Parent.of("com.winthier")),
    Spike,
    Spleef(Parent.of("com.winthier")),
    SQL(Parent.of("com.winthier")),
    StarBook(Parent.of("com.winthier")),
    StopRain(Parent.of("com.winthier")),
    Streamer,
    Structure,
    SurvivalGames,
    Televator,
    Tetris,
    Ticket(Parent.of("com.winthier")),
    Tinfoil(Parent.of("com.winthier"), Ver.of("0.1")),
    Title(Parent.of("com.winthier")),
    TooManyEntities(Parent.of("com.winthier"), Ver.of("0.1")),
    TPA,
    Trees,
    Tutor,
    Vertigo(Parent.of("io.github.feydk")),
    VoidGenerator,
    Vote,
    Wall(Parent.of("com.winthier")),
    Wardrobe,
    Warp,
    Watchman,
    Windicator,
    WinTag,
    WorldEdit(Util.uriOf("https://dev.bukkit.org/projects/worldedit/files/6013130/download"), Ver.of("7.3.10-Beta-1")),
    WorldMarker,
    Worlds(Parent.of("com.winthier")),
    Xmas;

    private final @NotNull Source source;
    private final @NotNull Plugin[] plugins;
    private final @NotNull String[] refs;

    private boolean selected = false;
    private boolean installed = false;
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
        this.source = new Jenkins(Job.of(this.name()), Group.of(parent, this.name().toLowerCase()), Ref.of(this.name().toLowerCase()), ver);
        this.plugins = new Plugin[]{this};
        this.refs = new String[aliases.length + 1];
        this.refs[0] = this.name();
        System.arraycopy(aliases, 0, this.refs, 1, aliases.length);
    }

    Plugin(@NotNull Parent parent, @NotNull Ref ref, @NotNull Ver ver, @NotNull String @NotNull ... aliases) {
        this.source = new Jenkins(Job.of(this.name()), Group.of(parent, ref), ref, ver);
        this.plugins = new Plugin[]{this};
        this.refs = new String[aliases.length + 1];
        this.refs[0] = this.name();
        System.arraycopy(aliases, 0, this.refs, 1, aliases.length);
    }

    Plugin(@NotNull Group group, @NotNull Ref ref, @NotNull Ver ver, @NotNull String @NotNull ... aliases) {
        this.source = new Jenkins(Job.of(this.name()), group, ref, ver);
        this.plugins = new Plugin[]{this};
        this.refs = new String[aliases.length + 1];
        this.refs[0] = this.name();
        System.arraycopy(aliases, 0, this.refs, 1, aliases.length);
    }

    Plugin(@NotNull URI uri, @NotNull Ver ver, @NotNull String @NotNull ... aliases) {
        this.source = new Source.Other(uri, ver);
        this.plugins = new Plugin[]{this};
        this.refs = new String[aliases.length + 1];
        this.refs[0] = this.name();
        System.arraycopy(aliases, 0, this.refs, 1, aliases.length);
    }

    @Override
    public @NotNull Plugin[] plugins() {
        return this.plugins;
    }

    @Override
    public @NotNull String toString() {
        return this.name();
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public boolean isSelected() {
        return this.selected;
    }

    public void clearInstallations() {
        this.installed = false;
        this.installations.clear();
    }

    public void addInstallation(@NotNull String file) {
        this.installed = true;
        this.installations.add(file);
    }

    public boolean isInstalled() {
        return this.installed;
    }

    public void install() {
        Console.log(Type.INFO, "Installing " + this.name() + " plugin");
        if (this.isInstalled()) {
            if (!Console.log(Type.INFO, Style.WARN, " skipped (already installed)\n")) {
                Console.log(Type.WARN, "Installing " + this.name() + " plugin skipped (already installed)\n");
            }
            return;
        }
        try {
            String file = this.name() + "-" + this.source.ver() + ".jar";
            Util.download(this.source.uri(), new File(Plugins.FOLDER, file));
            this.installations.add(file);
            Console.log(Type.INFO, Style.DONE, " done\n");
        } catch (IOException e) {
            if (!Console.log(Type.INFO, Style.ERR, " failed (" + e.getMessage() + ")\n")) {
                Console.log(Type.ERR, "Installing " + this.name() + " plugin failed (" + e.getMessage() + ")\n");
            }
            if (Flag.error.isSelected()) Console.log(Type.REQUESTED, e);
        }
    }

    public void update() {
        Console.log(Type.INFO, "Updating " + this.name() + " plugin");

        File folder = Plugins.FOLDER; // Uninstall plugin
        for (String fileName : this.installations) {
            File file = new File(folder, fileName);
            if (!Files.isSymbolicLink(file.toPath())) {
                if (file.delete()) continue;
                if (!Console.log(Type.EXTRA, Style.ERR, " failed - failed to delete " + file + "\n")) {
                    Console.log(Type.ERR, "Updating " + this.name() + " plugin failed - failed to delete " + file + "\n");
                }
            } else if (!Console.log(Type.EXTRA, Style.ERR, " failed - skipped " + file + " (linked)\n")) {
                Console.log(Type.ERR, "Updating " + this.name() + " plugin failed - skipped " + file + " (linked)\n");
            }
            return;
        }
        this.installations.clear();

        try { // Install plugin
            String file = this.name() + "-" + this.source.ver() + ".jar";
            Util.download(this.source.uri(), new File(Plugins.FOLDER, file));
            this.addInstallation(file);
            Console.log(Type.INFO, Style.DONE, " done\n");
        } catch (IOException e) {
            if (!Console.log(Type.INFO, Style.ERR, " failed - failed to download (" + e.getMessage() + ")\n")) {
                Console.log(Type.ERR, "Updating " + this.name() + " plugin failed - failed to download (" + e.getMessage() + ")\n");
            }
            if (Flag.error.isSelected()) Console.log(Type.REQUESTED, e);
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
        for (Plugin p : Plugin.values()) if (p.name().equalsIgnoreCase(ref)) return p;
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
        for (Plugin p : Plugin.values()) if (ref.equalsIgnoreCase(p.name())) return p;
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
