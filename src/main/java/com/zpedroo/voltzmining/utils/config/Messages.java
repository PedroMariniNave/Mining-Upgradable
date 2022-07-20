package com.zpedroo.voltzmining.utils.config;

import com.zpedroo.voltzmining.utils.FileUtils;
import com.zpedroo.voltzmining.utils.color.Colorize;

import java.util.List;

public class Messages {

    public static final String INVALID_BLOCK_MATERIAL = Colorize.getColored(FileUtils.get().getString(FileUtils.Files.CONFIG, "Messages.invalid-block-material"));

    public static final String INVALID_REGION = Colorize.getColored(FileUtils.get().getString(FileUtils.Files.CONFIG, "Messages.invalid-region"));

    public static final String INVALID_AMOUNT = Colorize.getColored(FileUtils.get().getString(FileUtils.Files.CONFIG, "Messages.invalid-amount"));

    public static final String COOLDOWN = Colorize.getColored(FileUtils.get().getString(FileUtils.Files.CONFIG, "Messages.cooldown"));

    public static final List<String> REWARD_DISAPPEAR = Colorize.getColored(FileUtils.get().getStringList(FileUtils.Files.CONFIG, "Messages.reward-disappear"));

    public static final List<String> CHOOSE_BLOCK_PERCENTAGE = Colorize.getColored(FileUtils.get().getStringList(FileUtils.Files.CONFIG, "Messages.choose-block-percentage"));

    public static final List<String> CHOOSE_RESET_TIME = Colorize.getColored(FileUtils.get().getStringList(FileUtils.Files.CONFIG, "Messages.choose-reset-time"));
}