package com.cavetale.manager.parser;

import com.cavetale.manager.Manager;
import com.cavetale.manager.data.plugin.*;
import com.cavetale.manager.data.server.Software;
import com.cavetale.manager.data.server.Softwares;
import com.cavetale.manager.util.Util;
import com.cavetale.manager.util.console.Console;
import com.cavetale.manager.util.console.Style;
import com.cavetale.manager.util.console.Type;
import com.cavetale.manager.util.console.XCode;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;

public enum Command {
    CONNECT("Link this tool to a server installation") {
        @Override
        public void run(@NotNull Parser parser) {
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
                Console.log(Type.REQUESTED, Style.ERR, "No tool jar in working directory\n");
                return;
            }

            List<File> files = parser.args().stream().map(File::new).toList(); // Parse destinations
            if (files.isEmpty()) {
                Console.log(Type.REQUESTED, Style.WARN, "No destination(s) specified\n");
                return;
            }

            Console.log(Type.REQUESTED, Style.LINK, files.size() + " destination(s) will be linked to\n");
            if (!Console.confirm("Continue linking")) return;

            for (File dest : files) {
                Console.log(Type.INFO, "Linking to " + dest.getName());
                if (!dest.isDirectory()) {
                    if (!Console.log(Type.INFO, Style.ERR, " failed (not a folder)\n")) {
                        Console.log(Type.ERR, "Linking to " + dest.getName() + " failed (not a folder)\n");
                    }
                    continue;
                }
                dest.mkdirs();
                File file = new File(dest, installation.getName());
                if (file.exists()) {
                    if (!Console.log(Type.INFO, Style.ERR, " failed (already exists)\n")) {
                        Console.log(Type.ERR, "Linking to " + dest.getName() + " failed (already exists)\n");
                    }
                    return;
                }
                try {
                    Files.createSymbolicLink(file.toPath(), installation.toPath());
                    Console.log(Type.INFO, Style.DONE, " done\n");
                } catch (IOException e) {
                    if (!Console.log(Type.INFO, Style.ERR, " failed (" + e.getMessage() + ")\n")) {
                        Console.log(Type.ERR, "Linking to " + dest.getName() + " failed (" + e.getMessage() + ")\n");
                    }
                    if (Flag.error.isSelected()) Console.log(Type.REQUESTED, e);
                }
            }
        }
    },

    Exit("Exit interactive mode", "E", "Quit", "Q", "Stop") {
        @Override
        public void run(@NotNull Parser parser) {
            Manager.exit();
        }
    },

    Find("Find anything", "Search") {
        private static double MIN = 0.33;

        @Override
        public void run(@NotNull Parser parser) {
            String arg = String.join(" ", parser.args()).toLowerCase();

            boolean all = Flag.all.isSelected() || (!Flag.command.isSelected() && !Flag.flag.isSelected() && !Flag.plugin.isSelected() && !Flag.category.isSelected() && !Flag.server.isSelected() && !Flag.software.isSelected());
            boolean found = false;

            if (all || Flag.command.isSelected()) found = commands(arg);
            if (all || Flag.flag.isSelected()) found = flags(arg);
            if (all || Flag.plugin.isSelected()) found = plugins(arg) || found;
            if (all || Flag.category.isSelected()) found = categories(arg) || found;
            if (all || Flag.server.isSelected()) found = servers(arg) || found;
            if (all || Flag.software.isSelected()) found = software(arg) || found;

            if (!found) Console.log(Type.REQUESTED, Style.WARN, "Nothing found to match \"" + arg + "\"\n");
        }

        private boolean commands(@NotNull String arg) {
            HashMap<Command, Double> commands = new HashMap<>();

            for (Command command : Command.values()) {
                double similarity = 0;
                for (String ref : command.refs) similarity = Math.max(Util.similarity(arg, ref.toLowerCase()), similarity);
                commands.put(command, similarity);
            }

            List<Command> result = commands.entrySet().stream().filter(e -> MIN <= e.getValue()).map(Map.Entry::getKey).toList();
            if (result.isEmpty()) return false;
            Console.sep();
            Console.logL(Type.REQUESTED, Style.COMMAND, "Commands", 4, 21, result.toArray());
            return true;
        }

        private boolean flags(@NotNull String arg) {
            HashMap<Flag, Double> flags = new HashMap<>();
            for (Flag flag : Flag.values()) flags.put(flag, Util.similarity(arg, flag.name().toLowerCase()));
            List<Flag> result = flags.entrySet().stream().filter(e -> MIN <= e.getValue()).map(Map.Entry::getKey).toList();
            if (result.isEmpty()) return false;
            Console.sep();
            Console.logL(Type.REQUESTED, Style.FLAG, "Flags", 4, 21, result.toArray());
            return true;
        }

        private boolean plugins(@NotNull String arg) {
            HashMap<Plugin, Double> plugins = new HashMap<>();
            for (Plugin plugin : Plugin.values()) plugins.put(plugin, Util.similarity(arg, plugin.name().toLowerCase()));
            List<Plugin> result = plugins.entrySet().stream().filter(e -> MIN <= e.getValue()).map(Map.Entry::getKey).toList();
            if (result.isEmpty()) return false;
            Console.sep();
            Console.logL(Type.REQUESTED, Style.PLUGIN, "Plugins", 4, 21, result.toArray());
            return true;
        }

        private boolean categories(@NotNull String arg) {
            HashMap<Category, Double> categories = new HashMap<>();
            for (Category category : Category.values()) categories.put(category, Util.similarity(arg, category.name().toLowerCase()));
            List<Category> result = categories.entrySet().stream().filter(e -> MIN <= e.getValue()).map(Map.Entry::getKey).toList();
            if (result.isEmpty()) return false;
            Console.sep();
            Console.logL(Type.REQUESTED, Style.CATEGORY, "Categories", 4, 21, result.toArray());
            return true;
        }

        private boolean servers(@NotNull String arg) {
            HashMap<Server, Double> servers = new HashMap<>();
            for (Server server : Server.values()) servers.put(server, Util.similarity(arg, server.name().toLowerCase()));
            List<Server> result = servers.entrySet().stream().filter(e -> MIN <= e.getValue()).map(Map.Entry::getKey).toList();
            if (result.isEmpty()) return false;
            Console.sep();
            Console.logL(Type.REQUESTED, Style.SERVER, "Servers", 4, 21, result.toArray());
            return true;
        }

        private boolean software(@NotNull String arg) {
            HashMap<Software, Double> software = new HashMap<>();
            for (Software soft : Software.values()) software.put(soft, Util.similarity(arg, soft.name().toLowerCase()));
            List<Software> result = software.entrySet().stream().filter(e -> MIN <= e.getValue()).map(Map.Entry::getKey).toList();
            if (result.isEmpty()) return false;
            Console.sep();
            Console.logL(Type.REQUESTED, Style.SOFTWARE, "Software", 4, 21, result.toArray());
            return true;
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
                        Console.log(Type.ERR, "Linking " + origin.getName() + " failed (already exists)\n");
                    }
                    continue;
                }
                try {
                    Files.createSymbolicLink(link.getAbsoluteFile().toPath(), origin.getAbsoluteFile().toPath());
                    Console.log(Type.INFO, Style.DONE, " done\n");
                } catch (IOException e) {
                    if (!Console.log(Type.INFO, Style.ERR, " failed (" + e.getMessage() + ")\n")) {
                        Console.log(Type.ERR, "Linking " + origin.getName() + " failed (" + e.getMessage() + ")\n");
                    }
                    if (Flag.error.isSelected()) Console.log(Type.REQUESTED, e);
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
                        ProcessBuilder builder = new ProcessBuilder("java", "-XX:+UseG1GC", "-Xmx2g", "-jar", installation, "nogui");
                        builder.directory(Softwares.FOLDER);
                        builder.redirectErrorStream(true);
                        Process process = builder.start();

                        OutputStream inStream = process.getOutputStream();
                        InputStream outStream = process.getInputStream();

                        Thread inThread = new Thread(() -> {
                            try {
                                int i = System.in.read();
                                while (0 <= i) {
                                    inStream.write(i);
                                    inStream.flush();
                                    i = System.in.read();
                                }
                            } catch (IOException ignored) { }
                        });

                        inThread.setDaemon(true);
                        inThread.start();

                        Thread outThread = new Thread(() -> {
                            try {
                                System.out.print(XCode.GRAY + "[" + software.name() + "] " + XCode.RESET);
                                int i = outStream.read(); // i: current char
                                while(0 <= i) {
                                    System.out.write(i);
                                    System.out.flush();
                                    if (i == '\n') System.out.print(XCode.GRAY + "[" + software.name() + "] " + XCode.RESET);
                                    i = outStream.read();
                                }
                                inThread.interrupt();
                            } catch (IOException ignored) { }
                        });

                        outThread.setDaemon(true);
                        outThread.start();
                        int exit = process.waitFor();
                        if (exit == 0) Console.log(Type.INFO, "\n\n" + installation + " exited with code " + exit + "\n");
                        else Console.log(Type.WARN, "\n\n" + installation + " exited with code " + exit + "\n");
                    } catch (IOException e) {
                        Console.log(Type.ERR, "\n\nFailed to run " + installation + " (" + e.getMessage() + ")\n");
                        if (Flag.error.isSelected()) Console.log(Type.REQUESTED, e);
                    } catch (InterruptedException e) {
                        Console.log(Type.WARN, "\n\n" + installation + " was interrupted\n");
                        if (Flag.error.isSelected()) Console.log(Type.REQUESTED, e);
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

    // TODO: Accept eula command

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
