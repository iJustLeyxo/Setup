package com.cavetale.setup.console.container;

import com.cavetale.setup.data.plugin.Category;
import org.jetbrains.annotations.NotNull;

import static io.github.ijustleyxo.jclix.io.Console.SYSIO;

/**
 * Category container, used to store categories from a category flag
 */
public final class CategoryContents extends SetContents<Category> {
    @Override
    public @NotNull Boolean add(@NotNull String arg) throws Category.NotFoundException {
        Category category = Category.get(arg);
        if (this.contents.contains(category)) SYSIO.warn("Ignoring duplicate category \"" + arg + "\n");
        else this.contents.add(category);
        return true;
    }
}
