package com.cavetale.manager.parser;

import com.cavetale.manager.Manager;
import com.cavetale.manager.data.Sel;
import com.cavetale.manager.data.plugin.*;
import com.cavetale.manager.data.server.Software;
import com.cavetale.manager.util.Util;
import com.cavetale.manager.util.console.Console;
import com.cavetale.manager.util.console.Style;
import com.cavetale.manager.util.console.Type;
import com.cavetale.manager.util.console.Code;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.nio.file.Files;
import java.util.*;

public enum Command {
    // TODO: Compare only
    COMPARE("Compare installed to selected software", "VERIFY", "CHECK"){
        @Override
        public
        void run(@NotNull Parser parser) {
            Plugin.summarize();
            Software.summarize();
        }
    },

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
                    if (Flag.ERROR.isSelected()) Console.log(Type.REQUESTED, e);
                }
            }
        }
    },

    EULA("Agree to the Minecraft EULA") {
        @Override
        public void run(@NotNull Parser parser) {
            File eula = new File("eula.txt");
            if (!eula.exists()) {
                Console.log(Type.ERR, eula.getName() + " does not exist. You may need to run the server first.\n");
                return;
            }

            if (!Console.confirm("Agree to the Minecraft EULA")) return;

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
                        else if (line.equals("eula=true")) Console.log(Type.INFO, "Already accepted the eula\n");
                        builder.append(line);
                        if (c == '\n') builder.append(c);
                        lineBuilder = new StringBuilder();
                    } else lineBuilder.append(c);
                }
            } catch (IOException e) {
                Console.log(Type.ERR, "Failed to read eula (" + e.getMessage() + ")\n");
                if (Flag.ERROR.isSelected()) Console.log(Type.REQUESTED, e);
                return;
            }

            try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(eula)))) { // Write eula
                writer.write(builder.toString());
                writer.flush();
            } catch (IOException e) {
                Console.log(Type.ERR, "Failed to write eula (" + e.getMessage() + ")\n");
                if (Flag.ERROR.isSelected()) Console.log(Type.REQUESTED, e);
            }
        }
    },

    EXIT("Exit interactive mode", "E", "QUIT", "Q", "STOP") {
        @Override
        public void run(@NotNull Parser parser) {
            Manager.exit();
        }
    },

    FIND("Find anything", "Search") {
        private static double MIN = 0.33;

        @Override
        public void run(@NotNull Parser parser) {
            String arg = String.join(" ", parser.args()).toLowerCase();

            boolean all = Flag.ALL.isSelected() || (!Flag.COMMAND.isSelected() && !Flag.FLAG.isSelected() && !Flag.PLUGIN.isSelected() && !Flag.CATEGORY.isSelected() && !Flag.SERVER.isSelected() && !Flag.SOFTWARE.isSelected());
            boolean found = false;

            if (all || Flag.COMMAND.isSelected()) found = commands(arg);
            if (all || Flag.FLAG.isSelected()) found = flags(arg);
            if (all || Flag.PLUGIN.isSelected()) found = plugins(arg) || found;
            if (all || Flag.CATEGORY.isSelected()) found = categories(arg) || found;
            if (all || Flag.SERVER.isSelected()) found = servers(arg) || found;
            if (all || Flag.SOFTWARE.isSelected()) found = software(arg) || found;

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
            for (Flag flag : Flag.values()) flags.put(flag, Util.similarity(arg, flag.displayName().toLowerCase()));
            List<Flag> result = flags.entrySet().stream().filter(e -> MIN <= e.getValue()).map(Map.Entry::getKey).toList();
            if (result.isEmpty()) return false;
            Console.sep();
            Console.logL(Type.REQUESTED, Style.FLAG, "Flags", 4, 21, result.toArray());
            return true;
        }

        private boolean plugins(@NotNull String arg) {
            HashMap<Plugin, Double> plugins = new HashMap<>();
            for (Plugin plugin : Plugin.values()) plugins.put(plugin, Util.similarity(arg, plugin.displayName().toLowerCase()));
            List<Plugin> result = plugins.entrySet().stream().filter(e -> MIN <= e.getValue()).map(Map.Entry::getKey).toList();
            if (result.isEmpty()) return false;
            Console.sep();
            Console.logL(Type.REQUESTED, Style.PLUGIN, "Plugins", 4, 21, result.toArray());
            return true;
        }

        private boolean categories(@NotNull String arg) {
            HashMap<Category, Double> categories = new HashMap<>();
            for (Category category : Category.values()) categories.put(category, Util.similarity(arg, category.displayName().toLowerCase()));
            List<Category> result = categories.entrySet().stream().filter(e -> MIN <= e.getValue()).map(Map.Entry::getKey).toList();
            if (result.isEmpty()) return false;
            Console.sep();
            Console.logL(Type.REQUESTED, Style.CATEGORY, "Categories", 4, 21, result.toArray());
            return true;
        }

        private boolean servers(@NotNull String arg) {
            HashMap<Server, Double> servers = new HashMap<>();
            for (Server server : Server.values()) servers.put(server, Util.similarity(arg, server.displayName().toLowerCase()));
            List<Server> result = servers.entrySet().stream().filter(e -> MIN <= e.getValue()).map(Map.Entry::getKey).toList();
            if (result.isEmpty()) return false;
            Console.sep();
            Console.logL(Type.REQUESTED, Style.SERVER, "Servers", 4, 21, result.toArray());
            return true;
        }

        private boolean software(@NotNull String arg) {
            HashMap<Software, Double> software = new HashMap<>();
            for (Software soft : Software.values()) software.put(soft, Util.similarity(arg, soft.displayName().toLowerCase()));
            List<Software> result = software.entrySet().stream().filter(e -> MIN <= e.getValue()).map(Map.Entry::getKey).toList();
            if (result.isEmpty()) return false;
            Console.sep();
            Console.logL(Type.REQUESTED, Style.SOFTWARE, "Software", 4, 21, result.toArray());
            return true;
        }
    },

    HELP("Show usage help") {
        @Override
        public void run(@NotNull Parser parser) {
            Manager.help();
        }
    },

    INSTALL("Install plugins and server software", "ADD") {
        @Override
        public void run(@NotNull Parser parser) {
            List<Plugin> plugins = Plugin.selected();
            List<Software> software = Software.selected();
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

    LINK("Link any jar archive path to the plugins directory") {
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
                File folder = Plugin.FOLDER;
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
                    if (Flag.ERROR.isSelected()) Console.log(Type.REQUESTED, e);
                }
            }
        }
    },

    LIST("List plugins, categories, servers and server software", "SHOW", "RESOLVE") {
        @Override
        public void run(@NotNull Parser parser) {
            if (Flag.INSTALLED.isSelected()) {
                if (Flag.ALL.isSelected()) {
                    Plugin.listInstalled();
                    Category.listInstalled();
                    Server.listInstalled();
                    Software.listInstalled();
                } else {
                    boolean selected = false;
                    if (Flag.PLUGIN.isSelected()) {
                        Plugin.listInstalled();
                        selected = true;
                    }
                    if (Flag.CATEGORY.isSelected()) {
                        Category.listInstalled();
                        selected = true;
                    }
                    if (Flag.SERVER.isSelected()) {
                        Server.listInstalled();
                        selected = true;
                    }
                    if (Flag.SOFTWARE.isSelected()) {
                        Software.listInstalled();
                        selected = true;
                    }
                    if (!selected) Plugin.listInstalled();
                }
            } else {
                if (Flag.ALL.isSelected() || (!Flag.PLUGIN.isSelected() && !Flag.CATEGORY.isSelected() && !Flag.SERVER.isSelected() && !Flag.SOFTWARE.isSelected())) {
                    Plugin.list();
                    Category.list();
                    Server.list();
                    Software.list();
                } else if (Plugin.selected().isEmpty() && Category.selected().isEmpty() && Server.selected().isEmpty() && Software.selected().isEmpty()) {
                    if (Flag.PLUGIN.isSelected()) Plugin.list();
                    if (Flag.CATEGORY.isSelected()) Category.list();
                    if (Flag.SERVER.isSelected()) Server.list();
                    if (Flag.SOFTWARE.isSelected()) Software.list();
                } else {
                    if (Flag.PLUGIN.isSelected() || Flag.CATEGORY.isSelected() || Flag.SERVER.isSelected()) {
                        Plugin.listSelected();
                        if (Flag.CATEGORY.isSelected()) Category.listSelected();
                        if (Flag.SERVER.isSelected()) Server.listSelected();
                    }
                    if (Flag.SOFTWARE.isSelected()) Software.listSelected();
                }
            }
        }
    },

    RUN("Run installed server software") {
        @Override
        public void run(@NotNull Parser parser) {
            List<Software> selected = Software.selected();
            if (!Flag.SOFTWARE.isSelected() || Software.selected().isEmpty()) {
                selected = Arrays.asList(Software.values());
            }
            if (selected.size() > 1) Console.log(Type.WARN, "Multiple software selected\n");
            for (Software software : selected) {
                List<String> installations = software.installations();
                if (!installations.isEmpty()) {
                    if (installations.size() > 1) Console.log(Type.WARN, software.displayName() + " has multiple installations\n");
                    String installation = installations.getFirst();
                    Console.log(Type.INFO, "Running " + installation + "\n\n" + Code.RESET);

                    try {
                        ProcessBuilder builder = new ProcessBuilder("java", "-XX:+UseG1GC", "-Xmx2g", "-jar", installation, "nogui");
                        builder.directory(Software.FOLDER);
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
                                System.out.print(Code.DARK_GRAY_FG + "[" + software.displayName() + "] " + Code.RESET);
                                int i = outStream.read(); // i: current char
                                while(0 <= i) {
                                    System.out.write(i);
                                    System.out.flush();
                                    if (i == '\n') System.out.print(Code.DARK_GRAY_FG + "[" + software.displayName() + "] " + Code.RESET);
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
                        if (Flag.ERROR.isSelected()) Console.log(Type.REQUESTED, e);
                    } catch (InterruptedException e) {
                        Console.log(Type.WARN, "\n\n" + installation + " was interrupted\n");
                        if (Flag.ERROR.isSelected()) Console.log(Type.REQUESTED, e);
                    }

                    return;
                }
            }
            Console.log(Type.REQUESTED, Style.ERR, "None of the selected software is installed\n");
        }
    },

    // TODO: Status only
    STATUS("View installation status", "STATE", "INFO") {
        @Override
        public
        void run(@NotNull Parser parser) {
            Plugin.summarize();
            Software.summarize();
        }
    },

    UNINSTALL("Uninstall plugins, server software and files", "Remove", "Delete") {
        @Override
        public void run(@NotNull Parser parser) {
            List<Plugin> plugins = Plugin.selected();
            List<Software> software = Software.selected();
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

    UPDATE("Update plugins and software", "Upgrade") {
        @Override
        public void run(@NotNull Parser parser) {
            List<Plugin> plugins = Plugin.selected();
            List<Software> software = Software.selected();
            if (plugins.isEmpty() && software.isEmpty()) { // Auto select installed if nothing specified
                plugins = Plugin.installed();
                software = Software.installed();
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

    public final @NotNull String[] refs;
    public final @NotNull String info;

    private @NotNull Sel sel = Sel.OFF;

    Command(@NotNull String info, @NotNull String @NotNull ... refs) {
        this.refs = new String[refs.length + 1];
        this.refs[0] = Util.capsToCamel(this.name());
        System.arraycopy(refs, 0, this.refs, 1, refs.length);
        this.info = info;
    }

    public @NotNull String displayName() {
        return this.refs[0];
    }

    @Override
    public @NotNull String toString() {
        return this.displayName();
    }

    public abstract void run(@NotNull Parser parser);

    public void help(@NotNull Parser parser) {
        Console.log(Type.REQUESTED, Style.HELP, this.refs[0] + ": " + this.info + "\n");
    }

    public static @NotNull Command get(@NotNull String ref) throws NotFoundException {
        for (Command c : Command.values()) for (String r : c.refs) if (r.equalsIgnoreCase(ref)) return c;
        throw new NotFoundException(ref);
    }

    //= Selection ==

    public void target() {
        this.sel = Sel.TARGET;
    }

    public boolean isTargeted() {
        return this.sel == Sel.TARGET;
    }

    public void select() {
        if (this.sel == Sel.OFF) this.sel = Sel.ON;
    }

    public boolean isSelected() {
        return this.sel != Sel.OFF;
    }

    public void reset() {
        this.sel = Sel.OFF;
    }

    public static class NotFoundException extends InputException {
        public NotFoundException(@NotNull String ref) {
            super("Command \"" + ref + "\" not found");
        }
    }
}
