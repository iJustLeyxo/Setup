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
    BASE("Plugins for all servers", Category.CORE, Category.GLOBAL),
    VOID("Plugins for void servers", Server.BASE),
    HUB("Plugins for hub servers", Server.BASE, Category.SURVIVAL, Category.BUILD,
            Category.HUB, Plugin.Structure),
    BUILD("Plugins for build servers", Server.BASE, Category.SURVIVAL, Category.BUILD, Category.HOME),
    MINE("Plugins for mine servers", Server.BASE, Category.SURVIVAL, Category.BUILD, Category.MINE),
    CREATIVE("Plugins for creative servers", Server.BASE, Category.CREATIVE, Category.BUILD,
            Plugin.Enemy, Plugin.Festival, Plugin.LinkPortal, Plugin.Race, Plugin.Raid, Plugin.Resident),
    EVENT("Plugins for event servers", Server.BASE, Plugin.Worlds),
    CLASSIC("Plugins for classic servers", Server.BASE, Category.SURVIVAL, Category.BUILD);

    public final @NotNull String ref;
    public final @NotNull String info;
    public final @NotNull Provider[] providers;

    Server(@NotNull String info, @NotNull Provider... providers) {
        this.ref = this.name().toLowerCase();
        this.info = info;
        this.providers = providers;
    }

    @Override
    public Set<Plugin> plugins() {
        Set<Plugin> plugins = new HashSet<>();
        for (Provider p : providers) {
            plugins.addAll(p.plugins());
        }
        return plugins;
    }

    public static @NotNull Server get(@NotNull String ref) throws NotFoundException {
        for (Server s : Server.values()) {
            if (ref.equalsIgnoreCase(s.ref)) return s;
        }
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
            Console.logF(Type.REQUESTED, Style.SERVER, "%-16s | %-68s\n", s.ref, s.info);
        }
    }

    public static class NotFoundException extends InputException {
        public NotFoundException(@NotNull String ref) {
            super("Server \"" + ref + "\" not found");
        }
    }
}