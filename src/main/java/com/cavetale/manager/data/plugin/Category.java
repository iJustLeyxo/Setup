package com.cavetale.manager.data.plugin;

import com.cavetale.manager.data.Sel;
import com.cavetale.manager.parser.InputException;
import com.cavetale.manager.util.Util;
import com.cavetale.manager.util.console.Console;
import com.cavetale.manager.util.console.Style;
import com.cavetale.manager.util.console.Type;
import com.cavetale.manager.util.console.XCode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.cavetale.manager.data.plugin.Plugin.*;

/**
 * Plugin categories, used to group plugins by purpose / usage
 */
public enum Category implements Provider {
    GLOBAL("Global plugins", A_F_K, ANTI_POPUP, AREA, ARMOR_STAND_EDITOR, AUCTION, BANS, BLOCK_CLIP,
            BLOCK_TRIGGER, COUNTDOWN, EDITOR, FAM, FLY, Plugin.HOME, HOT_SWAP, INVENTORY, ITEM_STORE,
            KIT, MAGIC_MAP, MAIL, MEMBER_LIST, MENU, OPEN_INV, PLAYER_INFO, PLUG_INFO, PROTECT,
            RESOURCE_PACK, RULES, SERVER, SHUTDOWN, SPAWN, SPIKE, STAR_BOOK, STOP_RAIN, STREAMER,
            TELEVATOR, TICKET, TINFOIL, T_P_A, TUTOR, VOTE, WALL, WARDROBE, WARP, WORLD_EDIT, WORLDS),
    BUILD("Plugins for build servers", CHAIR, FREE_HAT, MERCHANT, PHOTOS, RANDOM_PLAYER_HEAD, SIGN_SPY,
            TOO_MANY_ENTITIES, TREES, WATCHMAN, WIN_TAG),
    CORE("Essential plugins", CHAT, CONNECT, Plugin.CORE, MONEY, MYTEMS, PERM, PLAYER_CACHE, SIDEBAR,
            S_Q_L, TITLE, VOID_GENERATOR, WORLD_MARKER),
    CREATIVE("Plugins for creative servers", Plugin.CREATIVE, FLAT_GENERATOR, VOID_GENERATOR),
    HOME("Plugins for home servers", CULL_MOB),
    HUB("Plugins for hub servers", CHESS, EXTREME_GRASS_GROWING, KING_OF_THE_LADDER, RED_GREEN_LIGHT),
    MINE("Plugins for mining servers", DUNGEONS),
    EVENT("Plugins for events", BINGO, CAPTURE_THE_FLAG, COLORFALL, ENDERBALL, EXTREME_GRASS_GROWING,
            HIDE_AND_SEEK, KING_OF_THE_LADDER, MOB_ARENA, OVERBOARD, PICTIONARY, P_V_P_ARENA, QUIDDITCH, RACE, RED_GREEN_LIGHT,
            SPLEEF, SURVIVAL_GAMES, TETRIS, VERTIGO, WINDICATOR),
    SEASONAL("Plugins for seasonal events", EASTER, FESTIVAL, MAYPOLE, X_MAS),
    SURVIVAL("Plugins for survival servers", DUSK, ELECTION, ENEMY, EXPLOITS, FAST_LEAF_DECAY,
            GOLDEN_TICKET, HOPPER_FILTER, KEEP_INVENTORY, MASS_STORAGE, POCKET_MOB, POSTER, RESIDENT,
            RESOURCE, SHOP, SKILLS, STRUCTURE),
    UTIL("Optional utility plugins", MAP_LOAD, MINIVERSE, PROTOCOL_LIB),
    WORLD_GEN("World generation plugins", CAVES, DECORATOR),

    BASE("Basic plugins for all servers", GLOBAL, CORE);

    private final @NotNull String name;
    private final @NotNull String info;
    private final @NotNull Plugin[] plugins;

    // TODO: Custom printing
    private @NotNull Sel sel = Sel.NONE;
    private @Nullable Boolean inst = null;

    Category(@NotNull String info, @NotNull Plugin @NotNull ... plugins) {
        this.name = Util.capsToCamel(this.name());
        this.info = info;
        this.plugins = plugins;
    }

    Category(@NotNull String info, @NotNull Category @NotNull ... children) {
        this.name = Util.capsToCamel(this.name());
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

    public @NotNull String displayName() {
        return this.name;
    }

    @Override
    public Plugin[] plugins() {
        return this.plugins;
    }

    @Override
    public @NotNull String toString() {
        return this.displayName();
    }

    public void reset() {
        this.sel = Sel.NONE;
        this.inst = null;
    }

    //= Selection ==

    public void target() {
        this.sel = Sel.TARGET;
    }

    public boolean isTargeted() {
        return this.sel == Sel.TARGET;
    }

    public void select() {
        if (this.sel == Sel.NONE) this.sel = Sel.NORMAL;
    }

    public boolean isSelected() {
        return this.sel != Sel.NONE;
    }

    //= Installation ==

    public boolean isInstalled() {
        if (this.inst != null) return this.inst;
        return this.checkInstalled();
    }

    private boolean checkInstalled() {
        this.inst = true;

        for (Plugin p : this.plugins) if (!p.isInstalled()) {
            this.inst = false;
            return false;
        }

        return true;
    }

    public static @NotNull Category get(@NotNull String ref) throws NotFoundException {
        for (Category c : values()) if (c.displayName().equalsIgnoreCase(ref)) return c;
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
        for (Category c : categories) Console.logF(Type.REQUESTED, Style.CATEGORY, "%-16s | %-68s\n", c.displayName(), c.info);
    }

    public static final class NotFoundException extends InputException {
        public NotFoundException(@NotNull String name) {
            super("Category \"" + name + "\" not found");
        }
    }
}