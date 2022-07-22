package com.zpedroo.voltzmining.utils.config;

import com.zpedroo.voltzmining.objects.general.BlockProperties;
import com.zpedroo.voltzmining.utils.FileUtils;
import com.zpedroo.voltzmining.utils.loader.BlockLoader;

import java.util.List;

public class Blocks {

    public static final boolean FORTUNE_ENABLED = FileUtils.get().getBoolean(FileUtils.Files.CONFIG, "Blocks.fortune.enabled");

    public static final double FORTUNE_MULTIPLIER = FileUtils.get().getDouble(FileUtils.Files.CONFIG, "Blocks.fortune.multiplier");

    public static final List<String> DISABLED_WORLDS = FileUtils.get().getStringList(FileUtils.Files.CONFIG, "Blocks.disabled-worlds");

    public static final List<BlockProperties> LIST = BlockLoader.load(FileUtils.get().getStringList(FileUtils.Files.CONFIG, "Blocks.blocks"));
}