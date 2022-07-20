package com.zpedroo.voltzmining.utils.config;

import com.zpedroo.multieconomy.api.CurrencyAPI;
import com.zpedroo.multieconomy.objects.general.Currency;
import com.zpedroo.voltzmining.utils.FileUtils;
import com.zpedroo.voltzmining.utils.color.Colorize;

import java.util.List;

public class Settings {

    public static final String MAIN_COMMAND = FileUtils.get().getString(FileUtils.Files.CONFIG, "Settings.commands.main.command");

    public static final List<String> MAIN_COMMAND_ALIASES = FileUtils.get().getStringList(FileUtils.Files.CONFIG, "Settings.commands.main.aliases");

    public static final String PICKAXE_COMMAND = FileUtils.get().getString(FileUtils.Files.CONFIG, "Settings.commands.pickaxe.command");

    public static final List<String> PICKAXE_COMMAND_ALIASES = FileUtils.get().getStringList(FileUtils.Files.CONFIG, "Settings.commands.pickaxe.aliases");

    public static final String ADMIN_PERMISSION = FileUtils.get().getString(FileUtils.Files.CONFIG, "Settings.admin-permission");

    public static final int PICKAXE_PICKUP_COOLDOWN = FileUtils.get().getInt(FileUtils.Files.CONFIG, "Settings.pickaxe-pickup-cooldown");

    public static final int REWARD_DISAPPEAR_TIME = FileUtils.get().getInt(FileUtils.Files.CONFIG, "Settings.reward-disappear-time");

    public static final long SAVE_INTERVAL = FileUtils.get().getLong(FileUtils.Files.CONFIG, "Settings.save-interval");

    public static final Currency QUALITY_CURRENCY = CurrencyAPI.getCurrency(FileUtils.get().getString(FileUtils.Files.CONFIG, "Settings.quality-currency"));

    public static final String REWARD_ENTITY_NAME = Colorize.getColored(FileUtils.get().getString(FileUtils.Files.CONFIG, "Settings.reward-entity-name"));
}