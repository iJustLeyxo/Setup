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
    public boolean option(@NotNull String option) throws InputException {
        Software software = Software.get(option);
        if (this.contents.contains(software)) Console.log(Type.INFO, "Ignoring duplicate software \"" + option + "\n");
        this.contents.add(software);
        return true;
    }
}
