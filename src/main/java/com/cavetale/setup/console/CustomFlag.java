package com.cavetale.setup.console;

import com.cavetale.setup.console.container.*;
import io.github.ijustleyxo.jclix.app.Flag;
import io.github.ijustleyxo.jclix.app.container.Contents;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Flags, used to specify flag properties
 */
public enum CustomFlag implements Flag {
    ALL('A', "Select all"),

    CATEGORY('C', "Specify categor(y/ies)", "-s []:all | [categories]") {
        @Override
        public @NotNull Contents<?> init() {
            return new CategoryContents();
        }
    },

    INSTALLED('I', "Select installed"),

    PLUGIN('P', "Specify plugins(s)", "-p []:all | [plugins]") {
        @Override
        public @NotNull Contents<?> init() {
            return new PluginContents();
        }
    },

    SERVER('S', "Specify server(s)", "-s []:all | [servers]") {
        @Override
        public @NotNull Contents<?> init() {
            return new ServerContents();
        }
    },

    SOFTWARE('Z', "Specify server software", "-S []:all | [software]") {
        @Override
        public @NotNull Contents<?> init() {
            return new SoftwareContents();
        }
    };

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
