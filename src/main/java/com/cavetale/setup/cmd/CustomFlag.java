package com.cavetale.setup.cmd;

import link.l_pf.cmdlib.app.Flag;
import link.l_pf.cmdlib.app.container.Contents;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** Custom flags. */
public enum CustomFlag implements Flag {
    ALL('A', "Select all"),

    CATEGORY('C', "Select categor(y/ies)", ":all | [category]:select") {
        @Override
        public @NotNull Contents<?> init() {
            return new CategoryContents();
        }
    },

    INSTALLED('I', "Select installed"),

    PLUGIN('P', "Select plugins(s)", ":all | [plugin]:select") {
        @Override
        public @NotNull Contents<?> init() {
            return new PluginContents();
        }
    },

    SERVER('S', "Select server(s)", ":all | [server]:select") {
        @Override
        public @NotNull Contents<?> init() {
            return new ServerContents();
        }
    },

    SOFTWARE('Z', "Select server software", ":all | [software]:select") {
        @Override
        public @NotNull Contents<?> init() {
            return new SoftwareContents();
        }
    };

    // Package data into container from lib

    private final @NotNull Data data;

    CustomFlag(@NotNull Character ref, @NotNull String info) {
        this.data = new Data(this, ref, this.name(), info);
    }

    CustomFlag(@NotNull Character ref, @NotNull String info, @Nullable String usage) {
        this.data = new Data(this, ref, this.name(), info, usage);
    }

    @Override
    public @NotNull Data data() {
        return this.data;
    }
}
