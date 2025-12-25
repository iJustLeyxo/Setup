package com.cavetale.setup.console;

import com.cavetale.setup.data.plugin.Category;
import com.cavetale.setup.data.plugin.Plugin;
import com.cavetale.setup.data.plugin.Server;
import com.cavetale.setup.data.server.Software;
import com.cavetale.setup.util.Util;
import link.l_pf.cmdlib.app.Command;
import link.l_pf.cmdlib.app.Flag;
import link.l_pf.cmdlib.app.Parser;
import link.l_pf.cmdlib.app.container.Contents;
import link.l_pf.cmdlib.app.container.FileListContents;
import link.l_pf.cmdlib.app.container.StringListContents;
import link.l_pf.cmdlib.io.Code;
import link.l_pf.cmdlib.io.Style;
import link.l_pf.cmdlib.io.Type;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static link.l_pf.cmdlib.io.Console.SYSIO;

/** Custom console commands. */
public enum CustomCommand implements Command {
    /** Compares installed and selected software. */
    COMPARE("Compare installed to selected software", "VERIFY", "CHECK"){
        @Override
        public void onRun(@NotNull Parser.Result result) {
            boolean out = false;

            if (Plugin.listSelected()) {
                out = true;

                List<Plugin> selected = Plugin.get(true, true);
                if (!selected.isEmpty()) {
                    SYSIO.separate();
                    SYSIO.list(Type.REQUESTED, CustomStyle.INSTALL, selected.size() +
                            " plugin(s) installed", 4, 21, selected.toArray());
                }
                selected = Plugin.get(true, false);
                if (!selected.isEmpty()) {
                    SYSIO.separate();
                    SYSIO.list(Type.REQUESTED, CustomStyle.SUPERFLUOUS, selected.size() +
                            " plugin(s) superfluous", 4, 21, selected.toArray());
                }
                selected = Plugin.get(false, true);
                if (!selected.isEmpty()) {
                    SYSIO.separate();
                    SYSIO.list(Type.REQUESTED, CustomStyle.MISSING, selected.size() +
                            " plugin(s) missing", 4, 21, selected.toArray());
                }
            }

            if (Software.listSelected()) {
                out = true;

                List<Software> selected = Software.get(true, true);
                if (!selected.isEmpty()) {
                    SYSIO.separate();
                    SYSIO.list(Type.REQUESTED, CustomStyle.INSTALL, selected.size() +
                            " software installed", 4, 21, selected.toArray());
                }
                selected = Software.get(true, false);
                if (!selected.isEmpty()) {
                    SYSIO.separate();
                    SYSIO.list(Type.REQUESTED, CustomStyle.SUPERFLUOUS, selected.size() +
                            " software superfluous", 4, 21, selected.toArray());
                }
                selected = Software.get(false, true);
                if (!selected.isEmpty()) {
                    SYSIO.separate();
                    SYSIO.list(Type.REQUESTED, CustomStyle.MISSING, selected.size() +
                            " software missing", 4, 21, selected.toArray());
                }
            }

            if (!out) {
                SYSIO.warn("Nothing selected for comparison\n");
            }
        }
    },

    /** Links this tool to a server installation. */
    CONNECT("Link this tool to a server installation") {
        @Override
        public @NotNull Contents<?> init() {
            return new FileListContents();
        }

        @Override
        public void onRun(@NotNull Parser.Result result) {
            File installation = null; // Find Setup.jar
            {
                File[] files = new File("").getAbsoluteFile().listFiles();
                if (files != null) {
                    for (File file : files) {
                        if (file.isDirectory()) continue;
                        String name = file.getName();
                        if (name.startsWith("Setup") && name.endsWith(".jar")) {;
                            installation = file;
                            break;
                        }
                    }
                }
            }

            if (installation == null) {
                SYSIO.err("No tool jar in working directory\n");
                return;
            }

            List<File> files = (List<File>) this.contents(); // Parse destinations

            if (files.isEmpty()) {
                SYSIO.warn("No destination(s) specified\n");
                return;
            }

            SYSIO.requested(CustomStyle.LINK.toString() + files.size() + " destination(s) will be linked to\n");
            if (!SYSIO.getConfirmation("Continue linking")) return;

            for (File dest : files) {
                SYSIO.info("Linking to " + dest.getName());
                if (!dest.isDirectory()) {
                    SYSIO.send(Type.INFO, Type.ERR, "Linking to " + dest.getName(), " failed (not a folder)\n");
                    continue;
                }
                dest.mkdirs();
                File file = new File(dest, installation.getName());
                if (file.exists()) {
                    SYSIO.send(Type.INFO, Type.ERR, "Linking to " + dest.getName(), " failed (already exists)\n");
                    return;
                }

                try {
                    Files.createSymbolicLink(file.toPath(), installation.toPath());
                    SYSIO.info(Style.SUCCESS + " done\n");
                } catch (IOException e) {
                    SYSIO.send(Type.INFO, Type.ERR, "Linking to " + dest.getName(), " failed", e);
                }
            }
        }
    },

    /** Accepts the Minecraft EULA. */
    EULA("Agree to the Minecraft EULA") {
        @Override
        public void onRun(@NotNull Parser.Result result) {
            File eula = new File("eula.txt");
            if (!eula.exists()) {
                SYSIO.err(eula.getName() + " does not exist. You may need to run the server first.\n");
                return;
            }

            if (!SYSIO.getConfirmation("Agree to the Minecraft EULA")) return;

            StringBuilder builder = new StringBuilder(); // Read eula
            try (FileInputStream in = new FileInputStream(eula)) {
                StringBuilder lineBuilder = new StringBuilder();
                byte[] bytes = in.readAllBytes();
                in.close();
                for (int i = 0; i < bytes.length; i++) {
                    char c = (char) bytes[i];
                    if (c == '\n' || bytes.length - 1 <= i) {
                        String line = lineBuilder.toString();
                        if (line.equals("eula=false")) line = "eula=true";
                        else if (line.equals("eula=true")) SYSIO.warn("Already accepted the eula\n");
                        builder.append(line);
                        if (c == '\n') builder.append(c);
                        lineBuilder = new StringBuilder();
                    } else lineBuilder.append(c);
                }
            } catch (IOException e) {
                SYSIO.err("Failed to read eula", e);
                return;
            }

            try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(eula)))) { // Write eula
                writer.write(builder.toString());
                writer.flush();
            } catch (IOException e) {
                SYSIO.err("Failed to write eula", e);
            }
        }
    },

    /** Finds anything */
    FIND("Find anything", "Search") {
        private static final double MIN_SIMILARITY = 0.33;

        @Override
        public @NotNull Contents<?> init() {
            return new StringListContents();
        }

        @Override
        public void onRun(@NotNull Parser.Result result) {
            String arg = String.join(" ", (List<String>) this.contents()).toLowerCase();

            boolean all = CustomFlag.ALL.isSelected() || (!Flag.Default.COMMAND.isSelected() && !Flag.Default.FLAG.isSelected() && !CustomFlag.PLUGIN.isSelected() && !CustomFlag.CATEGORY.isSelected() && !CustomFlag.SERVER.isSelected() && !CustomFlag.SOFTWARE.isSelected());
            boolean found = false;

            if (all || Flag.Default.COMMAND.isSelected()) found = commands(arg);
            if (all || Flag.Default.FLAG.isSelected()) found = flags(arg);
            if (all || CustomFlag.PLUGIN.isSelected()) found = plugins(arg) || found;
            if (all || CustomFlag.CATEGORY.isSelected()) found = categories(arg) || found;
            if (all || CustomFlag.SERVER.isSelected()) found = servers(arg) || found;
            if (all || CustomFlag.SOFTWARE.isSelected()) found = software(arg) || found;

            if (!found) SYSIO.warn("Nothing found to match \"" + arg + "\"\n");
        }

        private boolean commands(@NotNull String arg) {
            HashMap<CustomCommand, Double> commands = new HashMap<>();

            for (CustomCommand command : CustomCommand.values()) {
                double similarity = 0;
                for (String ref : command.references()) similarity = Math.max(Util.similarity(arg, ref.toLowerCase()), similarity);
                commands.put(command, similarity);
            }

            List<CustomCommand> result = commands.entrySet().stream().filter(e -> MIN_SIMILARITY <= e.getValue()).map(Map.Entry::getKey).toList();
            if (result.isEmpty()) return false;
            SYSIO.separate();
            SYSIO.list(Type.REQUESTED, CustomStyle.COMMAND, "Commands", 4, 21, result.toArray());
            return true;
        }

        private boolean flags(@NotNull String arg) {
            HashMap<CustomFlag, Double> flags = new HashMap<>();
            for (CustomFlag flag : CustomFlag.values()) flags.put(flag, Util.similarity(arg, flag.reference()));
            List<CustomFlag> result = flags.entrySet().stream().filter(e -> MIN_SIMILARITY <= e.getValue()).map(Map.Entry::getKey).toList();
            if (result.isEmpty()) return false;
            SYSIO.separate();
            SYSIO.list(Type.REQUESTED, CustomStyle.FLAG, "Flags", 4, 21, result.toArray());
            return true;
        }

        private boolean plugins(@NotNull String arg) {
            HashMap<Plugin, Double> plugins = new HashMap<>();
            for (Plugin plugin : Plugin.values()) plugins.put(plugin, Util.similarity(arg, plugin.displayName().toLowerCase()));
            List<Plugin> result = plugins.entrySet().stream().filter(e -> MIN_SIMILARITY <= e.getValue()).map(Map.Entry::getKey).toList();
            if (result.isEmpty()) return false;
            SYSIO.separate();
            SYSIO.list(Type.REQUESTED, CustomStyle.PLUGIN, "Plugins", 4, 21, result.toArray());
            return true;
        }

        private boolean categories(@NotNull String arg) {
            HashMap<Category, Double> categories = new HashMap<>();
            for (Category category : Category.values()) categories.put(category, Util.similarity(arg, category.displayName().toLowerCase()));
            List<Category> result = categories.entrySet().stream().filter(e -> MIN_SIMILARITY <= e.getValue()).map(Map.Entry::getKey).toList();
            if (result.isEmpty()) return false;
            SYSIO.separate();
            SYSIO.list(Type.REQUESTED, CustomStyle.CATEGORY, "Categories", 4, 21, result.toArray());
            return true;
        }

        private boolean servers(@NotNull String arg) {
            HashMap<Server, Double> servers = new HashMap<>();
            for (Server server : Server.values()) servers.put(server, Util.similarity(arg, server.displayName().toLowerCase()));
            List<Server> result = servers.entrySet().stream().filter(e -> MIN_SIMILARITY <= e.getValue()).map(Map.Entry::getKey).toList();
            if (result.isEmpty()) return false;
            SYSIO.separate();
            SYSIO.list(Type.REQUESTED, CustomStyle.SERVER, "Servers", 4, 21, result.toArray());
            return true;
        }

        private boolean software(@NotNull String arg) {
            HashMap<Software, Double> software = new HashMap<>();
            for (Software soft : Software.values()) software.put(soft, Util.similarity(arg, soft.displayName().toLowerCase()));
            List<Software> result = software.entrySet().stream().filter(e -> MIN_SIMILARITY <= e.getValue()).map(Map.Entry::getKey).toList();
            if (result.isEmpty()) return false;
            SYSIO.separate();
            SYSIO.list(Type.REQUESTED, CustomStyle.SOFTWARE, "Software", 4, 21, result.toArray());
            return true;
        }
    },

    /** Installs plugins and server software */
    INSTALL("Install plugins and server software", "ADD") {
        @Override
        public void onRun(@NotNull Parser.Result result) {
            List<Plugin> plugins = Plugin.selected();
            List<Software> software = Software.selected();
            if (plugins.isEmpty() && software.isEmpty()) {
                SYSIO.warn("Nothing selected\n");
                return;
            }
            SYSIO.requested(CustomStyle.INSTALL.toString() + plugins.size() + " plugin(s) and " + software.size() + " software to install\n");
            if (!SYSIO.getConfirmation("Continue installation")) return;
            for (Plugin p : plugins) p.install();
            for (Software s : software) s.install();
        }
    },

    /** Links any jar archive to the plugin directory. */
    LINK("Link any jar archive path to the plugins directory") {
        @Override
        public @NotNull Contents<?> init() {
            return new FileListContents();
        }

        @Override
        public void onRun(@NotNull Parser.Result result) {
            List<File> files = (List<File>) this.contents();

            if (files.isEmpty()) {
                SYSIO.warn("No path(s) specified\n");
                return;
            }

            for (File origin : files) {
                try {
                    Plugin.get(origin);
                } catch (Plugin.NotAPluginException e) {
                    SYSIO.warn(e.getMessage() + "\n");
                } catch (Plugin.NotFoundException e) {
                    SYSIO.warn("Unknown plugin " + origin.getName() + "\n");
                }
            }

            SYSIO.requested(CustomStyle.LINK.toString() + files.size() + " file(s) will be linked\n");
            if (!SYSIO.getConfirmation("Continue linking")) return;

            for (File origin : files) {
                SYSIO.info("Linking " + origin.getName());
                File folder = Plugin.FOLDER;
                folder.mkdirs();
                File link = new File(folder, origin.getName());
                if (link.exists()) {
                    SYSIO.send(Type.INFO, Type.ERR, "Linking " + origin.getName(), " failed (already exists)\n");
                    continue;
                }
                try {
                    Files.createSymbolicLink(link.getAbsoluteFile().toPath(), origin.getAbsoluteFile().toPath());
                    SYSIO.info(Style.SUCCESS + " done\n");
                } catch (IOException e) {
                    SYSIO.send(Type.INFO, Type.ERR, "Linking " + origin.getName(), " failed", e);
                }
            }
        }
    },

    /** Lists plugins, categories, servers and server software. */
    LIST("List plugins, categories, servers and server software", "SHOW", "RESOLVE") {
        @Override
        public void onRun(@NotNull Parser.Result result) {
            if (CustomFlag.INSTALLED.isSelected()) {
                if (CustomFlag.ALL.isSelected()) {
                    Plugin.requestInstalled();
                    Category.requestInstalled();
                    Server.requestInstalled();
                    Software.requestInstalled();
                } else {
                    boolean selected = false;
                    if (CustomFlag.PLUGIN.isSelected()) {
                        Plugin.requestInstalled();
                        selected = true;
                    }
                    if (CustomFlag.CATEGORY.isSelected()) {
                        Category.requestInstalled();
                        selected = true;
                    }
                    if (CustomFlag.SERVER.isSelected()) {
                        Server.requestInstalled();
                        selected = true;
                    }
                    if (CustomFlag.SOFTWARE.isSelected()) {
                        Software.requestInstalled();
                        selected = true;
                    }
                    if (!selected) Plugin.requestInstalled();
                }
            } else {
                if (CustomFlag.ALL.isSelected() || (!CustomFlag.PLUGIN.isSelected() && !CustomFlag.CATEGORY.isSelected() && !CustomFlag.SERVER.isSelected() && !CustomFlag.SOFTWARE.isSelected())) {
                    Plugin.requestAll();
                    Category.requestAll();
                    Server.requestAll();
                    Software.requestAll();
                } else if (Plugin.selected().isEmpty() && Category.selected().isEmpty() && Server.selected().isEmpty() && Software.selected().isEmpty()) {
                    if (CustomFlag.PLUGIN.isSelected()) Plugin.requestAll();
                    if (CustomFlag.CATEGORY.isSelected()) Category.requestAll();
                    if (CustomFlag.SERVER.isSelected()) Server.requestAll();
                    if (CustomFlag.SOFTWARE.isSelected()) Software.requestAll();
                } else {
                    if (CustomFlag.PLUGIN.isSelected() || CustomFlag.CATEGORY.isSelected() || CustomFlag.SERVER.isSelected()) {
                        Plugin.listSelected();
                        if (CustomFlag.CATEGORY.isSelected()) Category.listSelected();
                        if (CustomFlag.SERVER.isSelected()) Server.listSelected();
                    }
                    if (CustomFlag.SOFTWARE.isSelected()) Software.listSelected();
                }
            }
        }
    },

    /** Runs the installed server software. */
    RUN("Run installed server software") {
        @Override
        public void onRun(@NotNull Parser.Result result) {
            List<Software> selected = Software.selected();
            if (!CustomFlag.SOFTWARE.isSelected() || Software.selected().isEmpty()) {
                selected = Arrays.asList(Software.values());
            }
            if (selected.size() > 1) SYSIO.warn("Multiple software selected\n");
            for (Software software : selected) {
                List<String> installations = software.installations();
                if (!installations.isEmpty()) {
                    if (installations.size() > 1) SYSIO.warn(software.displayName() + " has multiple installations\n");
                    String installation = installations.getFirst();
                    SYSIO.info("Running " + installation + "\n\n" + Code.RESET);

                    try {
                        ProcessBuilder builder = new ProcessBuilder("java", "-XX:+UseG1GC", "-Xmx2g", "-jar", installation, "nogui");
                        builder.directory(Software.FOLDER);
                        builder.redirectErrorStream(true);
                        Process process = builder.start();

                        SYSIO.connect(process, "SERVER");

                        int exit = process.waitFor();
                        if (exit == 0) SYSIO.info("\n\n" + installation + " exited with code " + exit + "\n");
                        else SYSIO.warn("\n\n" + installation + " exited with code " + exit + "\n");
                    } catch (IOException e) {
                        SYSIO.err("\n\nFailed to run " + installation, e);
                    } catch (InterruptedException e) {
                        SYSIO.err("\n\n" + installation + " was interrupted", e);
                    }

                    return;
                }
            }
            SYSIO.err("None of the selected software is installed\n");
        }
    },

    /** Prints installation status. */
    STATUS("View installation status", "STATE", "INFO") {
        @Override
        public void onRun(@NotNull Parser.Result result) {
            boolean out = Plugin.listInstalled();
            out = Plugin.listLinked() || out;
            out = Plugin.listUnknown() || out;
            out = Software.listInstalled() || out;
            out = Software.listUnknown() || out;

            if (!out) {
                SYSIO.warn(CustomStyle.SOFTWARE.toString() + Code.BOLD + "Nothing installed to show\n");
            }
        }
    },

    /** Uninstalls plugins, server software and other files. */
    UNINSTALL("Uninstall plugins, server software and files", "Remove", "Delete") {
        @Override
        public void onRun(@NotNull Parser.Result result) {
            List<Plugin> plugins = Plugin.selected();
            List<Software> software = Software.selected();
            if (plugins.isEmpty() && software.isEmpty()) {
                SYSIO.warn("Nothing selected\n");
                return;
            }
            SYSIO.requested(CustomStyle.UNINSTALL.toString() + plugins.size() + " plugin(s) and " + software.size() + " software to uninstall\n");
            if (!SYSIO.getConfirmation("Continue removal")) return;
            for (Plugin p : plugins) p.uninstall();
            for (Software s : software) s.uninstall();
        }
    },

    /** Updates plugins and server software. */
    UPDATE("Update plugins and software", "Upgrade") {
        @Override
        public void onRun(@NotNull Parser.Result result) {
            List<Plugin> plugins = Plugin.selected();
            List<Software> software = Software.selected();
            if (plugins.isEmpty() && software.isEmpty()) { // Auto select installed if nothing specified
                plugins = Plugin.installed();
                software = Software.installed();
            }
            if (plugins.isEmpty() && software.isEmpty()) {
                SYSIO.warn("Nothing selected\n");
                return;
            }
            SYSIO.requested(CustomStyle.UPDATE.toString() + plugins.size() + " plugin(s) and " + software.size() + " software to update\n");
            if (!SYSIO.getConfirmation("Continue update")) return;
            for (Plugin p : plugins) p.update();
            for (Software s  : software) s.update();
        }
    };

    /** Command data container, for each command. */
    private final @NotNull Data data;

    /**
     * Creates a new command.
     * @param info The description of the command.
     * @param refs All other references of the command.
     */
    CustomCommand(@NotNull String info, @NotNull String @NotNull ... refs) {
        this.data = new Data(this, this.name(), info, refs);
    }

    @Override
    public @NotNull Data data() {
        return this.data;
    }
}
