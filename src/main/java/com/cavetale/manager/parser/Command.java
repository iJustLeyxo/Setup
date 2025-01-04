package com.cavetale.manager.parser;

import com.cavetale.manager.Manager;
import com.cavetale.manager.data.plugin.*;
import com.cavetale.manager.data.server.Software;
import com.cavetale.manager.data.server.Softwares;
import com.cavetale.manager.parser.container.CategoryContainer;
import com.cavetale.manager.parser.container.PathContainer;
import com.cavetale.manager.parser.container.ServerContainer;
import com.cavetale.manager.util.console.Console;
import com.cavetale.manager.util.console.Style;
import com.cavetale.manager.util.console.Type;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

public enum Command {
    Exit("Exit interactive mode", "E", "Quit", "Q", "Stop") {
        @Override
        public void run(@NotNull Tokens tokens) {
            Manager.exit();
        }
    },

    Help("Show usage help") {
        @Override
        public void run(@NotNull Tokens tokens) {
            Manager.help();
        }
    },

    Install("Install plugins and server software", "Add") {
        @Override
        public void run(@NotNull Tokens tokens) {
            List<Plugin> plugins = Plugins.selected();
            List<Software> software = Softwares.selected();
            if (plugins.isEmpty() && software.isEmpty()) {
                Console.log(Type.REQUESTED, Style.WARN, "Nothing selected\n");
                return;
            }
            Console.log(Type.REQUESTED, Style.INSTALL,
                    plugins.size() + " plugin(s) and " + software.size() + " software to install\n");
            if (!Console.confirm("Continue installation")) return;
            for (Plugin p : plugins) p.install();
            for (Software s : software) s.install();
        }
    },

    Link("Link any jar archive path to the plugins directory") {
        @Override
        public
        void run(@NotNull Tokens tokens) {
            PathContainer patCon = (PathContainer) tokens.flags().get(Flag.path);
            if (patCon == null || patCon.isEmpty() || patCon.isEmpty()) {
                Console.log(Type.REQUESTED, Style.WARN, "No path specified\n");
                return;
            }
            File origin = new File(patCon.get());
            try {
                Plugin.get(origin);
            } catch (Plugin.NotAPluginException e) {
                Console.log(Type.WARN, e.getMessage() + "\n");
            } catch (Plugin.PluginNotFoundException ignored) {} // TODO: Add back unknown plugin warning
            Console.log(Type.REQUESTED, Style.LINK, origin.getName() + " will be linked\n");
            if (!Console.confirm("Continue linking")) return;
            Console.log(Type.INFO, "Linking " + origin.getName());
            File folder = Plugins.FOLDER;
            folder.mkdirs();
            File link = new File(folder, origin.getName());
            if (link.exists()) {
                if (!Console.log(Type.INFO, Style.ERR, " failed (already exists)\n")) {
                    Console.log(Type.ERR, "Linking " + this.name() + " plugin failed (already exists)\n");
                }
                return;
            }
            try {
                Files.createSymbolicLink(link.toPath(), origin.toPath());
                Console.log(Type.INFO, Style.DONE, " done\n");
            } catch (IOException e) {
                if (!Console.log(Type.INFO, Style.ERR, " failed\n")) {
                    Console.log(Type.ERR, "Linking " + origin.getName() + " failed\n");
                }
            }
        }
    },

    List("List plugins, categories, servers and server software", "Show", "Resolve") {
        @Override
        public void run(@NotNull Tokens tokens) {
            if (tokens.flags().containsKey(Flag.installed)) {
                if (tokens.flags().containsKey(Flag.all)) {
                    Plugins.listInstalled();
                    Categories.listInstalled();
                    Servers.listInstalled();
                    Softwares.listInstalled();
                } else {
                    boolean selected = false;
                    if (tokens.flags().containsKey(Flag.plugin)) {
                        Plugins.listInstalled();
                        selected = true;
                    }
                    if (tokens.flags().containsKey(Flag.category)) {
                        Categories.listInstalled();
                        selected = true;
                    }
                    if (tokens.flags().containsKey(Flag.server)) {
                        Servers.listInstalled();
                        selected = true;
                    }
                    if (tokens.flags().containsKey(Flag.software)) {
                        Softwares.listInstalled();
                        selected = true;
                    }
                    if (!selected) Plugins.listInstalled();
                }
            } else {
                if (tokens.flags().containsKey(Flag.all) || (!tokens.flags().containsKey(Flag.plugin) && !tokens.flags().containsKey(Flag.category) && !tokens.flags().containsKey(Flag.server) && !tokens.flags().containsKey(Flag.software))) {
                    Plugins.list();
                    Categories.list();
                    Servers.list();
                    Softwares.list();
                } else if (Plugins.selected().isEmpty() && Categories.selected().isEmpty() && Servers.selected().isEmpty() && Softwares.selected().isEmpty()) {
                    if (tokens.flags().containsKey(Flag.plugin)) Plugins.list();
                    if (tokens.flags().containsKey(Flag.category)) Categories.list();
                    if (tokens.flags().containsKey(Flag.server)) Servers.list();
                    if (tokens.flags().containsKey(Flag.software)) Softwares.list();
                } else {
                    if (tokens.flags().containsKey(Flag.plugin) || tokens.flags().containsKey(Flag.category) || tokens.flags().containsKey(Flag.server)) {
                        Plugins.listSelected();
                        if (tokens.flags().containsKey(Flag.category)) Categories.listSelected();
                        if (tokens.flags().containsKey(Flag.server)) Servers.listSelected();
                    }
                    if (tokens.flags().containsKey(Flag.software)) Softwares.listSelected();
                }
            }
        }
    },

    Status("View installation status", "Info", "Verify", "Check") {
        @Override
        public
        void run(@NotNull Tokens tokens) {
            Plugins.summarize();
            Softwares.summarize();
        }
    },

    Uninstall("Uninstall plugins, server software and files", "Remove", "Delete") {
        @Override
        public void run(@NotNull Tokens tokens) {
            List<Plugin> plugins = Plugins.selected();
            List<Software> software = Softwares.selected();
            if (plugins.isEmpty() && software.isEmpty()) {
                Console.log(Type.REQUESTED, Style.WARN, "Nothing selected\n");
                return;
            }
            Console.log(Type.REQUESTED, Style.UNINSTALL,
                    plugins.size() + " plugin(s) and " + software.size() + " software to uninstall\n");
            if (!Console.confirm("Continue removal")) return;
            for (Plugin p : plugins) p.uninstall();
            for (Software s : software) s.uninstall();
        }
    },

    Update("Update plugins and software", "Upgrade") {
        @Override
        public void run(@NotNull Tokens tokens) {
            List<Plugin> plugins = Plugins.selected();
            List<Software> software = Softwares.selected();
            if (plugins.isEmpty() && software.isEmpty()) { // Auto select installed if nothing specified
                plugins = Plugins.installed();
                software = Softwares.installed();
            }
            if (plugins.isEmpty() && software.isEmpty()) {
                Console.log(Type.REQUESTED, Style.WARN, "Nothing selected\n");
                return;
            }
            Console.log(Type.REQUESTED, Style.UPDATE,
                    plugins.size() + " plugin(s) and " + software.size() + " software to update\n");
            if (!Console.confirm("Continue update")) return;
            for (Plugin p : plugins) p.update();
            for (Software s  : software) s.update();
        }
    };

    // TODO: Accept eula option
    // TODO: Run command
    // TODO: Find command to fins stuff

    public final @NotNull String[] refs;
    public final @NotNull String info;

    Command(@NotNull String info, @NotNull String @NotNull ... refs) {
        this.refs = new String[refs.length + 1];
        this.refs[0] = this.name().toLowerCase();
        System.arraycopy(refs, 0, this.refs, 1, refs.length);
        this.info = info;
    }

    public abstract void run(@NotNull Tokens result);

    public void help(@NotNull Tokens result) {
        Console.log(Type.REQUESTED, Style.HELP, this.refs[0] + ": " + this.info + "\n");
    }

    public static @NotNull Command get(@NotNull String ref) throws NotFoundException {
        for (Command c : Command.values()) for (String r : c.refs) if (r.equalsIgnoreCase(ref)) return c;
        throw new NotFoundException(ref);
    }

    public static class NotFoundException extends InputException {
        public NotFoundException(@NotNull String ref) {
            super("Command \"" + ref + "\" not found");
        }
    }
}
