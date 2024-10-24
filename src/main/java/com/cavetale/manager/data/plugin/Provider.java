package com.cavetale.manager.data.plugin;

import java.util.Set;

/**
 * Plugin provide interface, used for classes that can return a set of plugins
 */
public interface Provider {
    Set<Plugin> plugins();
}