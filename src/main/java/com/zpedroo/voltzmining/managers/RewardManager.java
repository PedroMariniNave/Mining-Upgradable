package com.zpedroo.voltzmining.managers;

import com.zpedroo.voltzmining.nms.BabyRainbowSheep;
import com.zpedroo.voltzmining.nms.BabyZombieEntity;
import com.zpedroo.voltzmining.objects.general.Reward;
import com.zpedroo.voltzmining.tasks.RewardDisappearTask;
import net.minecraft.server.v1_8_R3.Entity;
import net.minecraft.server.v1_8_R3.WorldServer;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class RewardManager {

    private static RewardManager instance;
    public static RewardManager getInstance() { return instance; }

    public RewardManager() {
        instance = this;
    }

    public void handleRewardsChance(Player player, Location spawnLocation) {
        List<Reward> rewards = DataManager.getInstance().getCache().getRewards();
        for (Reward reward : rewards) {
            double chance = reward.getChance();
            double randomNumber = ThreadLocalRandom.current().nextDouble(0, 100);
            if (randomNumber > chance) continue;

            Entity entity = spawnRewardEntity(player, spawnLocation, reward);
            startRewardDisappearTask(player, entity);
            reward.sendSpawnMessages(player);
            reward.playSpawnSound(player);
            break;
        }
    }

    private Entity spawnRewardEntity(Player player, Location spawnLocation, Reward reward) {
        Entity entity = null;
        switch (reward.getEntityName().toUpperCase()) {
            case "BABY_ZOMBIE":
                entity = new BabyZombieEntity(spawnLocation, player, reward);
                break;
            case "BABY_RAINBOW_SHEEP":
                entity = new BabyRainbowSheep(spawnLocation, player, reward);
                break;
        }

        if (entity != null) {
            WorldServer worldServer = ((CraftWorld) player.getWorld()).getHandle();
            worldServer.addEntity(entity);
        }

        return entity;
    }

    private void startRewardDisappearTask(Player player, Entity entity) {
        RewardDisappearTask task = new RewardDisappearTask(player, entity.getBukkitEntity());
        task.start();
    }
}