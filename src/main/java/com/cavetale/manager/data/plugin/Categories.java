package com.cavetale.manager.data.plugin;

import com.cavetale.manager.parser.Flag;
import com.cavetale.manager.parser.Parser;
import com.cavetale.manager.parser.container.CategoryContainer;
import com.cavetale.manager.util.console.Console;
import com.cavetale.manager.util.console.Style;
import com.cavetale.manager.util.console.Type;
import com.cavetale.manager.util.console.XCode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;
import java.util.List;

public final class Categories {
    private static final @NotNull List<Category> selected = new LinkedList<>();
    private static final @NotNull List<Category> installed = new LinkedList<>();

    public static void reloadSelected(@NotNull Parser parser) {
        Console.log(Type.EXTRA, "Reloading selected categories\n");
        for (Category c : Category.values()) c.reset(); // Reset category states
        Categories.selected.clear();

        CategoryContainer categories = (CategoryContainer) Flag.category.container();
        if (Flag.installed.isSelected()) {
            Console.log(Type.DEBUG, "Selecting installed categories\n");
            for (Category c : Categories.installed) c.target();
        } else if (Flag.all.isSelected() || (Flag.category.isSelected() && categories.isEmpty())) { // Select all
            Console.log(Type.DEBUG, "Selecting all categories\n");
            for (Category c : Category.values()) c.target();
        } else {
            Console.log(Type.DEBUG, "Selecting categories " + categories.get() + "\n"); // Select by category
            for (Category c : categories.get()) c.target();

            for (Server s : Server.values()) if (s.isSelected()) for (Category c : s.categories()) c.select(); // Select by server
        }

        for (Category c : Category.values()) if (c.isSelected()) Categories.selected.add(c); // Update selection
    }

    public static void reloadInstallations() {
        Console.log(Type.EXTRA, "Reloading installed categories\n");
        Categories.installed.clear(); // Reset installations

        for (Category c : Category.values()) if (c.isInstalled()) Categories.installed.add(c); // Update installation
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

    public static @NotNull List<Category> installed() {
        return Categories.installed;
    }

    public static @NotNull List<Category> selected() {
        return Categories.selected;
    }

    public static void summarize() {
        if (!Categories.selected.isEmpty()) Categories.summarizeSelected();
        else if (!Categories.installed.isEmpty()) Categories.summarizeInstalled();
        else {
            Console.sep();
            Console.log(Type.REQUESTED, Style.CATEGORY, XCode.BOLD +  "No categories selected or installed\n");
        }
    }

    private static void summarizeSelected() {
        Console.sep();
        List<Category> selected = Categories.selected;
        Console.logL(Type.REQUESTED, Style.SELECT, selected.size() +
                " categor(y/ies) selected", 4, 21, selected.toArray());
        selected = Categories.get(true, true);
        if (!selected.isEmpty()) {
            Console.sep();
            Console.logL(Type.REQUESTED, Style.INSTALL, selected.size() +
                    " categor(y/ies) installed", 4, 21, selected.toArray());
        }
        selected = Categories.get(true, false);
        if (!selected.isEmpty()) {
            Console.sep();
            Console.logL(Type.REQUESTED, Style.SUPERFLUOUS, selected.size() +
                    " categor(y/ies) superfluous", 4, 21, selected.toArray());
        }
        selected = Categories.get(false, true);
        if (!selected.isEmpty()) {
            Console.sep();
            Console.logL(Type.REQUESTED, Style.MISSING, selected.size() +
                    " categor(y/ies) missing", 4, 21, selected.toArray());
        }
    }

    private static void summarizeInstalled() {
        List<Category> installed = Categories.installed;
        installed.remove(null);
        Console.sep();
        Console.logL(Type.REQUESTED, Style.INSTALL, installed.size() +
                " categor(y/ies) installed", 4, 21, installed.toArray());
    }

    public static void listSelected() {
        if (Categories.selected.isEmpty()) {
            Console.sep();
            Console.log(Type.REQUESTED, Style.CATEGORY, XCode.BOLD + "No categories selected\n");
            return;
        }

        Console.sep();
        Console.logL(Type.REQUESTED, Style.CATEGORY, Categories.selected.size() + " categor(y/ies) selected", 4, 21, Categories.selected.toArray());
    }

    public static void listInstalled() {
        if (Categories.installed.isEmpty()) {
            Console.sep();
            Console.log(Type.REQUESTED, Style.CATEGORY, XCode.BOLD + "No categories installed\n");
            return;
        }

        Console.sep();
        Console.logL(Type.REQUESTED, Style.CATEGORY, Categories.installed.size() + " categor(y/ies) installed", 4, 21, Categories.installed.toArray());
    }

    public static void list() {
        Console.sep();
        Console.logL(Type.REQUESTED, Style.CATEGORY, Category.values().length + " categor(y/ies) available", 4, 21, (Object[]) Category.values());
    }
}
