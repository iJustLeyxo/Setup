package com.cavetale.manager.data.plugin;

import com.cavetale.manager.data.DataError;
import com.cavetale.manager.data.DataException;
import com.cavetale.manager.data.Source;
import com.cavetale.manager.parser.InputException;
import com.cavetale.manager.util.Util;
import com.cavetale.manager.util.console.Console;
import com.cavetale.manager.util.console.Style;
import com.cavetale.manager.util.console.Type;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.LinkedList;
import java.util.List;

/**
 * List of available plugins
 */
public enum Plugin implements Provider {
    // TODO: Adventure
    @Deprecated
    AdviceAnimals("com.winthier.adviceanimals", "0.1-SNAPSHOT"),
    AFK("com.cavetale.afk", "0.1-SNAPSHOT"),
    AntiPopup(Util.uriOf("https://github.com/KaspianDev/AntiPopup/releases/download/499358a/AntiPopup-10.jar"), "10"),
    Area("com.cavetale.area", "0.1-SNAPSHOT"),
    ArmorStandEditor("io.github.rypofalem.armorstandeditor", "1.17-25"),
    Auction("com.cavetale.auction", "0.1-SNAPSHOT"),
    Bans("com.winthier.bans", "0.1-SNAPSHOT"),
    Bingo("com.cavetale.bingo", "0.1-SNAPSHOT"),
    BlockClip("com.cavetale.blockclip", "0.1-SNAPSHOT"),
    BlockTrigger("com.cavetale.blocktrigger", "0.1-SNAPSHOT"),
    // TODO: BungeeCavetale
    // TODO: CaptureTheFlag
    Caves("com.cavetale.caves", "0.1-SNAPSHOT"),
    // TODO: CavetaleResourcePack
    Chair("com.cavetale.chair", "0.1-SNAPSHOT"),
    Chat("com.winthier.chat", "0.1-SNAPSHOT"),
    // TODO: Chess
    @Deprecated
    Christmas("com.cavetale.christmas", "0.1-SNAPSHOT"),
    Colorfall("io.github.feydk.colorfall", "0.1-SNAPSHOT"),
    Connect("com.winthier.connect", "0.1-SNAPSHOT"),
    // TODO: ConnectCore
    Core("com.cavetale.core", "0.1-SNAPSHOT"),
    Countdown("com.winthier.countdown", "0.1"),
    @Deprecated
    CraftBay("com.winthier.craftbay", "2.26-SNAPSHOT"),
    Creative("com.winthier.creative", "0.1-SNAPSHOT"),
    CullMob("com.cavetale.cullmob", "0.1-SNAPSHOT"),
    Decorator("com.winthier.decorator", "0.1-SNAPSHOT"),
    Dungeons("com.cavetale.dungeons", "0.1-SNAPSHOT"),
    Dusk("com.winthier.dusk", "0.1"),
    Easter("com.cavetale.easter", "0.1-SNAPSHOT"),
    Editor("com.cavetale.editor", "0.1-SNAPSHOT"),
    Election("com.cavetale.election", "0.1-SNAPSHOT"),
    Enderball("com.cavetale.enderball", "0.1-SNAPSHOT"),
    Enemy("com.cavetale.enemy", "0.1-SNAPSHOT"),
    Exploits("com.winthier.exploits", "0.1-SNAPSHOT"),
    ExtremeGrassGrowing("com.cavetale.extremegrassgrowing", "0.1-SNAPSHOT"),
    Fam("com.cavetale.fam", "0.1-SNAPSHOT"),
    FastLeafDecay("com.cavetale.fastleafdecay", "1.0-SNAPSHOT"),
    Festival("com.cavetale.festival", "0.1-SNAPSHOT"),
    FlatGenerator("com.cavetale.flatgenerator", "0.1-SNAPSHOT"),
    Fly("com.cavetale.fly", "0.1-SNAPSHOT"),
    FreeHat("com.cavetale.freehat", "0.1-SNAPSHOT"),
    GoldenTicket("com.cavetale.goldenticket", "0.1-SNAPSHOT"),
    @Deprecated
    Halloween("com.cavetale.halloween", "0.1-SNAPSHOT"),
    HideAndSeek("com.cavetale.hideandseek", "0.1-SNAPSHOT"),
    Home("com.cavetale.home", "0.1-SNAPSHOT"),
    HopperFilter("com.winthier.hopperfilter", "0.1-SNAPSHOT"),
    HotSwap("com.cavetale.hotswap", "0.1-SNAPSHOT"),
    Inventory("com.cavetale.inventory", "0.1-SNAPSHOT"),
    @Deprecated
    InvisibleItemFrames("com.cavetale.invisibleitemframes", "0.1-SNAPSHOT"),
    ItemStore("com.winthier.itemstore", "0.1-SNAPSHOT"),
    KeepInventory("com.winthier.keepinventory", "0.1-SNAPSHOT"),
    KingOfTheLadder("com.cavetale.kotl", "kotl", "0.1-SNAPSHOT"),
    // TODO: KingOfTheRing
    Kit("com.winthier.kit", "0.1"),
    // TODO: LastLog
    @Deprecated
    LinkPortal("com.winthier.linkportal", "0.1-SNAPSHOT"),
    MagicMap("com.cavetale.magicmap", "0.1-SNAPSHOT"),
    Mail("com.winthier.mail", "0.1-SNAPSHOT"),
    MapLoad("com.cavetale.mapload", "0.1-SNAPSHOT"),
    MassStorage("com.cavetale.massstorage", "0.1-SNAPSHOT"),
    Maypole("com.winthier.maypole", "0.1"),
    MemberList("com.cavetale.memberlist", "0.1-SNAPSHOT"),
    Menu("com.cavetale.menu", "0.1-SNAPSHOT"),
    Merchant("com.cavetale.merchant", "0.1-SNAPSHOT"),
    Miniverse("com.cavetale.miniverse", "0.1-SNAPSHOT"),
    // TODO: MobArena
    Money("com.cavetale.money", "0.1-SNAPSHOT"),
    Mytems("com.cavetale.mytems", "0.1-SNAPSHOT"),
    // TODO: NBTDump
    OpenInv(Util.uriOf("https://github.com/Jikoo/OpenInv/releases/download/5.1.3/OpenInv.jar"), "5.1.3"),
    Overboard("com.cavetale.overboard", "0.1-SNAPSHOT"),
    Perm("com.winthier.perm", "0.1-SNAPSHOT"),
    Photos("com.winthier.photos", "0.1-SNAPSHOT"),
    Pictionary("com.cavetale.pictionary", "0.1-SNAPSHOT"),
    PlayerCache("com.winthier.playercache", "0.1-SNAPSHOT"),
    PlayerInfo("com.winthier.playerinfo", "0.1-SNAPSHOT"),
    PlugInfo("com.cavetale.pluginfo", "0.1-SNAPSHOT"),
    PocketMob("com.cavetale.pocketmob", "0.1-SNAPSHOT"),
    Poster("com.cavetale.poster", "0.1-SNAPSHOT"),
    Protect("com.winthier.protect", "0.1-SNAPSHOT"),
    ProtocolLib("com.comphenix.protocol", "4.7.1-SNAPSHOT"), // TODO: External download
    PVPArena("com.cavetale.pvparena", "0.1-SNAPSHOT"),
    // TODO: Quidditch
    @Deprecated
    Quiz("com.winthier.quiz", "0.1-SNAPSHOT"),
    Race("com.cavetale.race", "0.1-SNAPSHOT"),
    @Deprecated
    Raid("com.cavetale.raid", "0.1-SNAPSHOT"),
    RandomPlayerHead("com.winthier.rph", "random-player-head", "0.1-SNAPSHOT"),
    RedGreenLight("com.cavetale.redgreenlight", "0.1-SNAPSHOT"),
    Resident("com.cavetale.resident", "0.1-SNAPSHOT"),
    Resource("com.winthier.resource", "0.1"),
    ResourcePack("com.cavetale.resourcepack", "0.1-SNAPSHOT"),
    Rules("com.winthier.rules", "0.1-SNAPSHOT"),
    Server("com.cavetale.server", "0.1-SNAPSHOT"),
    @Deprecated
    ServerStatus("com.cavetale.serverstatus", "0.1-SNAPSHOT"),
    Shop("com.winthier.shop", "0.1-SNAPSHOT"),
    Shutdown("com.winthier.shutdown", "0.1-SNAPSHOT"),
    Sidebar("com.cavetale.sidebar", "0.1-SNAPSHOT"),
    SignSpy("com.cavetale.signspy", "0.1-SNAPSHOT"),
    Skills("com.cavetale.skills", "0.1-SNAPSHOT"),
    // TODO: SkyBlock
    Spawn("com.winthier.spawn", "0.1-SNAPSHOT"),
    Spike("com.cavetale.spike", "0.1-SNAPSHOT"),
    Spleef("com.winthier.spleef", "0.1-SNAPSHOT"),
    SQL("com.winthier.sql", "0.1-SNAPSHOT"),
    StarBook("com.winthier.starbook", "0.1-SNAPSHOT"),
    StopRain("com.winthier.stoprain", "0.1-SNAPSHOT"),
    Streamer("com.cavetale.streamer", "0.1-SNAPSHOT"),
    Structure("com.cavetale.structure", "0.1-SNAPSHOT"),
    SurvivalGames("com.cavetale.survivalgames", "0.1-SNAPSHOT"),
    Televator("com.cavetale.televator", "0.1-SNAPSHOT"),
    // TODO: Territory
    // TODO: Tetris
    Ticket("com.winthier.ticket", "0.1-SNAPSHOT"),
    Tinfoil("com.winthier.tinfoil", "0.1"),
    Title("com.winthier.title", "0.1-SNAPSHOT"),
    TooManyEntities("com.winthier.toomanyentities", "0.1"),
    TPA("com.cavetale.tpa", "0.1-SNAPSHOT"),
    Trees("com.cavetale.trees", "0.1-SNAPSHOT"),
    Tutor("com.cavetale.tutor", "0.1-SNAPSHOT"),
    Vertigo("io.github.feydk.vertigo", "0.1-SNAPSHOT"),
    VoidGenerator("com.cavetale.voidgenerator", "0.1-SNAPSHOT"),
    Vote("com.cavetale.vote", "0.1-SNAPSHOT"),
    Wall("com.winthier.wall", "0.1-SNAPSHOT"),
    Wardrobe("com.cavetale.wardrobe", "0.1-SNAPSHOT"),
    Warp("com.cavetale.warp", "0.1-SNAPSHOT"),
    Watchman("com.cavetale.watchman", "0.1-SNAPSHOT"),
    // TODO: Waterfall
    // TODO: Windicator
    WinTag("com.cavetale.wintag", "0.1-SNAPSHOT"),
    WorldEdit(Util.uriOf("https://dev.bukkit.org/projects/worldedit/files/5935693/download"), "7.3.9"),
    WorldMarker("com.cavetale.worldmarker", "0.1-SNAPSHOT"),
    Worlds("com.winthier.worlds", "0.1-SNAPSHOT"),
    Xmas("com.cavetale.xmas", "0.1-SNAPSHOT");

    private final @NotNull Source source;
    private final @NotNull Plugin[] plugins;

    private boolean selected = false;
    private final @NotNull List<String> installations = new LinkedList<>();

    Plugin(@NotNull URI uri, @NotNull String version) {
        this.source = new Source.Other(uri, version);
        this.plugins = new Plugin[]{this};
    }

    Plugin(@NotNull String groupId, @NotNull String version) {
        this.source = new Source.Jenkins(this.name(), groupId, this.name().toLowerCase(), version);
        this.plugins = new Plugin[]{this};
    }

    Plugin(@NotNull String groupId, @NotNull String artifactId, @NotNull String version) {
        this.source = new Source.Jenkins(this.name(), groupId, artifactId, version);
        this.plugins = new Plugin[]{this};
    }

    @Override
    public @NotNull Plugin[] plugins() {
        return this.plugins;
    }

    @Override
    public @NotNull String toString() {
        return this.name();
    }

    public void setSelected(boolean value) {
        this.selected = value;
    }

    public boolean isSelected() {
        return this.selected;
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
        Console.log(Type.INFO, "Installing " + this.name() + " plugin");
        if (this.isInstalled()) {
            if (!Console.log(Type.INFO, Style.WARN, " skipped (already installed)\n")) {
                Console.log(Type.WARN, "Installing " + this.name() + " plugin skipped (already installed)\n");
            }
            return;
        }
        try {
            String file = this.name() + "-" + this.source.version + ".jar";
            Util.download(this.source.uri, new File(Plugins.FOLDER, file));
            this.installations.add(file);
            Console.log(Type.INFO, Style.DONE, " done\n");
        } catch (IOException e) {
            if (!Console.log(Type.INFO, Style.ERR, " failed\n")) {
                Console.log(Type.ERR, "Installing " + this.name() + " plugin failed\n");
            }
        }
    }

    public void update() {
        this.uninstall();
        this.install();
    }

    public void uninstall() {
        File folder = Plugins.FOLDER;
        for (String file : this.installations) {
            Console.log(Type.INFO, Style.DEBUG, "Uninstalling " + file);
            if (new File(folder, file).delete()) {
                Console.log(Type.INFO, Style.DONE, " done\n");
                continue;
            }
            if (!Console.log(Type.DEBUG, Style.ERR, " failed\n")) {
                Console.log(Type.ERR, "Uninstalling " + file + " plugin failed\n");
            }
        }
        this.installations.clear();
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
            super("Plugin \"" + ref + "\" not found");
            // TODO: Path flag suggestion
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
