package com.zpedroo.voltzmining.tasks;

import com.zpedroo.voltzmining.VoltzMining;
import com.zpedroo.voltzmining.objects.mine.Mine;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.bukkit.scheduler.BukkitRunnable;

@EqualsAndHashCode(callSuper = true)
@Data
public class MineTask extends BukkitRunnable {

    private final Mine mine;

    @Override
    public void run() {
        mine.reset();
    }

    public void startTask() {
        if (mine.getResetDelayInSeconds() == -1) return;

        this.runTaskTimerAsynchronously(VoltzMining.get(), 0L, 20L * mine.getResetDelayInSeconds());
    }
}