package com.zpedroo.voltzmining.listeners;

import com.zpedroo.voltzmining.objects.general.Reward;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class RewardListeners implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onDamage(EntityDamageEvent event) {
        if (isRewardEntity(event.getEntity())) event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onDamage(EntityDamageByEntityEvent event) {
        if (!isRewardEntity(event.getEntity()) || !(event.getDamager() instanceof Player)) return;

        Entity entity = event.getEntity();
        Player player = (Player) event.getDamager();
        String ownerName = entity.getMetadata("MiningRewardOwner").get(0).asString();
        if (!StringUtils.equals(player.getName(), ownerName)) return;

        entity.remove();

        Reward reward = (Reward) entity.getMetadata("MiningRewardObject").get(0).value();
        giveReward(player, reward);
        reward.sendCollectMessages(player);
        reward.playCollectSound(player);
    }

    private void giveReward(@NotNull Player player, @NotNull Reward reward) {
        for (String command : reward.getCommands()) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), StringUtils.replace(command, "{player}", player.getName()));
        }

        ItemStack itemToGive = reward.getItemToGive();
        if (itemToGive != null) {
            if (player.getInventory().firstEmpty() != -1) {
                player.getInventory().addItem(itemToGive);
            } else {
                player.getWorld().dropItemNaturally(player.getLocation(), itemToGive);
            }
        }
    }

    private boolean isRewardEntity(Entity entity) {
        return entity.hasMetadata("MiningRewardObject");
    }
}