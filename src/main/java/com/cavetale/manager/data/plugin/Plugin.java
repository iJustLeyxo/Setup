package com.cavetale.manager.data.plugin;

import com.cavetale.manager.data.DataError;
import com.cavetale.manager.data.DataException;
import com.cavetale.manager.data.Source;
import com.cavetale.manager.parser.InputException;
import com.cavetale.manager.util.Util;
import com.cavetale.manager.util.console.Console;
import com.cavetale.manager.util.console.Style;
import com.cavetale.manager.util.console.Type;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Set;

/**
 * List of available plugins
 */
public enum Plugin implements Provider {
    // TODO: Adventure
    AdviceAnimals("com.winthier.adviceanimals", "0.1-SNAPSHOT", Category.Deprecated),
    AFK("com.cavetale.afk", "0.1-SNAPSHOT", Category.Global),
    AntiPopup(Util.uriOf("https://github.com/KaspianDev/AntiPopup/releases/download/499358a/AntiPopup-10.jar"), "10", Category.Global),
    Area("com.cavetale.area", "0.1-SNAPSHOT", Category.Global),
    ArmorStandEditor("io.github.rypofalem.armorstandeditor", "1.17-25", Category.Global),
    Auction("com.cavetale.auction", "0.1-SNAPSHOT", Category.Global),
    Bans("com.winthier.bans", "0.1-SNAPSHOT", Category.Global),
    Bingo("com.cavetale.bingo", "0.1-SNAPSHOT", Category.MiniGame),
    BlockClip("com.cavetale.blockclip", "0.1-SNAPSHOT", Category.Global),
    BlockTrigger("com.cavetale.blocktrigger", "0.1-SNAPSHOT", Category.Global),
    // TODO: BungeeCavetale
    // TODO: CaptureTheFlag
    Caves("com.cavetale.caves", "0.1-SNAPSHOT", Category.WorldGen),
    // TODO: CavetaleResourcePack
    Chair("com.cavetale.chair", "0.1-SNAPSHOT", Category.Build),
    Chat("com.winthier.chat", "0.1-SNAPSHOT", Category.Core),
    // TODO: Chess
    Christmas("com.cavetale.christmas", "0.1-SNAPSHOT", Category.Deprecated),
    Colorfall("io.github.feydk.colorfall", "0.1-SNAPSHOT", Category.MiniGame),
    Connect("com.winthier.connect", "0.1-SNAPSHOT", Category.Core),
    // TODO: ConnectCore
    Core("com.cavetale.core", "0.1-SNAPSHOT", Category.Core),
    Countdown("com.winthier.countdown", "0.1", Category.Global),
    CraftBay("com.winthier.craftbay", "2.26-SNAPSHOT", Category.Deprecated),
    Creative("com.winthier.creative", "0.1-SNAPSHOT", Category.Creative),
    CullMob("com.cavetale.cullmob", "0.1-SNAPSHOT", Category.Home),
    Decorator("com.winthier.decorator", "0.1-SNAPSHOT", Category.WorldGen),
    Dungeons("com.cavetale.dungeons", "0.1-SNAPSHOT", Category.Mine),
    Dusk("com.winthier.dusk", "0.1", Category.Survival),
    Easter("com.cavetale.easter", "0.1-SNAPSHOT", Category.Seasonal),
    Editor("com.cavetale.editor", "0.1-SNAPSHOT", Category.Global),
    Election("com.cavetale.election", "0.1-SNAPSHOT", Category.Survival),
    Enderball("com.cavetale.enderball", "0.1-SNAPSHOT", Category.MiniGame),
    Enemy("com.cavetale.enemy", "0.1-SNAPSHOT", Category.Survival),
    Exploits("com.winthier.exploits", "0.1-SNAPSHOT", Category.Survival),
    ExtremeGrassGrowing("com.cavetale.extremegrassgrowing", "0.1-SNAPSHOT", Category.Hub),
    Fam("com.cavetale.fam", "0.1-SNAPSHOT", Category.Global),
    FastLeafDecay("com.cavetale.fastleafdecay", "1.0-SNAPSHOT", Category.Survival),
    Festival("com.cavetale.festival", "0.1-SNAPSHOT", Category.Seasonal),
    FlatGenerator("com.cavetale.flatgenerator", "0.1-SNAPSHOT", Category.Creative),
    Fly("com.cavetale.fly", "0.1-SNAPSHOT", Category.Global),
    FreeHat("com.cavetale.freehat", "0.1-SNAPSHOT", Category.Build),
    GoldenTicket("com.cavetale.goldenticket", "0.1-SNAPSHOT", Category.Survival),
    Halloween("com.cavetale.halloween", "0.1-SNAPSHOT", Category.Deprecated),
    HideAndSeek("com.cavetale.hideandseek", "0.1-SNAPSHOT", Category.MiniGame),
    Home("com.cavetale.home", "0.1-SNAPSHOT", Category.Global),
    HopperFilter("com.winthier.hopperfilter", "0.1-SNAPSHOT", Category.Survival),
    HotSwap("com.cavetale.hotswap", "0.1-SNAPSHOT", Category.Global),
    Inventory("com.cavetale.inventory", "0.1-SNAPSHOT", Category.Global),
    InvisibleItemFrames("com.cavetale.invisibleitemframes", "0.1-SNAPSHOT", Category.Deprecated),
    ItemStore("com.winthier.itemstore", "0.1-SNAPSHOT", Category.Global),
    KeepInventory("com.winthier.keepinventory", "0.1-SNAPSHOT", Category.Survival),
    KingOfTheLadder("com.cavetale.kotl", "kotl", "0.1-SNAPSHOT", Category.Hub),
    // TODO: KingOfTheRing
    Kit("com.winthier.kit", "0.1", Category.Global),
    // TODO: LastLog
    LinkPortal("com.winthier.linkportal", "0.1-SNAPSHOT", Category.Deprecated),
    MagicMap("com.cavetale.magicmap", "0.1-SNAPSHOT", Category.Global),
    Mail("com.winthier.mail", "0.1-SNAPSHOT", Category.Global),
    MapLoad("com.cavetale.mapload", "0.1-SNAPSHOT", Category.Util),
    MassStorage("com.cavetale.massstorage", "0.1-SNAPSHOT", Category.Survival),
    Maypole("com.winthier.maypole", "0.1", Category.Seasonal),
    MemberList("com.cavetale.memberlist", "0.1-SNAPSHOT", Category.Global),
    Menu("com.cavetale.menu", "0.1-SNAPSHOT", Category.Global),
    Merchant("com.cavetale.merchant", "0.1-SNAPSHOT", Category.Build),
    Miniverse("com.cavetale.miniverse", "0.1-SNAPSHOT", Category.Util),
    // TODO: MobArena
    Money("com.cavetale.money", "0.1-SNAPSHOT", Category.Core),
    Mytems("com.cavetale.mytems", "0.1-SNAPSHOT", Category.Core),
    // TODO: NBTDump
    OpenInv(Util.uriOf("https://github.com/Jikoo/OpenInv/releases/download/5.1.3/OpenInv.jar"), "5.1.3", Category.Global),
    Overboard("com.cavetale.overboard", "0.1-SNAPSHOT", Category.MiniGame),
    Perm("com.winthier.perm", "0.1-SNAPSHOT", Category.Core),
    Photos("com.winthier.photos", "0.1-SNAPSHOT", Category.Build),
    Picitonary("com.cavetale.pictionary", "0.1-SNAPSHOT", Category.Creative),
    PlayerCache("com.winthier.playercache", "0.1-SNAPSHOT", Category.Core),
    PlayerInfo("com.winthier.playerinfo", "0.1-SNAPSHOT", Category.Global),
    PlugInfo("com.cavetale.pluginfo", "0.1-SNAPSHOT", Category.Global),
    PocketMob("com.cavetale.pocketmob", "0.1-SNAPSHOT", Category.Survival),
    Poster("com.cavetale.poster", "0.1-SNAPSHOT", Category.Survival),
    Protect("com.winthier.protect", "0.1-SNAPSHOT", Category.Global),
    ProtocolLib("com.comphenix.protocol", "4.7.1-SNAPSHOT", Category.Util), // TODO: External download
    PVPArena("com.cavetale.pvparena", "0.1-SNAPSHOT", Category.MiniGame),
    // TODO: Quidditch
    Quiz("com.winthier.quiz", "0.1-SNAPSHOT", Category.Deprecated),
    Race("com.cavetale.race", "0.1-SNAPSHOT", Category.MiniGame),
    Raid("com.cavetale.raid", "0.1-SNAPSHOT", Category.MiniGame),
    RandomPlayerHead("com.winthier.rph", "random-player-head", "0.1-SNAPSHOT", Category.Build),
    RedGreenLight("com.cavetale.redgreenlight", "0.1-SNAPSHOT", Category.Hub),
    Resident("com.cavetale.resident", "0.1-SNAPSHOT", Category.Survival),
    Resource("com.winthier.resource", "0.1", Category.Survival),
    ResourcePack("com.cavetale.resourcepack", "0.1-SNAPSHOT", Category.Global),
    Rules("com.winthier.rules", "0.1-SNAPSHOT", Category.Global),
    Server("com.cavetale.server", "0.1-SNAPSHOT", Category.Global),
    ServerStatus("com.cavetale.serverstatus", "0.1-SNAPSHOT", Category.Deprecated),
    Shop("com.winthier.shop", "0.1-SNAPSHOT", Category.Survival),
    Shutdown("com.winthier.shutdown", "0.1-SNAPSHOT", Category.Global),
    Sidebar("com.cavetale.sidebar", "0.1-SNAPSHOT", Category.Core),
    SignSpy("com.cavetale.signspy", "0.1-SNAPSHOT", Category.Build),
    Skills("com.cavetale.skills", "0.1-SNAPSHOT", Category.Survival),
    // TODO: SkyBlock
    Spawn("com.winthier.spawn", "0.1-SNAPSHOT", Category.Global),
    Spike("com.cavetale.spike", "0.1-SNAPSHOT", Category.Global),
    Spleef("com.winthier.spleef", "0.1-SNAPSHOT", Category.MiniGame),
    SQL("com.winthier.sql", "0.1-SNAPSHOT", Category.Core),
    StarBook("com.winthier.starbook", "0.1-SNAPSHOT", Category.Global),
    StopRain("com.winthier.stoprain", "0.1-SNAPSHOT", Category.Global),
    Streamer("com.cavetale.streamer", "0.1-SNAPSHOT", Category.Global),
    Structure("com.cavetale.structure", "0.1-SNAPSHOT", Category.Survival),
    SurvivalGames("com.cavetale.survivalgames", "0.1-SNAPSHOT", Category.MiniGame),
    Televator("com.cavetale.televator", "0.1-SNAPSHOT", Category.Global),
    // TODO: Territory
    // TODO: Tetris
    Ticket("com.winthier.ticket", "0.1-SNAPSHOT", Category.Global),
    Tinfoil("com.winthier.tinfoil", "0.1", Category.Global),
    Title("com.winthier.title", "0.1-SNAPSHOT", Category.Core),
    TooManyEntities("com.winthier.toomanyentities", "0.1", Category.Build),
    TPA("com.cavetale.tpa", "0.1-SNAPSHOT", Category.Global),
    Trees("com.cavetale.trees", "0.1-SNAPSHOT", Category.Build),
    Tutor("com.cavetale.tutor", "0.1-SNAPSHOT", Category.Global),
    Vertigo("io.github.feydk.vertigo", "0.1-SNAPSHOT", Category.MiniGame),
    VoidGenerator("com.cavetale.voidgenerator", "0.1-SNAPSHOT", Category.Core),
    Vote("com.cavetale.vote", "0.1-SNAPSHOT", Category.Global),
    Wall("com.winthier.wall", "0.1-SNAPSHOT", Category.Global),
    Wardrobe("com.cavetale.wardrobe", "0.1-SNAPSHOT", Category.Global),
    Warp("com.cavetale.warp", "0.1-SNAPSHOT", Category.Global),
    Watchman("com.cavetale.watchman", "0.1-SNAPSHOT", Category.Build),
    // TODO: Waterfall
    // TODO: Windicator
    WinTag("com.cavetale.wintag", "0.1-SNAPSHOT", Category.Build),
    WorldEdit(Util.uriOf("https://dev.bukkit.org/projects/worldedit/files/5935693/download"), "7.3.9", Category.Global),
    WorldMarker("com.cavetale.worldmarker", "0.1-SNAPSHOT", Category.Core),
    Worlds("com.winthier.worlds", "0.1-SNAPSHOT", Category.Global),
    Xmas("com.cavetale.xmas", "0.1-SNAPSHOT", Category.Seasonal);

    private final @NotNull Source source;
    public final @NotNull Category[] categories;

    Plugin(@NotNull URI uri, @NotNull String version, @NotNull Category... categories) {
        this.source = new Source.Other(uri, version);
        this.categories = categories;
    }

    Plugin(@NotNull String groupId, @NotNull String version, @NotNull Category... categories) {
        this.source = new Source.Jenkins(this.name(), groupId, this.name().toLowerCase(), version);
        this.categories = categories;
    }

    Plugin(@NotNull String groupId, @NotNull String artifactId, @NotNull String version,
           @NotNull Category... categories) {
        this.source = new Source.Jenkins(this.name(), groupId, artifactId, version);
        this.categories = categories;
    }

    public void install() {
        Console.log(Type.INFO, "Installing " + this.name() + " plugin");
        if (PlugIndexer.active.getInstalled().containsKey(this)) {
            if (!Console.log(Type.INFO, Style.WARN, " skipped (already installed)\n")) {
                Console.log(Type.WARN, "Installing " + this.name() + " plugin skipped (already installed)\n");
            }
            return;
        }
        File file = new File("plugins/" + this.name() + "-" + this.source.version + ".jar");
        try {
            Util.download(this.source.uri, file);
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
        Set<File> files = PlugIndexer.active.installed.get(this);
        if (files == null) return;
        File folder = new File("plugins/");
        for (File f : files) {
            Console.log(Type.INFO, Style.DEBUG, "Uninstalling " + f.getName());
            if (new File(folder, f.getName()).delete()) {
                Console.log(Type.INFO, Style.DONE, " done\n");
                continue;
            }
            if (!Console.log(Type.DEBUG, Style.ERR, " failed\n")) {
                Console.log(Type.ERR, "Uninstalling " + f.getName() + " plugin failed\n");
            }
        }
    }

    @Contract(value = " -> new", pure = true)
    @Override
    public @NotNull @Unmodifiable Set<Plugin> plugins() {
        return Set.of(this);
    }

    @Override
    public @NotNull String toString() {
        return this.name();
    }

    public static @NotNull Plugin get(@NotNull String ref) throws PluginNotFoundException {
        for (Plugin p : Plugin.values()) if (p.name().equalsIgnoreCase(ref)) return p;
        throw new PluginNotFoundException(ref);
    }

    public static @NotNull Plugin get(@NotNull File file) throws NotAPluginException, PluginNotFoundException {
        String ref = file.getName().toLowerCase();
        if (!file.getPath().endsWith(".jar")) throw new NotAPluginException(file);
        int verStart = ref.indexOf("-");
        if (verStart < 0) verStart = ref.length() - 1;
        int extStart = ref.indexOf(".");
        if (extStart < 0) extStart = ref.length() - 1;
        int endStart = Math.min(verStart, extStart);
        ref = ref.substring(0, endStart);
        for (Plugin p : Plugin.values()) if (ref.equalsIgnoreCase(p.name())) return p;
        throw new PluginNotFoundException(ref);
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
