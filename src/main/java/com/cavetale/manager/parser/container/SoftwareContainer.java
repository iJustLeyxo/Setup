package com.cavetale.manager.parser.container;

import com.cavetale.manager.data.server.Software;
import com.cavetale.manager.parser.InputException;
import com.cavetale.manager.util.console.Console;
import com.cavetale.manager.util.console.Type;
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
