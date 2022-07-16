package com.zpedroo.voltzmining.commands;

import com.zpedroo.voltzmining.managers.DataManager;
import com.zpedroo.voltzmining.objects.mine.Mine;
import com.zpedroo.voltzmining.utils.config.Settings;
import com.zpedroo.voltzmining.utils.menus.Menus;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MiningCmd implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        Player player = sender instanceof Player ? (Player) sender : null;

        if (args.length > 0) {
            String mineName = null;
            Mine mine = null;
            switch (args[0].toUpperCase()) {
                case "TOP":
                    if (player != null) Menus.getInstance().openTopMenu(player);
                    return true;
                case "CREATE":
                    if (args.length < 2 || !sender.hasPermission(Settings.ADMIN_PERMISSION)) break;

                    mineName = args[1].toUpperCase();
                    mine = DataManager.getInstance().getMineByName(mineName);
                    if (mine == null) {
                        mine = new Mine(mineName);
                        DataManager.getInstance().getCache().getMines().put(mineName, mine);
                    }

                    Menus.getInstance().openEditMineMenu(player, mine);
                    return true;
                case "EDIT":
                    if (args.length < 2 || !sender.hasPermission(Settings.ADMIN_PERMISSION)) break;

                    mineName = args[1].toUpperCase();
                    mine = DataManager.getInstance().getMineByName(mineName);
                    if (mine == null) break;

                    Menus.getInstance().openEditMineMenu(player, mine);
                    return true;
            }
        }

        if (player != null) Menus.getInstance().openMainMenu(player);
        return false;
    }
}