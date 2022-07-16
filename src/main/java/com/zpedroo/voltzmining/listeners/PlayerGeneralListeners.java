package com.zpedroo.voltzmining.listeners;

import com.zpedroo.voltzmining.managers.DataManager;
import com.zpedroo.voltzmining.utils.menus.Menus;
import com.zpedroo.voltzmining.utils.pickaxe.PickaxeUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerGeneralListeners implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onQuit(PlayerQuitEvent event) {
        DataManager.getInstance().savePlayerData(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK && event.getAction() != Action.RIGHT_CLICK_AIR) return;
        if (!PickaxeUtils.isPickaxe(event.getItem())) return;

        event.setCancelled(true);

        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        Menus.getInstance().openUpgradeMenu(player, item);
    }
}