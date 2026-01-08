package com.cavetale.setup.data;

/** Plugin provider interface, used for classes that can return a set of plugins. */
public interface Provider {
    Plugin[] plugins();
}
