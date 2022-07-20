package com.zpedroo.voltzmining.objects.general;

import lombok.Data;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

@Data
public class Reward {

    private final double chance;
    private final String entityName;
    private final List<String> spawnMessages, collectMessages, commands;
    private final SoundProperties spawnSound, collectSound;
    private final ItemStack itemToGive;

    public void sendSpawnMessages(Player player) {
        for (String message : spawnMessages) {
            player.sendMessage(message);
        }
    }

    public void sendCollectMessages(Player player) {
        for (String message : collectMessages) {
            player.sendMessage(message);
        }
    }

    public void playSpawnSound(Player player) {
        if (!spawnSound.isEnabled()) return;

        player.playSound(player.getLocation(), spawnSound.getSound(), spawnSound.getVolume(), spawnSound.getPitch());
    }

    public void playCollectSound(Player player) {
        if (!collectSound.isEnabled()) return;

        player.playSound(player.getLocation(), collectSound.getSound(), collectSound.getVolume(), collectSound.getPitch());
    }
}