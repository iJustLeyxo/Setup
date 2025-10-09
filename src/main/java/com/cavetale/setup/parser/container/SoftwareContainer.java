package com.cavetale.setup.parser.container;

import com.cavetale.setup.data.server.Software;
import com.cavetale.setup.parser.InputException;
import com.cavetale.setup.util.console.Console;
import com.cavetale.setup.util.console.Type;
import org.jetbrains.annotations.NotNull;

/**
 * Software container, used to store software from a software flag
 */
public final class SoftwareContainer extends SetContainer<Software> {
    @Override
    public boolean add(@NotNull String arg) throws InputException {
        Software software = Software.get(arg);
        if (this.contents.contains(software)) Console.log(Type.INFO, "Ignoring duplicate software \"" + arg + "\n");
        else this.contents.add(software);
        return true;
    }
}
