package com.cavetale.manager.parser;

import com.cavetale.manager.parser.container.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Flags, used to specify flag properties
 */
public enum Flag {
    ALL("Select all"),
    CATEGORY("Specify categor(y/ies)", "-s []:all | [categories]") {
        @Override
        public @NotNull NotAContainer container() {
            return new CategoryContainer();
        }
    },
    DEFAULT("Normal console output"),
    FORCE("Force execution"),
    HELP("Show command help"),
    INTERACTIVE("Enter command prompt mode"),
    PATH('P', "Specify a file path", "-P <path>") {
        @Override
        public @NotNull NotAContainer container() {
            return new PathContainer();
        }
    },
    PLUGIN('p', "Specify plugins(s)", "-p []:all | [categories]") {
        @Override
        public @NotNull NotAContainer container() {
            return new PluginContainer();
        }
    },
    QUIET("Reduced console output"),
    SERVER('s', "Specify server(s)", "-s []:all | [servers]") {
        @Override
        public @NotNull NotAContainer container() {
            return new ServerContainer();
        }
    },
    SOFTWARE('S', "Specify server software", "-S []:all | [software]") {
        @Override
        public @NotNull NotAContainer container() {
            return new SoftwareContainer();
        }
    },
    VERBOSE("Detailed console output");

    public final @NotNull Character shortRef;
    public final @NotNull String longRef;
    public final @NotNull String info;
    public final @Nullable String usage;

    Flag(@NotNull String info, @Nullable String usage) {
        this.shortRef = this.name().toLowerCase().charAt(0);
        this.longRef = this.name().toLowerCase();
        this.info = info;
        this.usage = usage;
    }

    Flag(@NotNull String info) {
        this.shortRef = this.name().toLowerCase().charAt(0);
        this.longRef = this.name().toLowerCase();
        this.info = info;
        this.usage = null;
    }

    Flag(@NotNull Character shotRef, @NotNull String info, @Nullable String usage) {
        this.shortRef = shotRef;
        this.longRef = this.name().toLowerCase();
        this.info = info;
        this.usage = usage;
    }

    /**
     * Gets the flag container of the respective flag
     * @return a flag container, by default an empty container
     */
    public @NotNull NotAContainer container() {
        return new NotAContainer();
    }

    public static @NotNull Flag get(@NotNull Character ref) throws NotFoundException {
        for (Flag type : values()) {
            if (ref == type.shortRef) return type;
        }
        throw new NotFoundException(ref);
    }

    public static @NotNull Flag get(@NotNull String ref) throws NotFoundException {
        for (Flag type : values()) {
            if (ref.equalsIgnoreCase(type.longRef)) return type;
        }
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
