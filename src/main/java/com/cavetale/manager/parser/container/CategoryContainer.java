package com.cavetale.manager.parser.container;

import com.cavetale.manager.data.plugin.Category;
import com.cavetale.manager.parser.InputException;
import com.cavetale.manager.util.console.Console;
import com.cavetale.manager.util.console.Type;
import org.jetbrains.annotations.NotNull;

/**
 * Category container, used to store categories from a category flag
 */
public final class CategoryContainer extends SetContainer<Category> {
    @Override
    public boolean option(@NotNull String option) throws InputException {
        Category category = Category.get(option);
        if (this.contents.contains(category)) Console.log(Type.INFO, "Ignoring duplicate category \"" + option + "\n");
        this.contents.add(category);
        return true;
    }
}
