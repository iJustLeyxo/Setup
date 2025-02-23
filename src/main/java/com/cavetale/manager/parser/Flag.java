package com.cavetale.manager.parser;

import com.cavetale.manager.parser.container.*;
import com.cavetale.manager.util.Util;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Flags, used to specify flag properties
 */
public enum Flag {
    ALL('a', "Select all"),

    CATEGORY('c', "Specify categor(y/ies)", "-s []:all | [categories]") {
        private final @NotNull CategoryContainer container = new CategoryContainer();

        @Override
        public @NotNull Container<?> container() {
            return this.container;
        }
    },

    COMMAND('C', "Filter by commands"),
    DEBUG('d', "Debug console output"),
    EXECUTE('x', "Run the command after the flag"),
    ERROR('e', "Detailed error output"),
    FLAG('f', "Filter by flags"),
    HELP('h', "Show command help"),
    INSTALLED('I', "Select installed"),
    INTERACTIVE('i', "Enter command prompt mode"),
    NORMAL('n', "Normal console output"),

    PLUGIN('p', "Specify plugins(s)", "-p []:all | [plugins]") {
        private final @NotNull PluginContainer container = new PluginContainer();

        @Override
        public @NotNull Container<?> container() {
            return this.container;
        }
    },

    QUIET('q', "Reduced console output"),

    SERVER('s', "Specify server(s)", "-s []:all | [servers]") {
        private final @NotNull ServerContainer container = new ServerContainer();

        @Override
        public @NotNull Container<?> container() {
            return this.container;
        }
    },

    SOFTWARE('S', "Specify server software", "-S []:all | [software]") {
        private final @NotNull SoftwareContainer container = new SoftwareContainer();

        @Override
        public @NotNull Container<?> container() {
            return this.container;
        }
    },

    VERBOSE('v', "Detailed console output");

    private final @NotNull Character ref;
    private final @NotNull String name;
    private final @NotNull String info;
    private final @Nullable String usage;

    private boolean selected = false;

    Flag(@NotNull Character shotRef, @NotNull String info) {
        this.ref = shotRef;
        this.name = Util.capsToCamel(this.name());
        this.info = info;
        this.usage = null;
    }

    Flag(@NotNull Character shotRef, @NotNull String info, @Nullable String usage) {
        this.ref = shotRef;
        this.name = Util.capsToCamel(this.name());
        this.info = info;
        this.usage = usage;
    }

    public char ref() {
        return this.ref;
    }

    public @NotNull String displayName() {
        return this.name;
    }

    @Override
    public @NotNull String toString() {
        return this.displayName();
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
        for (Flag flag : values()) if (flag.displayName().equalsIgnoreCase(ref)) return flag;
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
