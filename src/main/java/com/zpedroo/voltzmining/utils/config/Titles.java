package com.zpedroo.voltzmining.utils.config;

import com.zpedroo.voltzmining.utils.FileUtils;
import com.zpedroo.voltzmining.utils.color.Colorize;

public class Titles {

    public static final String[] PICKAXE_UPGRADE = new String[]{
            Colorize.getColored(FileUtils.get().getString(FileUtils.Files.CONFIG, "Titles.pickaxe-upgrade.title")),
            Colorize.getColored(FileUtils.get().getString(FileUtils.Files.CONFIG, "Titles.pickaxe-upgrade.subtitle"))
    };
}