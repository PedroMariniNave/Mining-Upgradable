package com.zpedroo.voltzmining.listeners;

import com.zpedroo.voltzmining.VoltzMining;
import com.zpedroo.voltzmining.enums.EditType;
import com.zpedroo.voltzmining.objects.mine.EditMine;
import com.zpedroo.voltzmining.objects.mine.Mine;
import com.zpedroo.voltzmining.utils.config.Messages;
import com.zpedroo.voltzmining.utils.menus.Menus;
import lombok.Data;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class EditMineListeners implements Listener {

    private static final Map<Player, EditMine> editingPlayers = new HashMap<>(2);

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onChat(AsyncPlayerChatEvent event) {
        if (!editingPlayers.containsKey(event.getPlayer())) return;

        event.setCancelled(true);

        Player player = event.getPlayer();
        EditMine editMine = editingPlayers.remove(player);
        if (editMine == null) return;

        Mine mine = editMine.getMine();
        Material blockMaterial = editMine.getBlockMaterial();
        EditType editType = editMine.getEditType();
        switch (editType) {
            case RESET_DELAY:
                int resetDelayInSeconds = getIntByString(event.getMessage());
                if (resetDelayInSeconds <= 0) {
                    player.sendMessage(Messages.INVALID_AMOUNT);
                    return;
                }

                mine.setResetDelayInSeconds(resetDelayInSeconds);
                mine.getMineTask().startTask();

                VoltzMining.get().getServer().getScheduler().runTaskLater(VoltzMining.get(), () -> Menus.getInstance().openEditMineMenu(player, mine), 0L);
                break;
            case BLOCK_PERCENTAGE:
                double percentage = getDoubleByString(event.getMessage());
                if (percentage <= 0) {
                    player.sendMessage(Messages.INVALID_AMOUNT);
                    return;
                }

                if (percentage > 100) percentage = 100;

                int totalPercentageNow = 0;
                for (Map.Entry<Material, Double> entry : mine.getBlocks().entrySet()) {
                    Material material = entry.getKey();
                    if (material.equals(blockMaterial)) continue;

                    double percentages = entry.getValue();
                    totalPercentageNow += percentages;
                }

                if (percentage + totalPercentageNow > 100) {
                    percentage = (percentage > totalPercentageNow) ? (percentage - totalPercentageNow) : (totalPercentageNow - percentage);
                }

                mine.getBlocks().put(blockMaterial, percentage);

                VoltzMining.get().getServer().getScheduler().runTaskLater(VoltzMining.get(), () -> Menus.getInstance().openMineBlocksMenu(player, mine), 0L);
                break;
        }
    }

    private double getDoubleByString(String str) {
        double percentage = 0;
        try {
            percentage = Double.parseDouble(str);
        } catch (Exception ex) {
            // ignore
        }

        return percentage;
    }

    private int getIntByString(String str) {
        int resetDelayInSeconds = 0;
        try {
            resetDelayInSeconds = Integer.parseInt(str);
        } catch (Exception ex) {
            // ignore
        }

        return resetDelayInSeconds;
    }

    public static Map<Player, EditMine> getEditingPlayers() {
        return editingPlayers;
    }
}
