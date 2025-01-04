package com.cavetale.manager.data;

import com.cavetale.manager.util.Util;
import org.jetbrains.annotations.NotNull;

import java.net.URI;

/**
 * Download source class for formatting download urls from different sources
 */
public abstract class Source {
    public final @NotNull String version;
    public final @NotNull URI uri;

    public Source(@NotNull String version, @NotNull URI uri) {
        this.version = version;
        this.uri = uri;
    }

    public static class Jenkins extends Source {
        public Jenkins(@NotNull String jobId, @NotNull String groupId, @NotNull String artifactId, @NotNull String version) {
            super(version, Util.uriOf("https://cavetale.com/jenkins/job/" + jobId +
                    "/lastSuccessfulBuild/" + groupId + "$" + artifactId + "/artifact/" + groupId +
                    "/" + artifactId + "/" + version + "/" + artifactId + "-" + version + ".jar"));
        }
    }

    public static class Other extends Source {
        public Other(@NotNull URI uri, @NotNull String version) {
            super(version, uri);
        }
    }
}
