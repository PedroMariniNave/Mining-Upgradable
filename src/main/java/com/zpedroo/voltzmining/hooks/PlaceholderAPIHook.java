package com.zpedroo.voltzmining.hooks;

import com.zpedroo.voltzmining.managers.DataManager;
import com.zpedroo.voltzmining.objects.player.PlayerData;
import com.zpedroo.voltzmining.utils.formatter.NumberFormatter;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public class PlaceholderAPIHook extends PlaceholderExpansion {

    private final Plugin plugin;

    public PlaceholderAPIHook(Plugin plugin) {
        this.plugin = plugin;
    }

    public @NotNull String getAuthor() {
        return plugin.getDescription().getAuthors().toString();
    }

    public @NotNull String getIdentifier() {
        return "mining";
    }

    public @NotNull String getVersion() {
        return plugin.getDescription().getVersion();
    }

    public @NotNull String onPlaceholderRequest(Player player, String identifier) {
        PlayerData data = DataManager.getInstance().getPlayerData(player);
        switch (identifier.toUpperCase()) {
            case "AVAILABLE_BLOCKS":
                return NumberFormatter.getInstance().format(data.getAvailableBlocks());
            case "BROKEN_BLOCKS":
                return NumberFormatter.getInstance().format(data.getBrokenBlocks());
            default:
                return "";
        }
    }
}