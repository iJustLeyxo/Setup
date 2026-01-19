package com.cavetale.setup.data;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.util.*;

import static java.lang.Math.min;
import static link.l_pf.cmdlib.shell.Shell.STDIO;

/** Base for installable entries. */
public abstract class Source extends Entry {
    /** Temp directory. */
    private static final @NotNull File TEMP = new File("setup");
    /** Default Jenkins version. */
    private static final @NotNull String DEFAULT_VERSION = "0.1-SNAPSHOT";
    /** Default Jenkins server. */
    private static final @NotNull String DEFAULT_SERVER = "cavetale.com/jenkins/";
    /** Default Jenkins parent. */
    private static final @NotNull String DEFAULT_PARENT = "com.cavetale";

    /** Version number. */
    public final @NotNull String ver;
    /** Deprecation status. */
    public final boolean deprecated;
    /** Source uri. */
    public final @NotNull URI uri;

    /** Installations. */
    private @Nullable List<Install> installations = null;

    /** Create from raw data. */
    protected Source(@NotNull Raw raw) {
        super(raw);
        ver = raw.ver == null ? DEFAULT_VERSION : raw.ver;
        deprecated = raw.deprecated == null || raw.deprecated;

        if (raw.uri != null) uri = uriOf(raw.uri);
        else { // Jenkins overrides for default values
            String
                    build = "https://" + (raw.server != null ? raw.server : DEFAULT_SERVER) +
                            "job/" + (raw.job == null ? ref : raw.job) + "/lastSuccessfulBuild/",
                    module = raw.group != null ? raw.group :
                            (raw.parent != null ? raw.parent : DEFAULT_PARENT) + "." +
                                    (raw.child != null ? raw.child :
                                            (raw.name != null ? raw.name :
                                                    ref.toLowerCase())),
                    artifact = raw.artifact != null ? raw.artifact :
                            (raw.name == null ? ref.toLowerCase() : raw.name),
                    file = artifact + "-" + ver + ".jar";
            uri = uriOf(raw.lib ?
                    build + "artifact/" + module + "/build/libs/" + file :
                    build + module + "$" + artifact + "/" + "artifact/" +
                            module + "/" + artifact + "/" + ver + "/" + file);
        }
    }

    @Override
    public void reset() {
        super.reset();
        this.installations = null;
    }

    @Override
    public final boolean isInstalled() {
        if (installations == null) load();
        return !installations.isEmpty();
    }

    /** Loads installations for this entry type. */
    protected abstract void load();

    /** Installation directory. */
    protected abstract @NotNull File dir();

    /** Installations. */
    public final @NotNull List<Install> installations() {
        if (installations == null) load();
        return installations;
    }

    /** Installation data. */
    public record Install(
        @NotNull String name,
        @NotNull Type type
    ) {
        /** Installation type. */
        public enum Type {
            /** Normal file. */
            NORMAL,
            /** Symbolic link. */
            LINK
        }
    }



    /** Create URI from string. */
    private static @NotNull URI uriOf(@NotNull String link) {
        try {
            return new URI(link);
        } catch (URISyntaxException e) {
            throw new RuntimeException("Failed to convert \"" + link + "\" to URI", e);
        }
    }

    /** Raw data. */
    protected static final class Raw extends Entry.Raw {
        public String ver = null;
        public Boolean deprecated = null;
        public String uri = null;
        public String server = null;
        public String job = null;
        public boolean lib = false;
        public String group = null;
        public String parent = null;
        public String child = null;
        public String name = null;
        public String artifact = null;
    }

    /** Gets the matching source. */
    protected static <S extends Source> @NotNull S get(
        @NotNull File file,
        @NotNull S @NotNull [] sources
    ) {
        String ref = file.getName().toLowerCase();
        if (!file.getPath().endsWith(".jar")) throw new UntrackedException(ref);
        int verStart = ref.indexOf("-");
        if (verStart < 0) verStart = ref.length() - 1;
        int extStart = ref.indexOf(".");
        if (extStart < 0) extStart = ref.length() - 1;
        return get(ref.substring(0, min(verStart, extStart)), sources);
    }

    /** Thrown if a file is not a source file. */
    public static final class UntrackedException extends RuntimeException {
        public UntrackedException(@NotNull String file) {
            super("Untracked file \"" + file + "\"");
        }
    }

    /** Load installations. */
    protected static <S extends Source> @NotNull Unknown load(
        @NotNull File folder,
        @NotNull S @NotNull [] sources
    ) {
        STDIO.debug("Reloading installations");
        Unknown unknown = new Unknown();
        boolean _ = folder.mkdirs();
        File[] files = folder.listFiles();
        if (files == null) throw new RuntimeException(
            "Installs location \"" + folder + "\" is not a directory");
        for (S s : sources)
            ((Source) s).installations = new ArrayList<>();

        for (File f : files) {
            if (!f.isFile() || f.getName().equals("Setup.jar")) continue;

            try {
                Source s = get(f, sources);
                assert s.installations != null;
                if (Files.isSymbolicLink(f.toPath())) s.installations
                        .add(new Install(f.getName(), Install.Type.LINK));
                else s.installations
                        .add(new Install(f.getName(), Install.Type.NORMAL));
            } catch (UntrackedException _) {
            } catch (UnknownException e) {
                if (Files.isSymbolicLink(f.toPath()))
                    unknown.linked.add(f.getName());
                else unknown.unknown.add(f.getName());
                STDIO.warn("Unknown file \"" + f.getName() + "\"");
            }
        }

        return unknown;
    }

    /** Summary of unknown installations. */
    public record Unknown(
        @NotNull List<String> unknown,
        @NotNull List<String> linked
    ) {
        /** Empty unknown. */
        public Unknown() {
            this(new ArrayList<>(), new ArrayList<>());
        }
    }

    /** Install sources. */
    @SafeVarargs
    public static void install(
            @NotNull Collection<? extends Source> @NotNull ... sources
    ) {
        Downloads downloads = download(union(sources));
        if (downloads.success ||
                STDIO.getConfirmation("Some downloads failed. Continue anyway"))
            install(downloads);
        clearTempDir();
    }

    /** Update sources. */
    @SafeVarargs
    public static void update(
        @NotNull Collection<? extends Source> @NotNull ... sources
    ) {
        Collection<Source> all = union(sources);
        Downloads downloads = download(all);
        if (downloads.success ||
                STDIO.getConfirmation("Some downloads failed. Continue anyway"))
            if (uninstall(all) ||
                    STDIO.getConfirmation("Some uninstalls failed. Continue anyway"))
                install(downloads);
        clearTempDir();
    }

    /** Uninstall sources. */
    @SafeVarargs
    public static void uninstall(
            @NotNull Collection<? extends Source> @NotNull ... sources
    ) {
        uninstall(union(sources));
    }

    /** Downloads sources to temp dir. */
    private static @NotNull Downloads download(
            @NotNull Collection<Source> sources
    ) {
        boolean _ = TEMP.mkdirs();
        if (!TEMP.isDirectory())
            throw new RuntimeException("Failed to init download dir \"" + TEMP + "\"");
        STDIO.openInfo("Downloading ", sources.size(), " element(s)");
        List<Downloads.DownloadFile> files = new ArrayList<>();
        boolean success = true;

        for (Source s : sources) {
            STDIO.openDebug("Downloading ", s.ref);
            File file;
            do file = new File(TEMP, UUID.randomUUID().toString());
            while (file.exists());

            try {
                ReadableByteChannel byteChannel =
                        Channels.newChannel(s.uri.toURL().openStream());
                FileOutputStream outputStream = new FileOutputStream(file);
                outputStream.getChannel()
                        .transferFrom(byteChannel, 0, Long.MAX_VALUE);

                files.add(new Downloads.DownloadFile(s, file));
                STDIO.closeDebugDone();
            } catch (IOException e) {
                success = false;
                STDIO.closeErr(e, "failed");
            }
        }

        if (success) STDIO.closeInfoDone();
        else STDIO.closeErr("Downloading some elements failed");
        return new Downloads(success, files);
    }

    /** Uninstalls sources. */
    private static boolean uninstall(
            @NotNull Collection<Source> sources
    ) {
        STDIO.openInfo("Uninstalling ", sources.size(), " element(s)");
        boolean success = true;

        for (Source s : sources)
            for (Install n : new ArrayList<>(s.installations())) {
                STDIO.openDebug("Uninstalling ", n.name);
                File f = new File(s.dir(), n.name);

                if (f.exists()) {
                    if (f.delete()) {
                        s.installations().remove(n);
                        STDIO.closeDebugDone();
                    } else {
                        success = false;
                        STDIO.closeErr("failed (failed to delete file)");
                    }
                } else STDIO.closeWarn("skipped (file no longer exists)");
            }

        if (success) STDIO.closeInfoDone();
        else STDIO.closeErr("Uninstalling some elements failed");
        return success;
    }

    /** Installs sources from temp dir to final dir. */
    private static void install(
        @NotNull Source.Downloads downloads
    ) {
        STDIO.openInfo("Installing ", downloads.files.size(), " element(s)");
        boolean success = true;

        for (Downloads.DownloadFile f : downloads.files) {
            STDIO.openDebug("Installing ", f.source.ref);
            String name = f.source.ref + "-" + f.source.ver + ".jar";

            try {
                f.source.installations().add(new Install(name, Install.Type.NORMAL));
                Files.copy(f.file.toPath(), new File(f.source.dir(), name).toPath());
                STDIO.closeDebugDone();
            } catch (IOException e) {
                success = false;
                STDIO.closeErr(e, "failed");
            }
        }

        if (success) STDIO.closeInfoDone();
        else STDIO.closeErr("Installing some elements failed");
    }

    /** Temp files. */
    private record Downloads(
        boolean success,
        @NotNull List<DownloadFile> files
    ) {
        /** Temp file. */
        private record DownloadFile(
            @NotNull Source source,
            @NotNull File file
        ) { }
    }

    /** Clears the temp dir. */
    public static void clearTempDir() {
        File[] files = TEMP.listFiles();
        if (files == null) return;
        for (File f : files) f.delete();
    }

    /** Unions collections. */
    @SafeVarargs
    private static <E> @NotNull  Collection<E> union(
            @NotNull Collection<? extends E> @NotNull ... collections
    ) {
        List<E> list = new ArrayList<>();
        for (Collection<? extends E> c : collections) list.addAll(c);
        return list;
    }

    /** Queries elements by selection and installation state. */
    protected static <S extends Source> @NotNull Set<S> selected(
            @Nullable Boolean selected,
            @Nullable Boolean installed,
            @NotNull S @NotNull [] sources
    ) {
        Set<S> result = new HashSet<>();
        for (S s : sources)
            if ((selected == null || s.isSelected() == selected) &&
                    (installed == null || s.isInstalled() == installed))
                result.add(s);
        return result;
    }
}
