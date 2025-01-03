package com.cavetale.manager.parser;

import com.cavetale.manager.parser.container.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Flags, used to specify flag properties
 */
public enum Flag {
    all("Select all"),
    category("Specify categor(y/ies)", "-s []:all | [categories]") {
        @Override
        public @NotNull DummyContainer container() {
            return new CategoryContainer();
        }
    },
    normal("Normal console output"),
    force("Force execution"),
    help("Show command help"),
    interactive("Enter command prompt mode"),
    path('P', "Specify a file path", "-P <path>") {
        @Override
        public @NotNull DummyContainer container() {
            return new PathContainer();
        }
    },
    plugin('p', "Specify plugins(s)", "-p []:all | [categories]") {
        @Override
        public @NotNull DummyContainer container() {
            return new PluginContainer();
        }
    },
    quiet("Reduced console output"),
    server('s', "Specify server(s)", "-s []:all | [servers]") {
        @Override
        public @NotNull DummyContainer container() {
            return new ServerContainer();
        }
    },
    software('S', "Specify server software", "-S []:all | [software]") {
        @Override
        public @NotNull DummyContainer container() {
            return new SoftwareContainer();
        }
    },
    verbose("Detailed console output");

    public final @NotNull Character shortRef;
    public final @NotNull String info;
    public final @Nullable String usage;

    Flag(@NotNull String info, @Nullable String usage) {
        this.shortRef = this.name().charAt(0);
        this.info = info;
        this.usage = usage;
    }

    Flag(@NotNull String info) {
        this.shortRef = this.name().charAt(0);
        this.info = info;
        this.usage = null;
    }

    Flag(@NotNull Character shotRef, @NotNull String info, @Nullable String usage) {
        this.shortRef = shotRef;
        this.info = info;
        this.usage = usage;
    }

    /**
     * Gets the flag container of the respective flag
     * @return a flag container, by default an empty container
     */
    public @NotNull DummyContainer container() {
        return new DummyContainer();
    }

    public static @NotNull Flag get(@NotNull Character ref) throws NotFoundException {
        for (Flag type : values()) {
            if (ref == type.shortRef) return type;
        }
        throw new NotFoundException(ref);
    }

    public static @NotNull Flag get(@NotNull String ref) throws NotFoundException {
        for (Flag flag : values()) if (flag.name().equalsIgnoreCase(ref)) return flag;
        throw new NotFoundException(ref);
    }

    public static class NotFoundException extends InputException {
        public NotFoundException(@NotNull Character ref) {
            super("Flag \"-"  + ref + "\" not found");
        }

        public NotFoundException(@NotNull String ref) {
            super("Flag \"--"  + ref + "\" not found");
        }
    }
}
