package com.cavetale.manager.data.download;

import com.cavetale.manager.data.download.build.Artifact;
import com.cavetale.manager.data.download.build.Group;
import com.cavetale.manager.data.download.build.Parent;
import com.cavetale.manager.data.download.build.Ref;
import com.cavetale.manager.util.Util;
import org.jetbrains.annotations.NotNull;

import java.net.URI;

/**
 * Download source class for formatting download urls from different sources
 */
public abstract class Source {
    private final @NotNull URI uri;
    private final @NotNull Ver ver;

    public Source(@NotNull URI uri, @NotNull Ver ver) {
        this.uri = uri;
        this.ver = ver;
    }

    public @NotNull Ver ver() {
        return this.ver;
    }

    public @NotNull URI uri() {
        return this.uri;
    }

    public static class Other extends Source {
        public Other(@NotNull URI uri, @NotNull Ver ver) {
            super(uri, ver);
        }
    }
}
