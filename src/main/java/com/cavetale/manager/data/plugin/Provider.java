package com.cavetale.manager.data.plugin;

/**
 * Plugin provide interface, used for classes that can return a set of plugins
 */
public interface Provider {
    Plugin[] plugins();
}