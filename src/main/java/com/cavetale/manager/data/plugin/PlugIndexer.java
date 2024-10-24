package com.cavetale.manager.data.plugin;

import com.cavetale.manager.parser.Flag;
import com.cavetale.manager.parser.Tokens;
import com.cavetale.manager.parser.container.CategoryContainer;
import com.cavetale.manager.parser.container.PluginContainer;
import com.cavetale.manager.parser.container.ServerContainer;
import com.cavetale.manager.util.console.Console;
import com.cavetale.manager.util.console.Style;
import com.cavetale.manager.util.console.Type;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.*;

/**
 * Plugin manager, used to analyse installed and selected plugins
 */
public final class PlugIndexer {
    static PlugIndexer active;

    private final @NotNull Map<Plugin, Index> index = new HashMap<>();
    final @NotNull Map<Plugin, Set<File>> installed;
    private final @NotNull Set<Plugin> selected;
    
    public PlugIndexer(@NotNull Tokens tokens) {
        Set<Plugin> plugins = new HashSet<>(List.of(Plugin.values()));
        plugins.add(null);
        this.selected = this.gatherSelected(tokens);
        this.installed = this.gatherInstalled();
        for (Plugin p : plugins) {
            Set<File> installs = this.installed.get(p);
            if (installs == null) installs = new HashSet<>();
            boolean selected = false;
            if (p != null) selected = this.selected.contains(p);
            this.index.put(p, new Index(selected, installs));
        }
        PlugIndexer.active = this;
    }

    private @NotNull Set<Plugin> gatherSelected(@NotNull Tokens tokens) {
        Set<Plugin> selected = new HashSet<>();
        PluginContainer pluCon = (PluginContainer) tokens.flags().get(Flag.PLUGIN);
        if (tokens.flags().containsKey(Flag.ALL)) return Set.of(Plugin.values());
        if (pluCon != null) {
            if (pluCon.isEmpty()) return Set.of(Plugin.values());
            selected.addAll(pluCon.get());
        }
        CategoryContainer catCon = (CategoryContainer) tokens.flags().get(Flag.CATEGORY);
        if (catCon != null) {
            Set<Category> cats = catCon.isEmpty() ? Set.of(Category.values()) : catCon.get();
            for (Category cat : cats) {
                selected.addAll(cat.plugins());
            }
        }
        ServerContainer serCon = (ServerContainer) tokens.flags().get(Flag.SERVER);
        if (serCon != null) {
            Set<Server> servers = serCon.isEmpty() ? Set.of(Server.values()) : serCon.get();
            for (Server ser : servers) {
                selected.addAll(ser.plugins());
            }
        }
        return selected;
    }

    private @NotNull Map<Plugin, Set<File>> gatherInstalled() {
        Map<Plugin, Set<File>> installs = new HashMap<>();
        File folder = new File("plugins/");
        File[] files = folder.listFiles();
        if (files == null) return installs;
        for (File f : files) {
            Plugin p = null;
            try {
                p = Plugin.get(f);
            } catch (Plugin.NotAPluginException e) {
                continue;
            } catch (Plugin.PluginNotFoundException ignored) {}
            File i = new File(f.getName());
            if (!installs.containsKey(p)) {
                installs.put(p, new HashSet<>(Set.of(i)));
            } else {
                installs.get(p).add(i);
            }
        }
        return installs;
    }

    public @NotNull Set<Plugin> getAll(@Nullable Boolean installed, @Nullable Boolean selected) {
        Set<Plugin> plugins = new HashSet<>();
        for (Map.Entry<Plugin, Index> e : this.index.entrySet()) {
            Index i = e.getValue();
            if ((installed == null || installed == !i.installs.isEmpty()) &&
                    (selected == null || selected == i.isSelected)) plugins.add(e.getKey());
        }
        return plugins;
    }

    public @NotNull Map<Plugin, Set<File>> getInstalled() {
        return this.installed;
    }

    public @NotNull Set<Plugin> getSelected() {
        return this.selected;
    }

    public @NotNull Set<File> getUnknown() {
        Index i = this.index.get(null);
        if (i == null) return new HashSet<>();
        return new HashSet<>(this.index.get(null).installs);
    }

    public void summarize() {
        if (!this.getSelected().isEmpty()) {
            this.summarizeSelected();
        } else if (!this.getInstalled().isEmpty()) {
            this.summarizeInstalled();
        } else {
            Console.sep();
            Console.log(Type.REQUESTED, Style.INFO, "No plugins selected or installed\n");
        }
    }

    private void summarizeSelected() {
        Console.sep();
        Set<Plugin> selected = this.getSelected();
        Console.logL(Type.REQUESTED, Style.SELECT, selected.size() +
                " plugins(s) selected", 4, 21, selected.toArray());
        selected = this.getAll(true, true);
        if (!selected.isEmpty()) {
            Console.sep();
            Console.logL(Type.REQUESTED, Style.INSTALL, selected.size() +
                    " plugins(s) installed", 4, 21, selected.toArray());
        }
        selected = this.getAll(true, false);
        if (!selected.isEmpty()) {
            Console.sep();
            Console.logL(Type.REQUESTED, Style.SUPERFLUOUS, selected.size() +
                    " plugins(s) superfluous", 4, 21, selected.toArray());
        }
        selected = this.getAll(false, true);
        if (!selected.isEmpty()) {
            Console.sep();
            Console.logL(Type.REQUESTED, Style.MISSING, selected.size() +
                    " plugins(s) missing", 4, 21, selected.toArray());
        }
    }

    private void summarizeInstalled() {
        Set<Plugin> installed = this.getInstalled().keySet();
        installed.remove(null);
        Console.sep();
        Console.logL(Type.REQUESTED, Style.INSTALL, installed.size() +
                " plugins(s) installed", 4, 21, installed.toArray());
        Set<File> unknown = this.getUnknown();
        if (!unknown.isEmpty()) {
            Console.sep();
            Console.logL(Type.REQUESTED, Style.UNKNOWN, unknown.size() +
                    " plugins(s) unknown", 4, 21, unknown.toArray());
        }
    }

    public void listSelected() {
        Console.sep();
        Console.logL(Type.REQUESTED, Style.PLUGIN, "Plugins", 4, 21, this.selected.toArray());
    }

    private record Index(
            @Nullable Boolean isSelected,
            @NotNull Set<File> installs
    ) { }
}