package com.cavetale.setup.data;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

/** Test integrity of JSON data. */
final class DataTest {
    @Test
    void plugins() {
        assertDoesNotThrow(() -> Plugin.get());
    }

    @Test
    void pluginCategories() {
        assertDoesNotThrow(() -> PluginCategory.get());
    }

    @Test
    void serverPresets() {
        assertDoesNotThrow(() -> ServerPreset.get());
    }

    @Test
    void serverSoftware() {
        assertDoesNotThrow(() -> ServerSoftware.get());
    }
}
