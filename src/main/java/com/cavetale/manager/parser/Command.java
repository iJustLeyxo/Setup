package com.cavetale.manager.parser;

import com.cavetale.manager.Manager;
import com.cavetale.manager.data.plugin.Category;
import com.cavetale.manager.data.plugin.Plugin;
import com.cavetale.manager.data.plugin.Server;
import com.cavetale.manager.data.server.Software;
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
import java.util.Set;

public enum Command {
    EXIT("Exit interactive mode", "quit", "q", "stop") {
        @Override
        public void run(@NotNull Result result) {
            Manager.exit();
        }
    },

    HELP("Show usage help") {
        @Override
        public void run(@NotNull Result result) {
            Manager.help();
        }
    },

    INSTALL("Install plugins and server software", "add") {
        @Override
        public void run(@NotNull Result result) {
            Set<Plugin> plugins = result.plugIndexer().getSelected();
            Set<Software> software = result.softwareIndexer().getSelected();
            if (plugins.isEmpty() && software.isEmpty()) {
                Console.log(Type.REQUESTED, Style.WARN, "Nothing selected\n");
                return;
            }
            Console.log(Type.REQUESTED, Style.INSTALL,
                    plugins.size() + " plugins and " + software.size() + " software to install\n");
            if (!Console.confirm("Continue installation")) return;
            File folder = new File("plugins/");
            folder.mkdir();
            for (Plugin p : plugins) {
                p.install();
            }
            for (Software s : software) {
                s.install();
            }
        }
    },

    LINK("Link any .jar to the plugins directory") {
        @Override
        public
        void run(@NotNull Result result) {
            PathContainer patCon = (PathContainer) result.tokens().flags().get(Flag.PATH);
            if (patCon == null || patCon.isEmpty() || patCon.get().isEmpty()) {
                Console.log(Type.REQUESTED, Style.WARN, "No path specified\n");
                return;
            }
            File origin = new File(patCon.get());
            try {
                Plugin.get(origin);
            } catch (Plugin.NotAPluginException e) {
                Console.log(Type.REQUESTED, Style.WARN, e.getMessage());
            } catch (Plugin.PluginNotFoundException ignored) {}
            Console.log(Type.REQUESTED, Style.LINK, origin.getName() + " will be linked\n");
            if (!Console.confirm("Continue linking")) return;
            Console.log(Type.INFO, "Linking " + origin.getName());
            File folder = new File("plugins/");
            folder.mkdir();
            File link = new File(folder, origin.getName());
            if (link.exists()) {
                if (!Console.log(Type.INFO, Style.ERR, " failed (already exists)\n"))
                    Console.log(Type.ERR, "Linking " + this.name() + " plugin failed (already exists)\n");
                return;
            }
            try {
                Files.createSymbolicLink(link.toPath(), origin.toPath());
                Console.log(Type.INFO, Style.DONE, " done\n");
            } catch (IOException e) {
                if (!Console.log(Type.INFO, Style.ERR, " failed\n"))
                    Console.log(Type.ERR, "Linking " + origin.getName() + " failed\n");
            }
        }
    },

    LIST("List plugins, categories, servers and server software", "show", "resolve") {
        @Override
        public void run(@NotNull Result result) {
            if (!result.plugIndexer().getSelected().isEmpty()) result.plugIndexer().listSelected();
            boolean all = result.tokens().flags().containsKey(Flag.ALL);
            CategoryContainer catCon = (CategoryContainer) result.tokens().flags().get(Flag.CATEGORY);
            if (all || catCon != null && catCon.isEmpty()) Category.list();
            ServerContainer serCon = (ServerContainer) result.tokens().flags().get(Flag.SERVER);
            if (all || serCon != null && serCon.isEmpty()) Server.list();
            if (!result.softwareIndexer().getSelected().isEmpty()) result.softwareIndexer().listSelected();
        }
    },

    STATUS("View installation status", "info", "verify", "check") {
        @Override
        public
        void run(@NotNull Result result) {
            result.plugIndexer().summarize();
            result.softwareIndexer().summarize();
        }
    },

    UNINSTALL("Uninstall plugins, server software and files", "remove", "delete") {
        @Override
        public void run(@NotNull Result result) {
            Set<Plugin> plugins = result.plugIndexer().getSelected();
            plugins.remove(null);
            Set<Software> software = result.softwareIndexer().getSelected();
            if (plugins.isEmpty() && software.isEmpty()) {
                Console.log(Type.REQUESTED, Style.WARN, "Nothing selected\n");
                return;
            }
            software.remove(null);
            Console.log(Type.REQUESTED, Style.UNINSTALL,
                    plugins.size() + " plugins and " + software.size() + " software to uninstall\n");
            if (!Console.confirm("Continue removal")) return;
            for (Plugin p : plugins) {
                p.uninstall();
            }
            for (Software s : software) {
                s.uninstall();
            }
        }
    },

    UPDATE("Update plugins and software", "upgrade") {
        @Override
        public void run(@NotNull Result result) {
            Set<Plugin> plugins = result.plugIndexer().getSelected();
            Set<Software> software = result.softwareIndexer().getSelected();
            if (plugins.isEmpty() && software.isEmpty()) {
                Console.log(Type.REQUESTED, Style.WARN, "Nothing selected\n");
                return;
            }
            Console.log(Type.REQUESTED, Style.UPDATE,
                    plugins.size() + " plugins and " + software.size() + " software to update\n");
            if (!Console.confirm("Continue update")) return;
            for (Plugin p : plugins) {
                p.update();
            }
            for (Software s  : software) {
                s.update();
            }
        }
    };

    public final @NotNull String[] refs;
    public final @NotNull String info;

    Command(@NotNull String info, @NotNull String... refs) {
        this.refs = new String[refs.length + 1];
        this.refs[0] = this.name().toLowerCase();
        System.arraycopy(refs, 0, this.refs, 1, refs.length);
        this.info = info;
    }

    public abstract void run(@NotNull Result result);

    public void help(@NotNull Result result) {
        Console.log(Type.REQUESTED, Style.HELP, this.refs[0] + ": " + this.info);
    }

    public static @NotNull Command get(@NotNull String ref) throws NotFoundException {
        for (Command c : Command.values()) {
            for (String r : c.refs) {
                if (r.equalsIgnoreCase(ref)) return c;
            }
        }
        throw new NotFoundException(ref);
    }

    public static class NotFoundException extends InputException {
        public NotFoundException(@NotNull String ref) {
            super("Command \"" + ref + "\" not found");
        }
    }
}
