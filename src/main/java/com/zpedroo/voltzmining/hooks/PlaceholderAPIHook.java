package com.zpedroo.voltzmining.hooks;

import com.zpedroo.voltzmining.managers.DataManager;
import com.zpedroo.voltzmining.objects.player.PlayerData;
import com.zpedroo.voltzmining.utils.formatter.NumberFormatter;
import com.zpedroo.voltzmining.utils.pickaxe.PickaxeUtils;
import com.zpedroo.voltzmining.utils.progress.ProgressConverter;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.math.BigInteger;

public class PlaceholderAPIHook extends PlaceholderExpansion {

    private final Plugin plugin;

    public PlaceholderAPIHook(Plugin plugin) {
        this.plugin = plugin;
    }

    @NotNull
    public String getAuthor() {
        return plugin.getDescription().getAuthors().toString();
    }

    @NotNull
    public String getIdentifier() {
        return "mining";
    }

    @NotNull
    public String getVersion() {
        return plugin.getDescription().getVersion();
    }

    public String onPlaceholderRequest(Player player, String identifier) {
        PlayerData data = DataManager.getInstance().getPlayerData(player);
        ItemStack item = player.getItemInHand();
        double experience = PickaxeUtils.getItemExperience(item);
        switch (identifier.toUpperCase()) {
            case "AVAILABLE_BLOCKS":
                return NumberFormatter.getInstance().format(data.getAvailableBlocks());
            case "BROKEN_BLOCKS":
                return NumberFormatter.getInstance().format(data.getBrokenBlocks());
            case "LEVEL":
                int level = PickaxeUtils.getItemLevel(item);
                return NumberFormatter.getInstance().formatThousand(level);
            case "QUALITY":
                int quality = PickaxeUtils.getItemQuality(item);
                return ProgressConverter.convertQuality(quality);
            case "PROGRESS":
                return ProgressConverter.convertExperience(experience);
            case "PERCENTAGE":
                return NumberFormatter.getInstance().formatDecimal(ProgressConverter.getPercentage(experience));
            case "POINTS":
                BigInteger pointsAmount = PickaxeUtils.getItemPoints(item);
                return NumberFormatter.getInstance().format(pointsAmount);
        }

        return null;
    }
}