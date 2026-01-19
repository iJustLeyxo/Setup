package com.cavetale.setup.cmd;

import com.cavetale.setup.data.Plugin;
import com.cavetale.setup.data.PluginCategory;
import com.cavetale.setup.data.ServerPreset;
import com.cavetale.setup.data.ServerSoftware;
import link.l_pf.cmdlib.app.Flag;
import link.l_pf.cmdlib.app.container.Contents;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** Custom flags for the command line app. */
public enum CustomFlag implements Flag {
    ALL('A', "Select all"),

    CATEGORIES('C', "Select categor(y/ies)", "[category]:select") {
        @Override
        public @NotNull Contents<?> init() {
            return new SelectableContents<>(PluginCategory::get);
        }
    },

    INSTALLED('I', "Select installed"),

    PLUGINS('P', "Select plugins(s)", "[plugin]:select") {
        @Override
        public @NotNull Contents<?> init() {
            return new SelectableContents<>(Plugin::get);
        }
    },

    SERVERS('S', "Select server(s)", "[server]:select") {
        @Override
        public @NotNull Contents<?> init() {
            return new SelectableContents<>(ServerPreset::get);
        }
    },

    SOFTWARE('Z', "Select server software", "[software]:select") {
        @Override
        public @NotNull Contents<?> init() {
            return new SelectableContents<>(ServerSoftware::get);
        }
    };

    // As per library specifications...

    private final @NotNull Data data;

    CustomFlag(
            @NotNull Character ref,
            @NotNull String info
    ) {
        this.data = new Data(this, ref, this.name(), info);
    }

    CustomFlag(
            @NotNull Character ref,
            @NotNull String info,
            @Nullable String usage
    ) {
        this.data = new Data(this, ref, this.name(), info, usage);
    }

    @Override
    public @NotNull Data data() {
        return this.data;
    }
}
