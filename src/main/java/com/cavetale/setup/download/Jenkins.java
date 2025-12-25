package com.cavetale.setup.download;

import com.cavetale.setup.data.Installable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** Jenkins download metadata. Used for temporary storage and modification of said data. */
public final class Jenkins {
    /** Default Jenkins server address. */
    public static final @NotNull String DEFAULT_SERVER = "cavetale.com/jenkins/";

    /** Default Maven group id parent (not the full group id, only the consistent part). */
    public static final @NotNull String DEFAULT_PARENT = "com.cavetale";

    /** Default maven artifact version. */
    public static final @NotNull String DEFAULT_VERSION = "0.1-SNAPSHOT";

    /** The installable metadata. */
    private final @NotNull Installable installable;

    /** The Jenkins download reference metadata. */
    private @NotNull String server, job, group, artifact, version;

    /**
     * Creates a new Jenkins default download metadata.
     * @param installable The installable to create the metadata from.
     */
    public Jenkins(@NotNull Installable installable) {
        this(installable, null);
    }

    /**
     * Creates a new Jenkins default download metadata.
     * @param installable The installable to create the metadata from.
     * @param mod The modification to apply to the metadata.
     */
    public Jenkins(@NotNull Installable installable, @Nullable Mod mod) {
        this.installable = installable;

        this.server = DEFAULT_SERVER;
        this.job = installable.displayName();
        this.group = DEFAULT_PARENT + "." + installable.displayName().toLowerCase();
        this.artifact = installable.displayName().toLowerCase();
        this.version = DEFAULT_VERSION;

        if (mod != null) mod.modify(this);
    }

    /**
     * Converts the Jenkins metadata to a download source, which is then further used for download handling.
     * @return The download source.
     */
    public @NotNull Source toSource() {
        return new Source(Link.of(
                "https://" + this.server + "job/" + this.job + "/lastSuccessfulBuild/" + this.group + "$" + this.artifact + "/artifact/" + this.group + "/" + this.artifact + "/" + this.version + "/" + this.artifact + "-" + this.version + ".jar"
        ), this.version);
    }

    /** Jenkins metadata modification information. */
    public interface Mod {
        /**
         * Modifies the provided Jenkins metadata.
         * @param jenkins The Jenkins metadata to modify.
         */
        void modify(@NotNull Jenkins jenkins);

        /**
         * Modifies the artifact group id parent.
         * @param parent The new parent to set.
         */
        record Parent(@NotNull String parent) implements Mod {
            @Override
            public void modify(@NotNull Jenkins jenkins) {
                jenkins.group = this.parent() + "." + jenkins.installable.displayName().toLowerCase();
            }
        }

        /**
         * Modifies the artifact version.
         * @param version The new version to set.
         */
        record Version(@NotNull String version) implements Mod {
            @Override
            public void modify(@NotNull Jenkins jenkins) {
                jenkins.version = this.version();
            }
        }

        /**
         * Modifies the artifact parent and version.
         * @param parent The new parent to set.
         * @param version The new version to set.
         */
        record ParentVersion(@NotNull String parent, @NotNull String version) implements Mod {
            @Override
            public void modify(@NotNull Jenkins jenkins) {
                jenkins.group = this.parent() + "." + jenkins.installable.displayName().toLowerCase();
                jenkins.version = this.version();
            }
        }

        /**
         * Modifies the Jenkins job and artifact id.
         * @param ref The new reference to set.
         */
        record Artifact(@NotNull String ref) implements Mod {
            @Override
            public void modify(@NotNull Jenkins jenkins) {
                jenkins.group = DEFAULT_PARENT + "." + this.ref();
                jenkins.artifact = this.ref();
            }
        }

        /**
         * Modifies the artifact id and the group id.
         * @param group The new group id to set.
         * @param artifact The new artifact id to set.
         */
        record GroupArtifact(@NotNull String group, @NotNull String artifact) implements Mod {
            @Override
            public void modify(@NotNull Jenkins jenkins) {
                jenkins.group = this.group();
                jenkins.artifact = this.artifact();
            }
        }

        // TODO: Find a better way of applying individual modifications instead of merging them to the required combinations.
    }
}
