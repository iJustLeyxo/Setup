package com.cavetale.setup.data;

import com.cavetale.setup.cmd.CustomFlag;
import com.cavetale.setup.cmd.CategoryContents;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;
import java.util.List;

import static com.cavetale.setup.cmd.CustomStyle.CATEGORY;
import static com.cavetale.setup.data.Plugin.*;
import static link.l_pf.cmdlib.shell.Code.Std.BOLD;
import static link.l_pf.cmdlib.shell.Shell.STDIO;

/** Plugin categories, used to group plugins by purpose or usage. */
public enum PluginCategory implements Provider {
    /** General plugins for all servers. */
    GLOBAL("Global plugins", A_F_K, ANTI_POPUP, AREA, ARMOR_STAND_EDITOR,
        AUCTION, BANS, BLOCK_CLIP, BLOCK_TRIGGER, COUNTDOWN, EDITOR, FAM, FLY,
        Plugin.HOME, HOT_SWAP, INVENTORY, ITEM_STORE, KIT, MAGIC_MAP, MAIL,
        MEMBER_LIST, MENU, OPEN_INV, PLAYER_INFO, PLUG_INFO, PROTECT, QUEST,
        RESOURCE_PACK, RULES, SERVER, SHUTDOWN, SPAWN, SPIKE, STAR_BOOK,
        STOP_RAIN, STREAMER, TELEVATOR, TICKET, TINFOIL, T_P_A, TUTOR, VOTE,
        WALL, WARDROBE, WARP, WORLD_EDIT, WORLDS
    ),

    /** Plugins for build servers. */
    BUILD("Plugins for build servers", CHAIR, FREE_HAT, MERCHANT, PHOTOS,
        RANDOM_PLAYER_HEAD, SIGN_SPY, TOO_MANY_ENTITIES, TREES, WATCHMAN,
        WIN_TAG
    ),

    /** Core plugins. */
    CORE("Essential plugins", CHAT, CONNECT, Plugin.CORE, MONEY, MYTEMS,
        PERM, PLAYER_CACHE, SIDEBAR, S_Q_L, TITLE, VOID_GENERATOR, WORLD_MARKER
    ),

    /** Plugins for creative servers. */
    CREATIVE("Plugins for creative servers", Plugin.CREATIVE,
        FLAT_GENERATOR, VOID_GENERATOR
    ),

    /** Plugins for home servers. */
    HOME("Plugins for home servers", CULL_MOB),

    /** Plugins for hub servers. */
    HUB("Plugins for hub servers", CHESS, EXTREME_GRASS_GROWING,
        FREE_FOOD, KING_OF_THE_LADDER, RED_GREEN_LIGHT
    ),

    /** Plugins for mining servers. */
    MINE("Plugins for mining servers", DUNGEONS, HIVE),

    /** Event plugins. */
    EVENT("Plugins for events", BINGO, BUILD_MY_THING, CAPTURE_THE_FLAG,
        COLORFALL, ENDER_BALL, ENDER_GOLF, EXTREME_GRASS_GROWING, HIDE_AND_SEEK,
        KING_OF_THE_LADDER, MOB_ARENA, OVERBOARD, PICTIONARY, P_V_P_ARENA,
        QUIDDITCH, RACE, RED_GREEN_LIGHT, SPLEEF, SURVIVAL_GAMES, TETRIS,
        VERTIGO, WINDICATOR
    ),

    /** Seasonal plugins. */
    SEASONAL("Plugins for seasonal events", EASTER, FESTIVAL, MAYPOLE,
        BAR_CRAWL, X_MAS
    ),

    /** Plugins for survival servers. */
    SURVIVAL("Plugins for survival servers", DUSK, ELECTION, ENEMY,
        EXPLOITS, FAST_LEAF_DECAY, GOLDEN_TICKET, HOPPER_FILTER, KEEP_INVENTORY,
        MASS_STORAGE, POCKET_MOB, POSTER, RESIDENT, RESOURCE, SHOP, SKILLS,
        STRUCTURE
    ),

    /** Utility plugins. */
    UTIL("Optional utility plugins", MAP_LOAD, MINIVERSE, PROTOCOL_LIB),

    /** World generation plugins. */
    WORLD_GEN("World generation plugins", CAVES, DECORATOR),

    /** Base plugins for all servers. */
    BASE("Basic plugins for all servers", GLOBAL, CORE);

    /** Category name. */
    private final @NotNull String name;

    /** Category description. */
    private final @NotNull String info;

    /** Category contents. */
    private final @NotNull Plugin[] plugins;

    /** Category selection state. */
    private @Nullable Sel sel = null;

    /** Category installation state. */
    private @Nullable Boolean inst = null;

    /**
     * Creates a new category.
     * @param info The category description.
     * @param plugins The category plugins.
     */
    PluginCategory(@NotNull String info, @NotNull Plugin @NotNull ... plugins) {
        this.name = Data.capsToCamel(this.name());
        this.info = info;
        this.plugins = plugins;
    }

    /**
     * Creates a new parent category.
     * @param info The category description.
     * @param children The child categories.
     */
    PluginCategory(@NotNull String info, @NotNull PluginCategory @NotNull ... children) {
        this.name = Data.capsToCamel(this.name());
        this.info = info;

        int len = 0; // Copy plugins
        for (PluginCategory child : children) len += child.plugins().length;
        this.plugins = new Plugin[len];
        len = 0;
        for (PluginCategory child : children) {
            System.arraycopy(child.plugins, 0, this.plugins, len, child.plugins.length);
            len += child.plugins.length;
        }
    }

    /**
     * Gets the categories' display name.
     * @return The categories' display name.
     */
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

    /** Resets the category. */
    public void revert() {
        this.sel = null;
        this.inst = null;
    }

    //= Selection ==

    /** Sets this categories' selection state to target. */
    public void target() {
        this.sel = Sel.TARGET;
    }

    /** Sets this categories' selection state to active. */
    public void select() {
        if (this.sel != Sel.TARGET) this.sel = Sel.ON;
    }

    /** Deselects this category. */
    public void deselect() {
        this.sel = Sel.OFF;
    }

    /**
     * Returns whether this category is selected.
     * @return {@code true} if and only if this category is selected.
     */
    public boolean isSelected() {
        if (this.sel == null) PluginCategory.selected();
        return this.sel != Sel.OFF;
    }

    //= Installation ==

    /**
     * Returns whether this category is selected.
     * @return {@code true} if and only if this category is selected.
     */
    public boolean isInstalled() {
        if (this.inst == null) this.checkInstalled();
        return this.inst;
    }

    /** Determines whether this category is installed. */
    private void checkInstalled() {
        this.inst = true;

        for (Plugin p : this.plugins) if (!p.isInstalled()) {
            this.inst = false;
            return;
        }
    }

    //= Finder ==

    /**
     * Finds the category matching a specific reference.
     * @param ref The reference of the category to find.
     * @return The category - if found.
     * @throws NotFoundException If no matching category was found.
     */
    public static @NotNull PluginCategory get(@NotNull String ref) throws NotFoundException {
        for (PluginCategory c : values()) if (c.displayName().equalsIgnoreCase(ref)) return c;
        throw new NotFoundException(ref);
    }

    //= Indexing ==

    /** List of selected categories. */
    private static @Nullable List<PluginCategory> selected = null;

    /** List of installed categories. */
    private static @Nullable List<PluginCategory> installed = null;

    /**
     * Returns the selected categories. Determines them beforehand if necessary.
     * @return A list of selected categories.
     */
    public static @NotNull List<PluginCategory> selected() {
        if (PluginCategory.selected != null) return PluginCategory.selected; // Update not necessary

        // Update selection
        STDIO.debug("Reloading selected categories");
        for (PluginCategory c : PluginCategory.values()) c.deselect();
        PluginCategory.selected = new LinkedList<>();

        CategoryContents categories = (CategoryContents) CustomFlag.CATEGORY.container();
        if (CustomFlag.INSTALLED.isSelected()) {
            PluginCategory.installed();
            STDIO.debug("Selecting installed categories");
            for (PluginCategory c : PluginCategory.installed()) c.target();
        } else if (CustomFlag.ALL.isSelected() || (CustomFlag.CATEGORY.isSelected() && categories.isEmpty())) { // Select all
            STDIO.debug("Selecting all categories");
            for (PluginCategory c : PluginCategory.values()) c.target();
        } else {
            STDIO.debug("Selecting categories ", categories.contents()); // Select by category
            for (PluginCategory c : categories.contents()) c.target();

            for (PluginServer s : PluginServer.values()) if (s.isSelected()) for (PluginCategory c : s.categories()) c.select(); // Select by server
        }

        for (PluginCategory c : PluginCategory.values()) if (c.isSelected()) PluginCategory.selected.add(c); // Update selection
        return PluginCategory.selected;
    }

    /**
     * Returns installed categories. Determines the beforehand if necessary.
     * @return A list of installed categories.
     */
    public static @NotNull List<PluginCategory> installed() {
        if (PluginCategory.installed != null) return PluginCategory.installed; // Update not necessary

        // Update installation
        STDIO.debug("Reloading installed categories");
        PluginCategory.installed = new LinkedList<>();
        for (PluginCategory c : PluginCategory.values()) if (c.isInstalled()) PluginCategory.installed.add(c);
        return PluginCategory.installed;
    }

    /** Resets the index of installed and selected categories and resets each categories' state. */
    public static void reset() {
        STDIO.debug("Resetting categories");
        for (PluginCategory c : PluginCategory.values()) c.revert();
        PluginCategory.selected = null;
        PluginCategory.installed = null;
    }

    /**
     * Filters categories by installation and selection state.
     * @param installed Set to {@code true} or {@code false} to filter by installation state.
     * @param selected Set to {@code true} or {@code false} to filter by selection state.
     * @return A list of filtered categories.
     */
    public static @NotNull List<PluginCategory> get(@Nullable Boolean installed, @Nullable Boolean selected) {
        List<PluginCategory> categories = new LinkedList<>();
        for (PluginCategory c : PluginCategory.values()) {
            if ((installed == null || installed == c.isInstalled()) &&
                    (selected == null || selected == c.isSelected())) {
                categories.add(c);
            }
        }
        return categories;
    }

    //= Cosmetics ==

    /** List all categories. */
    public static void requestAll() {
        if (PluginCategory.values().length == 0) {
            STDIO.log(CATEGORY, BOLD, "No categories available");
            return;
        }

        STDIO.list(CATEGORY, PluginCategory.values().length +
                " categor(y/ies) available", (Object[]) PluginCategory.values());
    }

    /** List selected categories. */
    public static void listSelected() {
        if (PluginCategory.selected().isEmpty()) {
            STDIO.log(CATEGORY,  BOLD, "No categories selected");
            return;
        }

        STDIO.list(CATEGORY, PluginCategory.selected().size() +
                " categor(y/ies) selected", PluginCategory.selected().toArray());
    }

    /** List installed categories. */
    public static void requestInstalled() {
        if (PluginCategory.installed().isEmpty()) {
            STDIO.log(CATEGORY, BOLD, "No categories installed");
            return;
        }

        STDIO.list(CATEGORY, PluginCategory.installed().size() +
                " categor(y/ies) installed", PluginCategory.installed().toArray());
    }

    /** Exception that is thrown if a requested category could not be found. */
    public static final class NotFoundException extends Exception {
        /**
         * Creates a new category not found exception.
         * @param name The name that could not be matched.
         */
        public NotFoundException(@NotNull String name) {
            super("Category \"" + name + "\" not found");
        }
    }
}
