package com.cavetale.setup.console.container;

import com.cavetale.setup.data.plugin.Category;
import link.l_pf.cmdlib.app.container.SetContents;
import org.jetbrains.annotations.NotNull;

import static link.l_pf.cmdlib.io.Console.SYSIO;

/** Used for storing categories in commands and flags. */
public final class CategoryContents extends SetContents<Category> {
    @Override
    public @NotNull Boolean add(@NotNull String arg) throws Category.NotFoundException {
        Category category = Category.get(arg);
        if (this.contents.contains(category)) SYSIO.warn("Ignoring duplicate category \"" + arg + "\n");
        else this.contents.add(category);
        return true;
    }
}
