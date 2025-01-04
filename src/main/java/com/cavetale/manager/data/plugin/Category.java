package com.cavetale.manager.data.plugin;

import com.cavetale.manager.parser.InputException;
import com.cavetale.manager.util.console.Console;
import com.cavetale.manager.util.console.Style;
import com.cavetale.manager.util.console.Type;
import com.cavetale.manager.util.console.XCode;
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
    Creative("Plugins for creative servers", Plugin.Creative, FlatGenerator, VoidGenerator),
    Home("Plugins for home servers", CullMob),
    Hub("Plugins for hub servers", ExtremeGrassGrowing, KingOfTheLadder, RedLightGreenLight),
    Mine("Plugins for mining servers", Dungeons),
    Event("Plugins for events", Bingo, Colorfall, Enderball, ExtremeGrassGrowing,
            HideAndSeek, KingOfTheLadder, Overboard, Pictionary, PVPArena, Race, RedLightGreenLight,
            Spleef, SurvivalGames, Vertigo),
    Seasonal("Plugins for seasonal events", Easter, Festival, Maypole, Xmas),
    Survival("Plugins for survival servers", Dusk, Election, Enemy, Exploits, FastLeafDecay,
            GoldenTicket, HopperFilter, KeepInventory, MassStorage, PocketMob, Poster, Resident,
            Resource, Shop, Skills, Structure),
    Util("Optional utility plugins", MapLoad, Miniverse, ProtocolLib),
    WorldGen("World generation plugins", Caves, Decorator),

    Base("Basic plugins for all servers", Global, Core);

    private final @NotNull String info;
    private final @NotNull Plugin[] plugins;

    private boolean selected = false;
    private boolean installed = false;

    Category(@NotNull String info, @NotNull Plugin @NotNull ... plugins) {
        this.info = info;
        this.plugins = plugins;
    }

    Category(@NotNull String info, @NotNull Category @NotNull ... children) {
        this.info = info;

        int len = 0; // Copy plugins
        for (Category child : children) len += child.plugins().length;
        this.plugins = new Plugin[len];
        len = 0;
        for (Category child : children) {
            System.arraycopy(child.plugins, 0, this.plugins, len, child.plugins.length);
            len += child.plugins.length;
        }
    }

    @Override
    public Plugin[] plugins() {
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

    public void setInstalled() {
        this.installed = true;

        for (Plugin p : this.plugins) {
            if (!p.isInstalled()) {
                this.installed = false;
                return;
            }
        }
    }

    public boolean isInstalled() {
        return this.installed;
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