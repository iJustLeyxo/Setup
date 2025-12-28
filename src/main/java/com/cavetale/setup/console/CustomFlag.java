package com.cavetale.setup.console;

import com.cavetale.setup.console.container.CategoryContents;
import com.cavetale.setup.console.container.PluginContents;
import com.cavetale.setup.console.container.ServerContents;
import com.cavetale.setup.console.container.SoftwareContents;
import link.l_pf.cmdlib.app.Flag;
import link.l_pf.cmdlib.app.container.Contents;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** Custom console flags. */
public enum CustomFlag implements Flag {
    /** Selects everything. */
    ALL('A', "Select all"),

    /** Selects categories. */
    CATEGORY('C', "Specify categor(y/ies)", ":all | [category]:select") {
        @Override
        public @NotNull Contents<?> init() {
            return new CategoryContents();
        }
    },

    /** Selects installed items. */
    INSTALLED('I', "Select installed"),

    /** Selects plugins. */
    PLUGIN('P', "Specify plugins(s)", ":all | [plugin]:select") {
        @Override
        public @NotNull Contents<?> init() {
            return new PluginContents();
        }
    },

    /** Selects server configurations. */
    SERVER('S', "Specify server(s)", ":all | [server]:select") {
        @Override
        public @NotNull Contents<?> init() {
            return new ServerContents();
        }
    },

    /** Selects server software. */
    SOFTWARE('Z', "Specify server software", ":all | [software]:select") {
        @Override
        public @NotNull Contents<?> init() {
            return new SoftwareContents();
        }
    };

    /** Flag data container, for each flag. */
    private final @NotNull Data data;

    /**
     * Creates a new flag.
     * @param ref The short reference of the flag.
     * @param info The description of the flag.
     */
    CustomFlag(@NotNull Character ref, @NotNull String info) {
        this.data = new Data(this, ref, this.name(), info);
    }

    /**
     * Creates a new flag.
     * @param ref The short reference of the flag.
     * @param info The description of the flag.
     * @param usage The usage information of the flag.
     */
    CustomFlag(@NotNull Character ref, @NotNull String info, @Nullable String usage) {
        this.data = new Data(this, ref, this.name(), info, usage);
    }

    @Override
    public @NotNull Data data() {
        return this.data;
    }
}
