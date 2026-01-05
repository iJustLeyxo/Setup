package com.cavetale.setup.download;

import com.cavetale.setup.data.Installable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record Jenkins(
        @Nullable String server,
        @Nullable String job,
        @Nullable String group,
        @Nullable String groupParent,
        @Nullable String groupArtifact,
        @Nullable String artifact,
        @Nullable String version
) {
    private static final @NotNull String DEFAULT_SERVER = "cavetale.com/jenkins/";
    private static final @NotNull String DEFAULT_PARENT = "com.cavetale";
    private static final @NotNull String DEFAULT_VERSION = "0.1-SNAPSHOT";

    private Jenkins() {
        this(
                null,
                null,
                null,
                null,
                null,
                null,
                null
        );
    }

    /** Create a new jenkins modification. */
    public static @NotNull Jenkins jenkins() {
        return new Jenkins();
    }

    /** Modify the Jenkins parent. */
    public @NotNull Jenkins parent(@NotNull String parent) {
        if (group != null || groupParent != null)
            throw new IllegalStateException("Jenkins parent was already modified");

        return new Jenkins(
                server,
                job,
                null,
                parent,
                groupArtifact,
                artifact,
                version
        );
    }

    /** Modify the Jenkins version. */
    public @NotNull Jenkins version(@NotNull String version) {
        if (this.version != null)
            throw new IllegalStateException("Jenkins version was already modified");

        return new Jenkins(
                server,
                job,
                group,
                groupParent,
                groupArtifact,
                artifact,
                version
        );
    }

    /** Modify the Jenkins group. */
    public @NotNull Jenkins group(@NotNull String group) {
        if (this.group != null || groupParent != null)
            throw new IllegalStateException("Jenkins group was already modified");

        return new Jenkins(
                server,
                job,
                group,
                null,
                null,
                artifact,
                version
        );
    }

    /** Modify the Jenkins artifact. */
    public @NotNull Jenkins artifact(@NotNull String artifact) {
        if (groupArtifact != null || this.artifact != null)
            throw new IllegalStateException("Jenkins artifact was already modified");

        return new Jenkins(
                server,
                job,
                group,
                groupParent,
                group == null ? artifact : null,
                artifact,
                version
        );
    }

    /*
     * Finalization:
     * Combine the requested modifications with default settings and
     * settings of the installable.
     */

    private record Final(
            @NotNull String server,
            @NotNull String job,
            @NotNull String group,
            @NotNull String artifact,
            @NotNull String version
    ) {
    }

    private @NotNull Final finalize(@NotNull Installable installable) {
        return new Final(
                server == null ? DEFAULT_SERVER : server,
                job == null ? installable.displayName() : job,
                group == null ? // Group info is compacted
                        (groupParent == null ? DEFAULT_PARENT : groupParent) + "." +
                                (groupArtifact == null ? installable.displayName().toLowerCase() : groupArtifact) :
                        group,
                artifact == null ? installable.displayName().toLowerCase() : artifact,
                version == null ? DEFAULT_VERSION : version
        );
    }

    /** Convert the Jenkins modifications to a download source. */
    public @NotNull Source source(@NotNull Installable installable) {
        Final fin = finalize(installable);

        return Source.link(
                "https://" + fin.server + "job/" + fin.job + "/lastSuccessfulBuild/" +
                        fin.group + "$" + fin.artifact + "/artifact/" +
                        fin.group + "/" + fin.artifact + "/" + fin.version + "/" +
                        fin.artifact + "-" + fin.version + ".jar",
                fin.version);
    }
}
