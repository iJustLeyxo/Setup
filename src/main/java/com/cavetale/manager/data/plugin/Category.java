package com.cavetale.manager.data.plugin;

import com.cavetale.manager.data.Sel;
import com.cavetale.manager.parser.Flag;
import com.cavetale.manager.parser.InputException;
import com.cavetale.manager.parser.container.CategoryContainer;
import com.cavetale.manager.util.Util;
import com.cavetale.manager.util.console.Code;
import com.cavetale.manager.util.console.Console;
import com.cavetale.manager.util.console.Style;
import com.cavetale.manager.util.console.Type;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
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

    private @Nullable Sel sel = null;
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
    public @NotNull String toString() {
        return this.displayName();
    }

    @Override
    public Plugin[] plugins() {
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
        if (this.sel == null) Category.loadSelection();
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
    }

    //= Finder ==

    public static @NotNull Category get(@NotNull String ref) throws NotFoundException {
        for (Category c : values()) if (c.displayName().equalsIgnoreCase(ref)) return c;
        throw new NotFoundException(ref);
    }

    //= Indexing ==

    private static @Nullable List<Category> selected = null;
    private static @Nullable List<Category> installed = null;

    public static @NotNull List<Category> installed() {
        if (Category.installed == null) Category.loadInstallation();
        return Category.installed;
    }

    public static @NotNull List<Category> selected() {
        if (Category.selected == null) Category.loadSelection();
        return Category.selected;
    }

    public static void reset() {
        Console.log(Type.DEBUG, "Resetting categories\n");
        for (Category c : Category.values()) c.revert();
        Category.selected = null;
        Category.installed = null;
    }

    public static void loadSelection() {
        Console.log(Type.EXTRA, "Reloading selected categories\n");
        for (Category c : Category.values()) c.deselect();
        Category.selected = new LinkedList<>();

        CategoryContainer categories = (CategoryContainer) Flag.CATEGORY.container();
        if (Flag.INSTALLED.isSelected()) {
            Category.loadInstallation();
            Console.log(Type.DEBUG, "Selecting installed categories\n");
            for (Category c : Category.installed()) c.target();
        } else if (Flag.ALL.isSelected() || (Flag.CATEGORY.isSelected() && categories.isEmpty())) { // Select all
            Console.log(Type.DEBUG, "Selecting all categories\n");
            for (Category c : Category.values()) c.target();
        } else {
            Console.log(Type.DEBUG, "Selecting categories " + categories.get() + "\n"); // Select by category
            for (Category c : categories.get()) c.target();

            for (Server s : Server.values()) if (s.isSelected()) for (Category c : s.categories()) c.select(); // Select by server
        }

        for (Category c : Category.values()) if (c.isSelected()) Category.selected.add(c); // Update selection
    }

    public static void loadInstallation() {
        Console.log(Type.EXTRA, "Reloading installed categories\n");
        Category.installed = new LinkedList<>();

        for (Category c : Category.values()) if (c.isInstalled()) Category.installed.add(c); // Update installation
    }

    public static @NotNull List<Category> get(@Nullable Boolean installed, @Nullable Boolean selected) {
        List<Category> categories = new LinkedList<>();
        for (Category c : Category.values()) {
            if ((installed == null || installed == c.isInstalled()) &&
                    (selected == null || selected == c.isSelected())) {
                categories.add(c);
            }
        }
        return categories;
    }

    //= Cosmetics ==

    public static void requestAll() {
        Console.sep();

        if (Category.values().length == 0) {
            Console.log(Type.REQUESTED, Style.CATEGORY, Code.BOLD + "No categories available\n");
            return;
        }

        Console.logL(Type.REQUESTED, Style.CATEGORY, Category.values().length +
                " categor(y/ies) available", 4, 21, (Object[]) Category.values());
    }

    public static void listSelected() {
        if (Category.selected().isEmpty()) {
            Console.sep();
            Console.log(Type.REQUESTED, Style.CATEGORY, Code.BOLD + "No categories selected\n");
            return;
        }

        Console.sep();
        Console.logL(Type.REQUESTED, Style.CATEGORY, Category.selected().size() +
                " categor(y/ies) selected", 4, 21, Category.selected().toArray());
    }

    public static void requestInstalled() {
        Console.sep();

        if (Category.installed().isEmpty()) {
            Console.log(Type.REQUESTED, Style.CATEGORY, Code.BOLD + "No categories installed\n");
            return;
        }

        Console.logL(Type.REQUESTED, Style.CATEGORY, Category.installed().size() +
                " categor(y/ies) installed", 4, 21, Category.installed().toArray());
    }

    public static void details() {
        Console.sep();
        Console.log(Type.REQUESTED, Style.CATEGORY, Code.BOLD +
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