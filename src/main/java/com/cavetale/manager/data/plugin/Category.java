package com.cavetale.manager.data.plugin;

import com.cavetale.manager.parser.InputException;
import com.cavetale.manager.util.console.Console;
import com.cavetale.manager.util.console.Style;
import com.cavetale.manager.util.console.Type;
import com.cavetale.manager.util.console.XCode;
import com.sun.source.tree.Tree;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.cavetale.manager.data.plugin.Plugin.*;

/**
 * Plugin categories, used to group plugins by purpose / usage
 */
public enum Category implements Provider {
    Global("Global plugins", AFK, AntiPopup, Area, ArmorStandEditor, Auction, Bans, BlockClip,
            BlockTrigger, Countdown, Editor, Fam, Fly, Plugin.Home, HotSwap, Inventory, ItemStore,
            Kit, MagicMap, Mail, MemberList, Menu, OpenInv, PlayerInfo, PlugInfo, Protect,
            ResourcePack, Rules, Server, Shutdown, Spawn, Spike, StarBook, StopRain, Streamer,
            Televator, Ticket, Tinfoil, TPA, Tutor, Vote, Wall, Wardrobe, Warp, WorldEdit, Worlds),
    Build("Plugins for build servers", Chair, FreeHat, Merchant, Photos, RandomPlayerHead, SignSpy,
            TooManyEntities, Trees, Watchman, WinTag),
    Core("Essential plugins", Chat, Connect, Plugin.Core, Money, Mytems, Perm, PlayerCache, Sidebar,
            SQL, Title, VoidGenerator, WorldMarker),
    Creative("Plugins for creative servers", Plugin.Creative, FlatGenerator, Pictionary),
    Event("Plugins for events without a dedicated server"),
    Home("Plugins for home servers", CullMob),
    Hub("Plugins for hub servers", ExtremeGrassGrowing, KingOfTheLadder, RedGreenLight),
    Mine("Plugins for mine servers", Dungeons),
    MiniGame("Plugins for mini game servers", Bingo, Colorfall, Enderball, HideAndSeek, Overboard,
            PVPArena, Race, Spleef, SurvivalGames, Vertigo),
    Seasonal("Seasonal event plugins", Easter, Festival, Maypole, Xmas),
    Survival("Plugins for survival servers", Dusk, Election, Enemy, Exploits, FastLeafDecay,
            GoldenTicket, HopperFilter, KeepInventory, MassStorage, PocketMob, Poster, Resident,
            Resource, Shop, Skills, Structure),
    Util("Optional utility plugins", MapLoad, Miniverse, ProtocolLib),
    WorldGen("World generation plugins", Caves, Decorator);

    private final @NotNull String info;
    private final @NotNull Plugin[] plugins;

    Category(@NotNull String info, @NotNull Plugin @NotNull ... plugins) {
        this.info = info;
        this.plugins = plugins;
    }

    @Override
    public Plugin[] plugins() {
        return this.plugins;
    }

    @Override
    public @NotNull String toString() {
        return this.name();
    }

    public static @NotNull Category get(@NotNull String ref) throws NotFoundException {
        for (Category c : values()) if (c.name().equalsIgnoreCase(ref)) return c;
        throw new NotFoundException(ref);
    }

    public static void list() {
        Console.sep();
        Console.log(Type.REQUESTED, Style.CATEGORY, XCode.BOLD +
                "-------------------------------------- " +
                "Categories -------------------------------------\n");
        Console.logF(Type.REQUESTED, Style.CATEGORY, "%-16s | %-68s\n", "Category", "Info");
        Console.log(Type.REQUESTED, Style.CATEGORY, "--------------------------------------------" +
                "-------------------------------------------\n");
        ArrayList<Category> categories = new ArrayList<>(List.of(Category.values()));
        Collections.sort(categories);
        for (Category c : categories) Console.logF(Type.REQUESTED, Style.CATEGORY, "%-16s | %-68s\n", c.name(), c.info);
    }

    public static final class NotFoundException extends InputException {
        public NotFoundException(@NotNull String name) {
            super("Category \"" + name + "\" not found");
        }
    }
}