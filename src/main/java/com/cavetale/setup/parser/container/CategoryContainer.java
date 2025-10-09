package com.cavetale.setup.parser.container;

import com.cavetale.setup.data.plugin.Category;
import com.cavetale.setup.parser.InputException;
import com.cavetale.setup.util.console.Console;
import com.cavetale.setup.util.console.Type;
import org.jetbrains.annotations.NotNull;

/**
 * Category container, used to store categories from a category flag
 */
public final class CategoryContainer extends SetContainer<Category> {
    @Override
    public boolean add(@NotNull String arg) throws InputException {
        Category category = Category.get(arg);
        if (this.contents.contains(category)) Console.log(Type.INFO, "Ignoring duplicate category \"" + arg + "\n");
        else this.contents.add(category);
        return true;
    }
}
