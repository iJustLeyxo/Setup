package com.cavetale.manager.data.plugin;

import com.cavetale.manager.parser.InputException;
import com.cavetale.manager.util.console.Console;
import com.cavetale.manager.util.console.Style;
import com.cavetale.manager.util.console.Type;
import com.cavetale.manager.util.console.XCode;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * Servers, used to group plugins by installed server
 */
public enum Server implements Provider {
    Base("Plugins for all servers", Category.Core, Category.Global),
    Void("Plugins for void servers", Server.Base),
    Hub("Plugins for hub servers", Server.Base, Category.Survival, Category.Build, Category.Hub,
            Plugin.Structure, Plugin.ExtremeGrassGrowing, Plugin.KingOfTheLadder, Plugin.RedLightGreenLight),
    Build("Plugins for build servers", Server.Base, Category.Survival, Category.Build, Category.Home),
    Mine("Plugins for mine servers", Server.Base, Category.Survival, Category.Build, Category.Mine),
    Creative("Plugins for creative servers", Server.Base, Category.Creative, Category.Build,
            Plugin.Enemy, Plugin.Festival, Plugin.Pictionary, Plugin.Race, Plugin.Resident),
    Event("Plugins for event servers", Server.Base, Plugin.Worlds),
    Classic("Plugins for classic servers", Server.Base, Category.Survival, Category.Build);

    private final @NotNull String info;
    private final @NotNull Plugin[] plugins;

    Server(@NotNull String info, @NotNull Provider @NotNull ... providers) {
        this.info = info;
        Set<Plugin> plugins = new HashSet<>();
        for (Provider p : providers) if (this != p) Collections.addAll(plugins, p.plugins());
        this.plugins = plugins.toArray(new Plugin[]{});
    }

    @Override
    public @NotNull Plugin[] plugins() {
        return this.plugins;
    }

    public static @NotNull Server get(@NotNull String ref) throws NotFoundException {
        for (Server s : Server.values()) if (s.name().equalsIgnoreCase(ref)) return s;
        throw new NotFoundException(ref);
    }

    public static void list() {
        Console.sep();
        Console.log(Type.REQUESTED, Style.SERVER, XCode.BOLD +
                "--------------------------------------- " +
                "Servers ---------------------------------------\n");
        Console.logF(Type.REQUESTED, Style.SERVER, "%-16s | %-68s\n", "Server", "Info");
        Console.log(Type.REQUESTED, Style.SERVER, "----------------------------------------------" +
                "-----------------------------------------\n");
        ArrayList<Server> servers = new ArrayList<>(List.of(Server.values()));
        Collections.sort(servers);
        for (Server s : servers) {
            Console.logF(Type.REQUESTED, Style.SERVER, "%-16s | %-68s\n", s.name(), s.info);
        }
    }

    public static class NotFoundException extends InputException {
        public NotFoundException(@NotNull String ref) {
            super("Server \"" + ref + "\" not found");
        }
    }
}