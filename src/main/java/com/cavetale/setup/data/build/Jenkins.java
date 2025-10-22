package com.cavetale.setup.data.build;

import com.cavetale.setup.data.Installable;
import com.cavetale.setup.download.Source;
import com.cavetale.setup.download.Ver;
import com.cavetale.setup.util.Util;
import org.jetbrains.annotations.NotNull;

public final class Jenkins extends Source {
    public Jenkins(@NotNull Job job, @NotNull Group group, @NotNull Ref ref, @NotNull Ver ver) {
        super(Installable.uriOf("https://cavetale.com/jenkins/job/" + job + "/lastSuccessfulBuild/" +
                group + "$" + ref + "/artifact/" + group + "/" + ref + "/" + ver + "/" + ref + "-" + ver + ".jar"), ver);
    }
}
