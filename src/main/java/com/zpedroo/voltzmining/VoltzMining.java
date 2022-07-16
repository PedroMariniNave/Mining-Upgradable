package com.zpedroo.voltzmining;

import com.zpedroo.voltzmining.commands.MiningCmd;
import com.zpedroo.voltzmining.commands.PickaxeCmd;
import com.zpedroo.voltzmining.hooks.PlaceholderAPIHook;
import com.zpedroo.voltzmining.listeners.*;
import com.zpedroo.voltzmining.managers.DataManager;
import com.zpedroo.voltzmining.mysql.DBConnection;
import com.zpedroo.voltzmining.tasks.SaveTask;
import com.zpedroo.voltzmining.utils.FileUtils;
import com.zpedroo.voltzmining.utils.cooldown.Cooldown;
import com.zpedroo.voltzmining.utils.formatter.NumberFormatter;
import com.zpedroo.voltzmining.utils.menus.Menus;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandMap;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.List;
import java.util.logging.Level;

import static com.zpedroo.voltzmining.utils.config.Settings.*;

public class VoltzMining extends JavaPlugin {

    private static VoltzMining instance;
    public static VoltzMining get() { return instance; }

    public void onEnable() {
        instance = this;
        new FileUtils(this);

        if (!isMySQLEnabled(getConfig())) {
            getLogger().log(Level.SEVERE, "MySQL are disabled! You need to enable it.");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        new DBConnection(getConfig());
        new NumberFormatter(getConfig());
        new DataManager();
        new Menus();
        new Cooldown();
        new SaveTask(this);

        registerHooks();
        registerListeners();
        registerCommand(MAIN_COMMAND, MAIN_COMMAND_ALIASES, new MiningCmd());
        registerCommand(PICKAXE_COMMAND, PICKAXE_COMMAND_ALIASES, new PickaxeCmd());
    }

    public void onDisable() {
        if (!isMySQLEnabled(getConfig())) return;

        try {
            DataManager.getInstance().saveAllPlayersData();
            DataManager.getInstance().saveAllMinesData();
            DBConnection.getInstance().closeConnection();
        } catch (Exception ex) {
            getLogger().log(Level.SEVERE, "An error occurred while trying to save data!");
        }
    }

    private void registerHooks() {
        if (getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new PlaceholderAPIHook(this).register();
        }
    }

    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new EditMineListeners(), this);
        getServer().getPluginManager().registerEvents(new EXPListeners(), this);
        getServer().getPluginManager().registerEvents(new MiningListeners(), this);
        getServer().getPluginManager().registerEvents(new PlayerGeneralListeners(), this);
        getServer().getPluginManager().registerEvents(new PointsListeners(), this);
    }

    private void registerCommand(String command, List<String> aliases, CommandExecutor executor) {
        try {
            Constructor<PluginCommand> constructor = PluginCommand.class.getDeclaredConstructor(String.class, Plugin.class);
            constructor.setAccessible(true);

            PluginCommand pluginCmd = constructor.newInstance(command, this);
            pluginCmd.setAliases(aliases);
            pluginCmd.setExecutor(executor);

            Field field = Bukkit.getPluginManager().getClass().getDeclaredField("commandMap");
            field.setAccessible(true);
            CommandMap commandMap = (CommandMap) field.get(Bukkit.getPluginManager());
            commandMap.register(getName().toLowerCase(), pluginCmd);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private Boolean isMySQLEnabled(FileConfiguration file) {
        if (!file.contains("MySQL.enabled")) return false;

        return file.getBoolean("MySQL.enabled");
    }
}