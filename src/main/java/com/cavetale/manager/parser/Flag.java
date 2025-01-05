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
        private final @NotNull CategoryContainer container = new CategoryContainer();

        @Override
        public @NotNull Container<?> container() {
            return this.container;
        }
    },

    debug("Debug console output"),
    force("Force execution"),
    help("Show command help"),
    installed('I', "Select installed"),
    interactive("Enter command prompt mode"),
    normal("Normal console output"),

    path('P', "Specify a file path", "-P <path>") {
        private final @NotNull PathContainer container = new PathContainer();

        @Override
        public @NotNull Container<?> container() {
            return this.container;
        }
    },

    plugin('p', "Specify plugins(s)", "-p []:all | [categories]") {
        private final @NotNull PluginContainer container = new PluginContainer();

        @Override
        public @NotNull Container<?> container() {
            return this.container;
        }
    },

    quiet("Reduced console output"),

    server('s', "Specify server(s)", "-s []:all | [servers]") {
        private final @NotNull ServerContainer container = new ServerContainer();

        @Override
        public @NotNull Container<?> container() {
            return this.container;
        }
    },

    software('S', "Specify server software", "-S []:all | [software]") {
        private final @NotNull SoftwareContainer container = new SoftwareContainer();

        @Override
        public @NotNull Container<?> container() {
            return this.container;
        }
    },

    verbose("Detailed console output");

    private final @NotNull Character ref;
    private final @NotNull String info;
    private final @Nullable String usage;

    private boolean selected = false;

    Flag(@NotNull String info, @Nullable String usage) {
        this.ref = this.name().charAt(0);
        this.info = info;
        this.usage = usage;
    }

    Flag(@NotNull String info) {
        this.ref = this.name().charAt(0);
        this.info = info;
        this.usage = null;
    }

    Flag(@NotNull Character shotRef, @NotNull String info) {
        this.ref = shotRef;
        this.info = info;
        this.usage = null;
    }

    Flag(@NotNull Character shotRef, @NotNull String info, @Nullable String usage) {
        this.ref = shotRef;
        this.info = info;
        this.usage = usage;
    }

    public char ref() {
        return this.ref;
    }

    public @NotNull String info() {
        return this.info;
    }

    public @Nullable String usage() {
        return this.usage;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public boolean isSelected() {
        return this.selected;
    }

    public @Nullable Container<?> container() {
        return null;
    }

    public static @NotNull Flag get(@NotNull Character ref) throws NotFoundException {
        for (Flag type : values()) {
            if (ref == type.ref) return type;
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
