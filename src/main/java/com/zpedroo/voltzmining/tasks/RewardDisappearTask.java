package com.zpedroo.voltzmining.tasks;

import com.zpedroo.voltzmining.VoltzMining;
import com.zpedroo.voltzmining.utils.config.Messages;
import com.zpedroo.voltzmining.utils.config.Settings;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

public class RewardDisappearTask extends BukkitRunnable {

    private final Player player;
    private final Entity entity;

    public RewardDisappearTask(@NotNull Player player, @NotNull Entity entity) {
        this.player = player;
        this.entity = entity;
    }

    @Override
    public void run() {
        if (entity.isDead()) return;

        entity.remove();
        if (player.isOnline()) {
            for (String message : Messages.REWARD_DISAPPEAR) {
                player.sendMessage(message);
            }
        }
    }

    public void start() {
        this.runTaskLaterAsynchronously(VoltzMining.get(), Settings.REWARD_DISAPPEAR_TIME * 20L);
    }
}