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
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static link.l_pf.cmdlib.shell.Code.Std.BOLD;
import static link.l_pf.cmdlib.shell.Shell.STDIO;

/** Custom console commands. */
public enum CustomCommand implements Command {
    /** Compares installed and selected software. */
    COMPARE("Compare software", "-P | -C | -S | -Z", "VERIFY", "CHECK"){
        @Override
        public void onRun(@NotNull Parser.Result result) {
            boolean out = false;

            if (Plugin.listSelected()) {
                out = true;

                List<Plugin> selected = Plugin.get(true, true);
                if (!selected.isEmpty()) {
                    STDIO.list(CustomStyle.INSTALL, selected.size() +
                            " plugin(s) installed", selected.toArray());
                }
                selected = Plugin.get(true, false);
                if (!selected.isEmpty()) {
                    STDIO.list(CustomStyle.SUPERFLUOUS, selected.size() +
                            " plugin(s) superfluous", selected.toArray());
                }
                selected = Plugin.get(false, true);
                if (!selected.isEmpty()) {
                    STDIO.list(CustomStyle.MISSING, selected.size() +
                            " plugin(s) missing", selected.toArray());
                }
            }

            if (Software.listSelected()) {
                out = true;

                List<Software> selected = Software.get(true, true);
                if (!selected.isEmpty()) {
                    STDIO.list(CustomStyle.INSTALL, selected.size() +
                            " software installed", selected.toArray());
                }
                selected = Software.get(true, false);
                if (!selected.isEmpty()) {
                    STDIO.list(CustomStyle.SUPERFLUOUS, selected.size() +
                            " software superfluous", selected.toArray());
                }
                selected = Software.get(false, true);
                if (!selected.isEmpty()) {
                    STDIO.list(CustomStyle.MISSING, selected.size() +
                            " software missing", selected.toArray());
                }
            }

            if (!out) {
                STDIO.warn("Nothing selected for comparison");
            }
        }
    },

    /** Accepts the Minecraft EULA. */
    EULA("Agree to the EULA", "") {
        @Override
        public void onRun(@NotNull Parser.Result result) {
            File eula = new File("eula.txt");
            if (!eula.exists()) {
                STDIO.err(eula.getName(), " does not exist. You may need to run the server first.");
                return;
            }

            if (!STDIO.getConfirmation("Agree to the Minecraft EULA")) return;

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
                        else if (line.equals("eula=true")) STDIO.warn("Already accepted the eula");
                        builder.append(line);
                        if (c == '\n') builder.append(c);
                        lineBuilder = new StringBuilder();
                    } else lineBuilder.append(c);
                }
            } catch (IOException e) {
                STDIO.err(e, "Failed to read eula");
                return;
            }

            try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(eula)))) { // Write eula
                writer.write(builder.toString());
                writer.flush();
            } catch (IOException e) {
                STDIO.err(e, "Failed to write eula");
            }
        }
    },

    /** Finds anything */
    FIND("Find anything", "-c | -f | -P | -C | -S | -Z", "Search") {
        private static final double MIN_SIMILARITY = 0.33;

        @Override
        public @NotNull Contents<?> init() {
            return new StringListContents();
        }

        @Override
        public void onRun(@NotNull Parser.Result result) {
            String arg = String.join(" ", (List<String>) this.contents()).toLowerCase();

            boolean all = CustomFlag.ALL.isSelected() || (!Flag.Std.COMMAND.isSelected() && !Flag.Std.FLAG.isSelected() && !CustomFlag.PLUGIN.isSelected() && !CustomFlag.CATEGORY.isSelected() && !CustomFlag.SERVER.isSelected() && !CustomFlag.SOFTWARE.isSelected());
            boolean found = false;

            if (all || Flag.Std.COMMAND.isSelected()) found = commands(arg);
            if (all || Flag.Std.FLAG.isSelected()) found = flags(arg);
            if (all || CustomFlag.PLUGIN.isSelected()) found = plugins(arg) || found;
            if (all || CustomFlag.CATEGORY.isSelected()) found = categories(arg) || found;
            if (all || CustomFlag.SERVER.isSelected()) found = servers(arg) || found;
            if (all || CustomFlag.SOFTWARE.isSelected()) found = software(arg) || found;

            if (!found) STDIO.warn("Nothing found to match \"", arg, "\"\n");
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
            STDIO.list(CustomStyle.COMMAND, "Commands", result.toArray());
            return true;
        }

        private boolean flags(@NotNull String arg) {
            HashMap<CustomFlag, Double> flags = new HashMap<>();
            for (CustomFlag flag : CustomFlag.values()) flags.put(flag, Util.similarity(arg, flag.reference()));
            List<CustomFlag> result = flags.entrySet().stream().filter(e -> MIN_SIMILARITY <= e.getValue()).map(Map.Entry::getKey).toList();
            if (result.isEmpty()) return false;
            STDIO.list(CustomStyle.FLAG, "Flags", result.toArray());
            return true;
        }

        private boolean plugins(@NotNull String arg) {
            HashMap<Plugin, Double> plugins = new HashMap<>();
            for (Plugin plugin : Plugin.values()) plugins.put(plugin, Util.similarity(arg, plugin.displayName().toLowerCase()));
            List<Plugin> result = plugins.entrySet().stream().filter(e -> MIN_SIMILARITY <= e.getValue()).map(Map.Entry::getKey).toList();
            if (result.isEmpty()) return false;
            STDIO.list(CustomStyle.PLUGIN, "Plugins", result.toArray());
            return true;
        }

        private boolean categories(@NotNull String arg) {
            HashMap<Category, Double> categories = new HashMap<>();
            for (Category category : Category.values()) categories.put(category, Util.similarity(arg, category.displayName().toLowerCase()));
            List<Category> result = categories.entrySet().stream().filter(e -> MIN_SIMILARITY <= e.getValue()).map(Map.Entry::getKey).toList();
            if (result.isEmpty()) return false;
            STDIO.list(CustomStyle.CATEGORY, "Categories", result.toArray());
            return true;
        }

        private boolean servers(@NotNull String arg) {
            HashMap<Server, Double> servers = new HashMap<>();
            for (Server server : Server.values()) servers.put(server, Util.similarity(arg, server.displayName().toLowerCase()));
            List<Server> result = servers.entrySet().stream().filter(e -> MIN_SIMILARITY <= e.getValue()).map(Map.Entry::getKey).toList();
            if (result.isEmpty()) return false;
            STDIO.list(CustomStyle.SERVER, "Servers", result.toArray());
            return true;
        }

        private boolean software(@NotNull String arg) {
            HashMap<Software, Double> software = new HashMap<>();
            for (Software soft : Software.values()) software.put(soft, Util.similarity(arg, soft.displayName().toLowerCase()));
            List<Software> result = software.entrySet().stream().filter(e -> MIN_SIMILARITY <= e.getValue()).map(Map.Entry::getKey).toList();
            if (result.isEmpty()) return false;
            STDIO.list(CustomStyle.SOFTWARE, "Software", result.toArray());
            return true;
        }
    },

    /** Installs plugins and server software */
    INSTALL("Install software", "-P | -C | -S | -Z", "ADD") {
        @Override
        public void onRun(@NotNull Parser.Result result) {
            List<Plugin> plugins = Plugin.selected();
            List<Software> software = Software.selected();
            if (plugins.isEmpty() && software.isEmpty()) {
                STDIO.warn("Nothing selected");
                return;
            }

            STDIO.log(CustomStyle.INSTALL, plugins.size(), " plugin(s) and ", software.size(), " software to install");
            if (!STDIO.getConfirmation("Continue installation")) return;
            for (Plugin p : plugins) p.install();
            for (Software s : software) s.install();
        }
    },

    /** Links any jar archive to the plugin directory. */
    LINK("Link jar as plugin", "[path]:files") {
        @Override
        public @NotNull Contents<?> init() {
            return new FileListContents();
        }

        @Override
        public void onRun(@NotNull Parser.Result result) {
            List<File> files = (List<File>) this.contents();

            if (files.isEmpty()) {
                STDIO.warn("No path(s) specified");
                return;
            }

            for (File origin : files) {
                try {
                    Plugin.get(origin);
                } catch (Plugin.NotAPluginException e) {
                    STDIO.warn(e.getMessage());
                } catch (Plugin.NotFoundException e) {
                    STDIO.warn("Unknown plugin ", origin.getName());
                }
            }

            STDIO.log(CustomStyle.LINK, files.size(), " file(s) will be linked");
            if (!STDIO.getConfirmation("Continue linking")) return;

            for (File origin : files) {
                STDIO.openInfo("Linking ", origin.getName());
                File folder = Plugin.FOLDER;
                folder.mkdirs();
                File link = new File(folder, origin.getName());
                if (link.exists()) {
                    STDIO.closeErr("failed (already exists)");
                    continue;
                }
                try {
                    Files.createSymbolicLink(link.getAbsoluteFile().toPath(), origin.getAbsoluteFile().toPath());
                    STDIO.closeInfoDone();
                } catch (IOException e) {
                    STDIO.closeErr(e, "failed");
                }
            }
        }
    },

    /** Lists plugins, categories, servers and server software. */
    LIST("List software", "-P | -C | -S | -Z", "SHOW", "RESOLVE") {
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
    RUN("Run server", ":only | -Z") {
        @Override
        public void onRun(@NotNull Parser.Result result) {
            List<Software> selected = Software.selected();
            Software software;

            if (selected.isEmpty()) {
                List<Software> installed = Software.installed();
                if (installed.isEmpty()) {
                    STDIO.err("No software installed");
                    return;
                }

                if (installed.size() > 1) {
                    STDIO.err("Specify the software to run");
                    return;
                }

                software = installed.getFirst();
            } else {
                if (selected.size() > 1) {
                    STDIO.err("Multiple software selected");
                    return;
                }

                software = selected.getFirst();
            }

            List<String> installations = software.installations();

            if (installations.isEmpty()) {
                STDIO.err("Selected is not installed");
                return;
            }

            if (installations.size() > 1) {
                STDIO.err(software.displayName(), " has multiple installations");
                return;
            }

            String installation = installations.getFirst();

            try {
                ProcessBuilder builder = new ProcessBuilder("java", "-XX:+UseG1GC", "-Xmx2g", "-jar", installation, "nogui");
                builder.directory(Software.FOLDER);
                Process process = builder.start();
                STDIO.link(process, software.displayName());
            } catch (IOException e) {
                STDIO.err(e, "Failed to run ", installation);
            }
        }
    },

    /** Prints installation status. */
    STATUS("Installation status", "", "STATE", "INFO") {
        @Override
        public void onRun(@NotNull Parser.Result result) {
            boolean out = Plugin.listInstalled();
            out = Plugin.listLinked() || out;
            out = Plugin.listUnknown() || out;
            out = Software.listInstalled() || out;
            out = Software.listUnknown() || out;

            if (!out) {
                STDIO.warn(CustomStyle.SOFTWARE.toString(), BOLD, "Nothing installed to show");
            }
        }
    },

    /** Uninstalls plugins, server software and other files. */
    UNINSTALL("Uninstall software", "-P | -C | -S | -Z", "Remove", "Delete") {
        @Override
        public void onRun(@NotNull Parser.Result result) {
            List<Plugin> plugins = Plugin.selected();
            List<Software> software = Software.selected();

            if (plugins.isEmpty() && software.isEmpty()) {
                STDIO.warn("Nothing selected");
                return;
            }

            STDIO.log(CustomStyle.UNINSTALL, plugins.size(), " plugin(s) and ", software.size(), " software to uninstall");
            if (!STDIO.getConfirmation("Continue removal")) return;
            for (Plugin p : plugins) p.uninstall();
            for (Software s : software) s.uninstall();
        }
    },

    /** Updates plugins and server software. */
    UPDATE("Update software", "-P | -C | -S | -Z", "Upgrade") {
        @Override
        public void onRun(@NotNull Parser.Result result) {
            List<Plugin> plugins = Plugin.selected();
            List<Software> software = Software.selected();
            if (plugins.isEmpty() && software.isEmpty()) { // Auto select installed if nothing specified
                plugins = Plugin.installed();
                software = Software.installed();
            }

            if (plugins.isEmpty() && software.isEmpty()) {
                STDIO.warn("Nothing selected");
                return;
            }

            STDIO.log(CustomStyle.UPDATE, plugins.size(), " plugin(s) and ", software.size(), " software to update");
            if (!STDIO.getConfirmation("Continue update")) return;
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
    CustomCommand(@NotNull String info, @NotNull String usage, @NotNull String @NotNull ... refs) {
        this.data = new Data(this, this.name(), info, usage, refs);
    }

    @Override
    public @NotNull Data data() {
        return this.data;
    }
}
