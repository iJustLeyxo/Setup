package com.cavetale.manager.data.server;

import com.cavetale.manager.parser.Flag;
import com.cavetale.manager.parser.Tokens;
import com.cavetale.manager.parser.container.SoftwareContainer;
import com.cavetale.manager.util.console.Console;
import com.cavetale.manager.util.console.Style;
import com.cavetale.manager.util.console.Type;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Server software manager, used to analyse installed and selected plugins
 */
public final class SoftwareIndexer {
    public static SoftwareIndexer active;

    private final @NotNull Map<Software, Index> index = new HashMap<>();
    final @NotNull Map<Software, Set<File>> installed;
    private final @NotNull Set<Software> selected;

    public SoftwareIndexer(@NotNull Tokens tokens) {
        this.selected = this.gatherSelected(tokens);
        this.installed = this.gatherInstalls();
        for (Map.Entry<Software, Set<File>> e : this.installed.entrySet()) {
            this.index.put(e.getKey(), new Index(this.selected.contains(e.getKey()), e.getValue()));
        }
        SoftwareIndexer.active = this;
    }

    /**
     * Resolve installed and selected software
     */
    private Set<Software> gatherSelected(@NotNull Tokens tokens) {
        SoftwareContainer softCon = (SoftwareContainer) tokens.flags().get(Flag.SOFTWARE);
        if (tokens.flags().containsKey(Flag.ALL)) return Set.of(Software.values());
        if (softCon != null) {
            if (softCon.isEmpty()) return Set.of(Software.values());
            return Set.of(softCon.get().toArray(new Software[0]));
        }
        return new HashSet<>();
    }

    private Map<Software, Set<File>> gatherInstalls() {
        Map<Software, Set<File>> installs = new HashMap<>();
        File folder = new File(".");
        File[] files = folder.listFiles();
        if (files == null) return installs;
        for (File f : files) {
            if (f.getName().startsWith("Setup")) continue;
            Software s = null;
            try {
                s = Software.get(f);
            } catch (Software.NotASoftwareException e) {
                continue;
            } catch (Software.SoftwareNotFoundException ignored) {}
            File i = new File(f.getName());
            if (!installs.containsKey(s)) {
                installs.put(s, new HashSet<>(Set.of(i)));
            } else {
                installs.get(s).add(i);
            }
        }
        return installs;
    }

    public Set<Software> getAll(@Nullable Boolean installed, @Nullable Boolean selected) {
        Set<Software> software = new HashSet<>();
        for (Map.Entry<Software, Index> e : this.index.entrySet()) {
            Index i = e.getValue();
            if ((installed == null || installed == !i.installs.isEmpty()) &&
                    (selected == null || selected == i.isSelected)) software.add(e.getKey());
        }
        return software;
    }

    public @NotNull Map<Software, Set<File>> getInstalled() {
        return this.installed;
    }

    public @NotNull Set<Software> getSelected() {
        return this.selected;
    }

    public @NotNull Set<File> getUnknown() {
        Index i = this.index.get(null);
        if (i == null) return new HashSet<>();
        return new HashSet<>(this.index.get(null).installs);
    }

    public void summarize() {
        if (!this.selected.isEmpty()) {
            this.summarizeSelected(); // Compare selected to installed software
        } else if (!this.getInstalled().isEmpty()) {
            this.summarizeInstalled(); // Show installed software if nothing is selected
        } else {
            Console.sep();
            Console.log(Type.REQUESTED, Style.INFO, "No software selected or installed\n");
        }
    }

    private void summarizeSelected() {
        Console.sep();
        Set<Software> selected = this.getSelected();
        Console.logL(Type.REQUESTED, Style.SELECT, selected.size() +
                " software(s) selected", 4, 21, selected.toArray());
        selected = this.getAll(true, true);
        if (!selected.isEmpty()) {
            Console.sep();
            Console.logL(Type.REQUESTED, Style.INSTALL, selected.size() +
                    " software(s) installed", 4, 21, selected.toArray());
        }
        selected = this.getAll(true, false);
        if (!selected.isEmpty()) {
            Console.sep();
            Console.logL(Type.REQUESTED, Style.SUPERFLUOUS, selected.size() +
                    " software(s) superfluous", 4, 21, selected.toArray());
        }
        selected = this.getAll(false, true);
        if (!selected.isEmpty()) {
            Console.sep();
            Console.logL(Type.REQUESTED, Style.MISSING, selected.size() +
                    " software(s) missing", 4, 21, selected.toArray());
        }
    }

    private void summarizeInstalled() {
        Console.sep();
        Set<Software> installed = this.getInstalled().keySet();
        installed.remove(null);
        Console.logL(Type.REQUESTED, Style.SOFTWARE, installed.size() +
                " software(s) installed", 4, 21, installed.toArray());
        Set<File> unknown = this.getUnknown(); // Always show unknown software
        if (!unknown.isEmpty()) {
            Console.sep();
            Console.logL(Type.REQUESTED, Style.UNKNOWN, unknown.size() +
                    " software(s) unknown", 4, 21, unknown.toArray());
        }
    }

    public void listSelected() {
        Console.sep();
        Console.logL(Type.REQUESTED, Style.SOFTWARE, "Server software", 4, 21, this.selected.toArray());
    }

    private record Index(
            @Nullable Boolean isSelected,
            @NotNull Set<File> installs
    ) { }
}
