package com.cavetale.manager.parser;

import com.cavetale.manager.Manager;
import com.cavetale.manager.data.plugin.*;
import com.cavetale.manager.data.server.Software;
import com.cavetale.manager.data.server.Softwares;
import com.cavetale.manager.util.console.Console;
import com.cavetale.manager.util.console.Style;
import com.cavetale.manager.util.console.Type;
import com.cavetale.manager.util.console.XCode;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;

public enum Command {
    Exit("Exit interactive mode", "E", "Quit", "Q", "Stop") {
        @Override
        public void run(@NotNull Parser parser) {
            Manager.exit();
        }
    },

    Help("Show usage help") {
        @Override
        public void run(@NotNull Parser parser) {
            Manager.help();
        }
    },

    Install("Install plugins and server software", "Add") {
        @Override
        public void run(@NotNull Parser parser) {
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
        void run(@NotNull Parser parser) {
            List<File> files = parser.args().stream().map(File::new).toList();

            if (files.isEmpty()) {
                Console.log(Type.REQUESTED, Style.WARN, "No path(s) specified\n");
                return;
            }

            for (File origin : files) {
                try {
                    Plugin.get(origin);
                } catch (Plugin.NotAPluginException e) {
                    Console.log(Type.WARN, e.getMessage() + "\n");
                } catch (Plugin.PluginNotFoundException e) {
                    Console.log(Type.WARN, "Unknown plugin " + origin.getName() + "\n");
                }
            }

            Console.log(Type.REQUESTED, Style.LINK, files.size() + " file(s) will be linked\n");
            if (!Console.confirm("Continue linking")) return;

            for (File origin : files) {
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
                    System.out.println("Linking from " + link + " to " + origin);
                    Files.createSymbolicLink(link.getAbsoluteFile().toPath(), origin.getAbsoluteFile().toPath());
                    Console.log(Type.INFO, Style.DONE, " done\n");
                } catch (IOException e) {
                    if (!Console.log(Type.INFO, Style.ERR, " failed (" + e.getMessage() + ")\n")) {
                        Console.log(Type.ERR, "Linking " + origin.getName() + " failed (" + e.getMessage() + ")\n");
                    }
                }
            }
        }
    },

    List("List plugins, categories, servers and server software", "Show", "Resolve") {
        @Override
        public void run(@NotNull Parser parser) {
            if (Flag.installed.isSelected()) {
                if (Flag.all.isSelected()) {
                    Plugins.listInstalled();
                    Categories.listInstalled();
                    Servers.listInstalled();
                    Softwares.listInstalled();
                } else {
                    boolean selected = false;
                    if (Flag.plugin.isSelected()) {
                        Plugins.listInstalled();
                        selected = true;
                    }
                    if (Flag.category.isSelected()) {
                        Categories.listInstalled();
                        selected = true;
                    }
                    if (Flag.server.isSelected()) {
                        Servers.listInstalled();
                        selected = true;
                    }
                    if (Flag.software.isSelected()) {
                        Softwares.listInstalled();
                        selected = true;
                    }
                    if (!selected) Plugins.listInstalled();
                }
            } else {
                if (Flag.all.isSelected() || (!Flag.plugin.isSelected() && !Flag.category.isSelected() && !Flag.server.isSelected() && !Flag.software.isSelected())) {
                    Plugins.list();
                    Categories.list();
                    Servers.list();
                    Softwares.list();
                } else if (Plugins.selected().isEmpty() && Categories.selected().isEmpty() && Servers.selected().isEmpty() && Softwares.selected().isEmpty()) {
                    if (Flag.plugin.isSelected()) Plugins.list();
                    if (Flag.category.isSelected()) Categories.list();
                    if (Flag.server.isSelected()) Servers.list();
                    if (Flag.software.isSelected()) Softwares.list();
                } else {
                    if (Flag.plugin.isSelected() || Flag.category.isSelected() || Flag.server.isSelected()) {
                        Plugins.listSelected();
                        if (Flag.category.isSelected()) Categories.listSelected();
                        if (Flag.server.isSelected()) Servers.listSelected();
                    }
                    if (Flag.software.isSelected()) Softwares.listSelected();
                }
            }
        }
    },

    RUN("Run installed server software") {
        @Override
        public void run(@NotNull Parser parser) {
            List<Software> selected = Softwares.selected();
            if (!Flag.software.isSelected() || Softwares.selected().isEmpty()) {
                selected = Arrays.asList(Software.values());
            }
            if (selected.size() > 1) Console.log(Type.WARN, "Multiple software selected\n");
            for (Software software : selected) {
                List<String> installations = software.installations();
                if (!installations.isEmpty()) {
                    if (installations.size() > 1) Console.log(Type.WARN, software.name() + " has multiple installations\n");
                    String installation = installations.getFirst();
                    Console.log(Type.INFO, "Running " + installation + "\n\n" + XCode.RESET);

                    try {
                        ProcessBuilder builder = new ProcessBuilder("java", "-XX:+UseG1GC", "-Xmx2g", "-jar", installation);
                        builder.directory(Softwares.FOLDER);
                        builder.redirectErrorStream(true);

                        Process process = builder.start();

                        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                        String line;
                        while ((line = reader.readLine()) != null) {
                            System.out.println(line);
                        }

                        int exit = process.waitFor();
                        if (exit == 0) Console.log(Type.INFO, "\n" + installation + " exited with code " + exit + "\n");
                        else Console.log(Type.WARN, "\n" + installation + " exited with code " + exit + "\n");
                    } catch (IOException e) {
                        Console.log(Type.ERR, "\nFailed to run " + installation + " (" + e.getMessage() + ")\n");
                    } catch (InterruptedException e) {
                        Console.log(Type.WARN, "\n" + installation + " was interrupted\n");
                    }

                    return;
                }
            }
            Console.log(Type.REQUESTED, Style.ERR, "None of the selected software is installed\n");
        }
    },

    Status("View installation status", "Info", "Verify", "Check") {
        @Override
        public
        void run(@NotNull Parser parser) {
            Plugins.summarize();
            Softwares.summarize();
        }
    },

    Uninstall("Uninstall plugins, server software and files", "Remove", "Delete") {
        @Override
        public void run(@NotNull Parser parser) {
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
        public void run(@NotNull Parser parser) {
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
    // TODO: Find command to fins stuff

    public final @NotNull String[] refs;
    public final @NotNull String info;

    private boolean selected = false;

    Command(@NotNull String info, @NotNull String @NotNull ... refs) {
        this.refs = new String[refs.length + 1];
        this.refs[0] = this.name().toLowerCase();
        System.arraycopy(refs, 0, this.refs, 1, refs.length);
        this.info = info;
    }

    public abstract void run(@NotNull Parser parser);

    public void help(@NotNull Parser parser) {
        Console.log(Type.REQUESTED, Style.HELP, this.refs[0] + ": " + this.info + "\n");
    }

    public static @NotNull Command get(@NotNull String ref) throws NotFoundException {
        for (Command c : Command.values()) for (String r : c.refs) if (r.equalsIgnoreCase(ref)) return c;
        throw new NotFoundException(ref);
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public boolean isSelected() {
        return this.selected;
    }

    public static class NotFoundException extends InputException {
        public NotFoundException(@NotNull String ref) {
            super("Command \"" + ref + "\" not found");
        }
    }
}
