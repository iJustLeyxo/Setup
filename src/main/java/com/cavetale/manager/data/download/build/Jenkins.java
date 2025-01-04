package com.cavetale.manager.data.download.build;

import com.cavetale.manager.data.download.Source;
import com.cavetale.manager.data.download.Ver;
import com.cavetale.manager.util.Util;
import org.jetbrains.annotations.NotNull;

public final class Jenkins extends Source {
    public Jenkins(@NotNull Job job, @NotNull Group group, @NotNull Ref ref, @NotNull Ver ver) {
        super(Util.uriOf("https://cavetale.com/jenkins/job/" + job + "/lastSuccessfulBuild/" +
                group + "$" + ref + "/artifact/" + group + "/" + ref + "/" + ver + "/" + ref + "-" + ver + ".jar"), ver);
    }
}
