package com.cavetale.setup;

import com.cavetale.setup.console.CustomCommand;
import com.cavetale.setup.console.CustomFlag;
import io.github.ijustleyxo.jclix.app.App;
import org.jetbrains.annotations.NotNull;

import java.io.File;

/**
 * Cavetale installation manager, used to manage plugins and server software for testing servers
 */
public final class Setup {
    public static final @NotNull File TEMP = new File(".setup/");

    public static void main(String[] args) {
        new App(CustomCommand.values(), CustomFlag.values(), args);
    }

    // TODO: Resource pack download (with hash)
}