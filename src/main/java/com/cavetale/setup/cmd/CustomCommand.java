package com.cavetale.setup.cmd;

import com.cavetale.setup.data.*;
import link.l_pf.cmdlib.app.Command;
import link.l_pf.cmdlib.app.Parser;
import link.l_pf.cmdlib.app.container.Contents;
import link.l_pf.cmdlib.app.container.FileListContents;
import link.l_pf.cmdlib.app.container.StringListContents;
import link.l_pf.cmdlib.shell.Style;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.cavetale.setup.cmd.CustomStyle.*;
import static link.l_pf.cmdlib.shell.Shell.STDIO;

/** Custom commands for the command line app. */
public enum CustomCommand implements Command {
    COMPARE("Compare selected", "-PCSZA:select", "VERIFY", "CHECK"){
        @Override
        public void onRun(@NotNull Parser.Result result) {
            if (Plugin.selected().isEmpty() && ServerSoftware.selected().isEmpty()) {
                STDIO.warn("Nothing selected for comparison");
                return;
            }

            listCounted(SELECT, " plugin(s) installed", Plugin.matching().toArray());
            listCounted(SUPERFLUOUS, " plugin(s) superfluous", Plugin.superfluous().toArray());
            listCounted(MISSING, " plugin(s) missing", Plugin.missing().toArray());
            listCounted(CustomStyle.LINK, "unknown linked plugin(s)", Plugin.unknown().linked().toArray());
            listCounted(UNKNOWN, "unknown plugin(s)", Plugin.unknown().unknown().toArray());

            listCounted(SELECT, " server software installed", ServerSoftware.matching().toArray());
            listCounted(SUPERFLUOUS, " server software superfluous", ServerSoftware.superfluous().toArray());
            listCounted(MISSING, " server software missing", ServerSoftware.missing().toArray());
            listCounted(CustomStyle.LINK, "unknown linked server software", ServerSoftware.unknown().linked().toArray());
            listCounted(UNKNOWN, "unknown server software", ServerSoftware.unknown().unknown().toArray());
        }
    },

    EULA("Agree to the EULA", "-y:auto") {
        private static final @NotNull File EULA = new File("eula.txt");

        @Override
        public void onRun(@NotNull Parser.Result result) {
            if (!EULA.exists()) {
                STDIO.err(EULA, " does not exist. You may need to run the server first.");
                return;
            }

            if (!STDIO.getConfirmation("Agree to the Minecraft EULA")) return;

            // Read eula and set to true
            StringBuilder builder = new StringBuilder();
            try (FileInputStream in = new FileInputStream(EULA)) {
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

            // Write edited eula
            try (BufferedWriter writer =
                         new BufferedWriter(
                                 new OutputStreamWriter(
                                         new FileOutputStream(EULA)))) {
                writer.write(builder.toString());
                writer.flush();
            } catch (IOException e) {
                STDIO.err(e, "Failed to write eula");
            }
        }
    },

    INSTALL("Install selected", "-PCSZA:select", "ADD") {
        @Override
        public void onRun(@NotNull Parser.Result result) {
            Set<Plugin> plugins = Plugin.missing();
            Set<ServerSoftware> software = ServerSoftware.missing();

            int missing = plugins.size() + software.size();
            int selected = Plugin.selected().size() + ServerSoftware.selected().size();

            if (selected == 0) {
                STDIO.warn("Nothing selected for installation");
                return;
            }

            if (missing == 0) {
                STDIO.warn("Everything selected is already installed");
                return;
            }

            STDIO.log(CustomStyle.INSTALLED, "(", missing, '/', selected, ") will be installed");
            if (!STDIO.getConfirmation("Confirm installation")) return;
            Source.install(plugins, software);
        }
    },

    LINK("Link files as plugins", "[path]:files") {
        @Override
        public @NotNull Contents<?> init() {
            return new FileListContents();
        }

        @Override
        public void onRun(@NotNull Parser.Result result) {
            Set<File> files = new HashSet<>((List<File>) contents());

            if (files.isEmpty()) {
                STDIO.warn("Nothing selected to link");
                return;
            }

            STDIO.log(CustomStyle.LINK, files.size(), " file(s) selected to link");
            if (!STDIO.getConfirmation("Confirm linking")) return;

            File folder = Plugin.PLUGINS_DIR;
            boolean _ = folder.mkdirs();

            if (!folder.isDirectory()) {
                STDIO.err("Failed to init dirs");
                return;
            }

            for (File f : files) {
                try {
                    Plugin.get(f);
                } catch (Source.UntrackedException e) {
                    STDIO.warn("Untracked file \"", f, '"');
                    if (!STDIO.getConfirmation("Link anyway")) continue;
                } catch (Entry.UnknownException e) {
                    STDIO.warn("Unknown reference \"", f.getName(), '"');
                    if (!STDIO.getConfirmation("Link anyway")) continue;
                }

                STDIO.openInfo("Linking ", f);

                if (!f.exists()) {
                    STDIO.err("failed (" + f + " does not exist)");
                    continue;
                }

                if (!f.isFile()) {
                    STDIO.err("failed (" + f + " must be a file)");
                    continue;
                }

                File link = new File(folder, f.getName());

                if (link.exists()) {
                    STDIO.closeErr("failed (link target already exists)");
                    continue;
                }

                try {
                    Files.createSymbolicLink(link.getAbsoluteFile().toPath(), f.getAbsoluteFile().toPath());
                    STDIO.closeInfoDone();
                } catch (IOException e) {
                    STDIO.closeErr(e, "failed");
                }
            }
        }
    },

    LIST("List selected", "-PCSZIA:select", "SHOW", "RESOLVE", "GET") {
        @Override
        public void onRun(@NotNull Parser.Result result) {
            boolean s;
            s = listCounted(PLUGIN, " plugin(s) selected", Plugin.selected().toArray());
            s = listCounted(CATEGORY, " plugin categor(y/ies) selected", PluginCategory.selected().toArray()) || s;
            s = listCounted(SERVER, " server preset(s) selected", ServerPreset.selected().toArray()) || s;
            s = listCounted(SOFTWARE, " server software selected", ServerSoftware.selected().toArray()) || s;
            if (!s) STDIO.warn("Nothing selected");
        }
    },

    RUN("Run server", ":unique! | -Z:select") {
        @Override
        public void onRun(@NotNull Parser.Result result) {
            Set<ServerSoftware> selected = ServerSoftware.selected();
            ServerSoftware software;

            if (selected.isEmpty()) {
                Set<ServerSoftware> installed = ServerSoftware.installed();
                if (installed.isEmpty()) {
                    STDIO.err("No software installed");
                    return;
                }

                if (installed.size() > 1) {
                    STDIO.err("Select the software to run (multiple installed)");
                    return;
                }

                software = installed.iterator().next();
            } else {
                if (selected.size() > 1) {
                    STDIO.err("Select only one software (multiple selected)");
                    return;
                }

                software = selected.iterator().next();
            }

            List<Source.Install> installations = software.installations();

            if (installations.isEmpty()) {
                STDIO.err("Selected is not installed");
                return;
            }

            if (installations.size() > 1) {
                STDIO.err(software.ref, " has multiple installations");
                return;
            }

            String installation = installations.getFirst().name();

            try {
                ProcessBuilder builder = new ProcessBuilder(
                    "java", "-XX:+UseG1GC", "-Xmx2g", "-jar", installation, "nogui"
                );
                builder.directory(ServerSoftware.SERVER_DIR);
                Process process = builder.start();
                STDIO.link(process, software.ref);
            } catch (IOException e) {
                STDIO.err(e, "Failed to run ", installation);
            }
        }
    },

    SEARCH("Search refs", "[string]", "Find") {
        @Override
        public @NotNull Contents<?> init() {
            return new StringListContents();
        }

        @Override
        public void onRun(@NotNull Parser.Result result) {
            String search = String.join(" ", (List<String>) contents()).toLowerCase();
            boolean s;
            s = listCounted(PLUGIN, " plugin(s) found", Plugin.search(search).toArray());
            s = listCounted(CATEGORY, " plugin categor(y/ies) found", PluginCategory.search(search).toArray()) || s;
            s = listCounted(SERVER, " server preset(s) found", ServerPreset.search(search).toArray()) || s;
            s = listCounted(SOFTWARE, " server software found", ServerSoftware.search(search).toArray()) || s;
            if (!s) STDIO.warn("Nothing found");
        }
    },

    STATUS("Installation status", "= list -I") {
        @Override
        public void onRun(@NotNull Parser.Result result) {
            boolean i;
            i = listCounted(PLUGIN, " plugin(s) installed", Plugin.installed().toArray());
            i = listCounted(CATEGORY, " plugin categor(y/ies) installed", PluginCategory.installed().toArray()) || i;
            i = listCounted(SERVER, " server preset(s) installed", ServerPreset.installed().toArray()) || i;
            i = listCounted(CustomStyle.LINK, "unknown linked plugin(s)", Plugin.unknown().linked().toArray()) || i;
            i = listCounted(UNKNOWN, "unknown plugin(s)", Plugin.unknown().unknown().toArray()) || i;
            i = listCounted(SOFTWARE, " server software installed", ServerSoftware.installed().toArray()) || i;
            i = listCounted(CustomStyle.LINK, "unknown linked server software", ServerSoftware.unknown().linked().toArray()) || i;
            i = listCounted(UNKNOWN, "unknown server software", ServerSoftware.unknown().unknown().toArray()) || i;
            if (!i) STDIO.warn("Nothing installed");
        }
    },

    UNINSTALL("Uninstall selected", "-PCSZIA:select", "Remove", "Delete") {
        @Override
        public void onRun(@NotNull Parser.Result result) {
            Set<Plugin> plugins = Plugin.matching();
            Set<ServerSoftware> software = ServerSoftware.matching();

            int matching = plugins.size() + software.size();
            int selected = Plugin.selected().size() + ServerSoftware.selected().size();

            if (selected == 0) {
                STDIO.warn("Nothing selected for removal");
                return;
            }

            if (matching == 0) {
                STDIO.warn("Nothing selected is installed");
                return;
            }

            STDIO.log(CustomStyle.UNINSTALL, "(", matching, '/', selected, ") will be removed");
            if (!STDIO.getConfirmation("Confirm removal")) return;
            Source.uninstall(plugins, software);
        }
    },

    UPDATE("Update selected", "-PCSZIA:select", "Upgrade") {
        @Override
        public void onRun(@NotNull Parser.Result result) {
            Set<Plugin> plugins = Plugin.matching();
            Set<ServerSoftware> software = ServerSoftware.matching();

            int matching = plugins.size() + software.size();
            int selected = Plugin.selected().size() + ServerSoftware.selected().size();

            if (selected == 0) {
                STDIO.warn("Nothing selected for update");
                return;
            }

            if (matching == 0) {
                STDIO.warn("Nothing selected is installed");
                return;
            }

            STDIO.log(CustomStyle.UPDATE, "(", matching, '/', selected, ") will be updated");
            if (!STDIO.getConfirmation("Confirm update")) return;
            Source.update(plugins, software);
        }
    };

    // As per library specifications...

    private final @NotNull Data data;

    CustomCommand(@NotNull String info, @NotNull String usage, @NotNull String @NotNull ... refs) {
        this.data = new Data(this, this.name(), info, usage, refs);
    }

    @Override
    public @NotNull Data data() {
        return this.data;
    }

    // Utils

    /**  Writes objects to console as table if not empty. */
    private static boolean listCounted(
        @NotNull Style style,
        @NotNull String header,
        @Nullable Object @NotNull [] objects
    ) {
        if (objects.length == 0) return false;
        STDIO.list(style, objects.length + header, objects);
        return true;
    }
}
