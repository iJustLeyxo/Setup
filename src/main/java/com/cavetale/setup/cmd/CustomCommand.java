package com.cavetale.setup.cmd;

import com.cavetale.setup.data.PluginCategory;
import com.cavetale.setup.data.Plugin;
import com.cavetale.setup.data.PluginServer;
import com.cavetale.setup.data.ServerSoftware;
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

/** Custom commands. */
public enum CustomCommand implements Command {
    COMPARE("Compare selected", "-PCSZIA", "VERIFY", "CHECK"){
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

            if (ServerSoftware.listSelected()) {
                out = true;

                List<ServerSoftware> selected = ServerSoftware.get(true, true);
                if (!selected.isEmpty()) {
                    STDIO.list(CustomStyle.INSTALL, selected.size() +
                            " software installed", selected.toArray());
                }
                selected = ServerSoftware.get(true, false);
                if (!selected.isEmpty()) {
                    STDIO.list(CustomStyle.SUPERFLUOUS, selected.size() +
                            " software superfluous", selected.toArray());
                }
                selected = ServerSoftware.get(false, true);
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

    FIND("Find anything", "[string]", "Search") {
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
                for (String ref : command.references()) similarity = Math.max(similarity(arg, ref.toLowerCase()), similarity);
                commands.put(command, similarity);
            }

            List<CustomCommand> result = commands.entrySet().stream().filter(e -> MIN_SIMILARITY <= e.getValue()).map(Map.Entry::getKey).toList();
            if (result.isEmpty()) return false;
            STDIO.list(CustomStyle.COMMAND, "Commands", result.toArray());
            return true;
        }

        private boolean flags(@NotNull String arg) {
            HashMap<CustomFlag, Double> flags = new HashMap<>();
            for (CustomFlag flag : CustomFlag.values()) flags.put(flag, similarity(arg, flag.reference()));
            List<CustomFlag> result = flags.entrySet().stream().filter(e -> MIN_SIMILARITY <= e.getValue()).map(Map.Entry::getKey).toList();
            if (result.isEmpty()) return false;
            STDIO.list(CustomStyle.FLAG, "Flags", result.toArray());
            return true;
        }

        private boolean plugins(@NotNull String arg) {
            HashMap<Plugin, Double> plugins = new HashMap<>();
            for (Plugin plugin : Plugin.values()) plugins.put(plugin, similarity(arg, plugin.displayName().toLowerCase()));
            List<Plugin> result = plugins.entrySet().stream().filter(e -> MIN_SIMILARITY <= e.getValue()).map(Map.Entry::getKey).toList();
            if (result.isEmpty()) return false;
            STDIO.list(CustomStyle.PLUGIN, "Plugins", result.toArray());
            return true;
        }

        private boolean categories(@NotNull String arg) {
            HashMap<PluginCategory, Double> categories = new HashMap<>();
            for (PluginCategory category : PluginCategory.values()) categories.put(category, similarity(arg, category.displayName().toLowerCase()));
            List<PluginCategory> result = categories.entrySet().stream().filter(e -> MIN_SIMILARITY <= e.getValue()).map(Map.Entry::getKey).toList();
            if (result.isEmpty()) return false;
            STDIO.list(CustomStyle.CATEGORY, "Categories", result.toArray());
            return true;
        }

        private boolean servers(@NotNull String arg) {
            HashMap<PluginServer, Double> servers = new HashMap<>();
            for (PluginServer server : PluginServer.values()) servers.put(server, similarity(arg, server.displayName().toLowerCase()));
            List<PluginServer> result = servers.entrySet().stream().filter(e -> MIN_SIMILARITY <= e.getValue()).map(Map.Entry::getKey).toList();
            if (result.isEmpty()) return false;
            STDIO.list(CustomStyle.SERVER, "Servers", result.toArray());
            return true;
        }

        private boolean software(@NotNull String arg) {
            HashMap<ServerSoftware, Double> software = new HashMap<>();
            for (ServerSoftware soft : ServerSoftware.values()) software.put(soft, similarity(arg, soft.displayName().toLowerCase()));
            List<ServerSoftware> result = software.entrySet().stream().filter(e -> MIN_SIMILARITY <= e.getValue()).map(Map.Entry::getKey).toList();
            if (result.isEmpty()) return false;
            STDIO.list(CustomStyle.SOFTWARE, "Software", result.toArray());
            return true;
        }

        private static double similarity(@NotNull String s1, @NotNull String s2) {
            String longer = s1, shorter = s2;
            if (s1.length() < s2.length()) {
                longer = s2;
                shorter = s1;
            }

            int longerLength = longer.length();
            if (longerLength == 0) {
                return 1.0; // Both strings are empty
            }

            return (longerLength - editDistance(longer, shorter)) / (double) longerLength;
        }

        private static int editDistance(String s1, String s2) {
            s1 = s1.toLowerCase();
            s2 = s2.toLowerCase();

            int[] costs = new int[s2.length() + 1];
            for (int i = 0; i <= s1.length(); i++) {
                int lastValue = i;
                for (int j = 0; j <= s2.length(); j++) {
                    if (i == 0) {
                        costs[j] = j;
                    } else {
                        if (j > 0) {
                            int newValue = costs[j - 1];
                            if (s1.charAt(i - 1) != s2.charAt(j - 1)) {
                                newValue = Math.min(Math.min(newValue, lastValue), costs[j]) + 1;
                            }
                            costs[j - 1] = lastValue;
                            lastValue = newValue;
                        }
                    }
                }
                if (i > 0) {
                    costs[s2.length()] = lastValue;
                }
            }
            return costs[s2.length()];
        }
    },

    INSTALL("Install selected", "-PCSZIA", "ADD") {
        @Override
        public void onRun(@NotNull Parser.Result result) {
            List<Plugin> plugins = Plugin.selected();
            List<ServerSoftware> software = ServerSoftware.selected();
            if (plugins.isEmpty() && software.isEmpty()) {
                STDIO.warn("Nothing selected");
                return;
            }

            STDIO.log(CustomStyle.INSTALL, plugins.size(), " plugin(s) and ", software.size(), " software to install");
            if (!STDIO.getConfirmation("Continue installation")) return;
            for (Plugin p : plugins) p.install();
            for (ServerSoftware s : software) s.install();
        }
    },

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

    LIST("List selected", "-PCSZIA", "SHOW", "RESOLVE") {
        @Override
        public void onRun(@NotNull Parser.Result result) {
            if (CustomFlag.INSTALLED.isSelected()) {
                if (CustomFlag.ALL.isSelected()) {
                    Plugin.requestInstalled();
                    PluginCategory.requestInstalled();
                    PluginServer.requestInstalled();
                    ServerSoftware.requestInstalled();
                } else {
                    boolean selected = false;
                    if (CustomFlag.PLUGIN.isSelected()) {
                        Plugin.requestInstalled();
                        selected = true;
                    }
                    if (CustomFlag.CATEGORY.isSelected()) {
                        PluginCategory.requestInstalled();
                        selected = true;
                    }
                    if (CustomFlag.SERVER.isSelected()) {
                        PluginServer.requestInstalled();
                        selected = true;
                    }
                    if (CustomFlag.SOFTWARE.isSelected()) {
                        ServerSoftware.requestInstalled();
                        selected = true;
                    }
                    if (!selected) Plugin.requestInstalled();
                }
            } else {
                if (CustomFlag.ALL.isSelected() || (!CustomFlag.PLUGIN.isSelected() && !CustomFlag.CATEGORY.isSelected() && !CustomFlag.SERVER.isSelected() && !CustomFlag.SOFTWARE.isSelected())) {
                    Plugin.requestAll();
                    PluginCategory.requestAll();
                    PluginServer.requestAll();
                    ServerSoftware.requestAll();
                } else if (Plugin.selected().isEmpty() && PluginCategory.selected().isEmpty() && PluginServer.selected().isEmpty() && ServerSoftware.selected().isEmpty()) {
                    if (CustomFlag.PLUGIN.isSelected()) Plugin.requestAll();
                    if (CustomFlag.CATEGORY.isSelected()) PluginCategory.requestAll();
                    if (CustomFlag.SERVER.isSelected()) PluginServer.requestAll();
                    if (CustomFlag.SOFTWARE.isSelected()) ServerSoftware.requestAll();
                } else {
                    if (CustomFlag.PLUGIN.isSelected() || CustomFlag.CATEGORY.isSelected() || CustomFlag.SERVER.isSelected()) {
                        Plugin.listSelected();
                        if (CustomFlag.CATEGORY.isSelected()) PluginCategory.listSelected();
                        if (CustomFlag.SERVER.isSelected()) PluginServer.listSelected();
                    }
                    if (CustomFlag.SOFTWARE.isSelected()) ServerSoftware.listSelected();
                }
            }
        }
    },

    RUN("Run server", ":only | -Z") {
        @Override
        public void onRun(@NotNull Parser.Result result) {
            List<ServerSoftware> selected = ServerSoftware.selected();
            ServerSoftware software;

            if (selected.isEmpty()) {
                List<ServerSoftware> installed = ServerSoftware.installed();
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
                builder.directory(ServerSoftware.FOLDER);
                Process process = builder.start();
                STDIO.link(process, software.displayName());
            } catch (IOException e) {
                STDIO.err(e, "Failed to run ", installation);
            }
        }
    },

    STATUS("Installation status", "", "STATE", "INFO") {
        @Override
        public void onRun(@NotNull Parser.Result result) {
            boolean out = Plugin.listInstalled();
            out = Plugin.listLinked() || out;
            out = Plugin.listUnknown() || out;
            out = ServerSoftware.listInstalled() || out;
            out = ServerSoftware.listUnknown() || out;

            if (!out) {
                STDIO.warn(CustomStyle.SOFTWARE.toString(), BOLD, "Nothing installed to show");
            }
        }
    },

    UNINSTALL("Uninstall selected", "-PCSZIA", "Remove", "Delete") {
        @Override
        public void onRun(@NotNull Parser.Result result) {
            List<Plugin> plugins = Plugin.selected();
            List<ServerSoftware> software = ServerSoftware.selected();

            if (plugins.isEmpty() && software.isEmpty()) {
                STDIO.warn("Nothing selected");
                return;
            }

            STDIO.log(CustomStyle.UNINSTALL, plugins.size(), " plugin(s) and ", software.size(), " software to uninstall");
            if (!STDIO.getConfirmation("Continue removal")) return;
            for (Plugin p : plugins) p.uninstall();
            for (ServerSoftware s : software) s.uninstall();
        }
    },

    UPDATE("Update selected", ":-I | -PCSZA", "Upgrade") {
        @Override
        public void onRun(@NotNull Parser.Result result) {
            List<Plugin> plugins = Plugin.selected();
            List<ServerSoftware> software = ServerSoftware.selected();
            if (plugins.isEmpty() && software.isEmpty()) { // Auto select installed if nothing specified
                plugins = Plugin.installed();
                software = ServerSoftware.installed();
            }

            if (plugins.isEmpty() && software.isEmpty()) {
                STDIO.warn("Nothing selected");
                return;
            }

            STDIO.log(CustomStyle.UPDATE, plugins.size(), " plugin(s) and ", software.size(), " software to update");
            if (!STDIO.getConfirmation("Continue update")) return;
            for (Plugin p : plugins) p.update();
            for (ServerSoftware s  : software) s.update();
        }
    };

    // Package data into container from lib

    private final @NotNull Data data;

    CustomCommand(@NotNull String info, @NotNull String usage, @NotNull String @NotNull ... refs) {
        this.data = new Data(this, this.name(), info, usage, refs);
    }

    @Override
    public @NotNull Data data() {
        return this.data;
    }
}
