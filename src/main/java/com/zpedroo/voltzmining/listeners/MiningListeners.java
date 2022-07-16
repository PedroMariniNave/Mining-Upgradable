package com.zpedroo.voltzmining.listeners;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.zpedroo.voltzmining.api.BlockExplodeEvent;
import com.zpedroo.voltzmining.enums.EnchantProperty;
import com.zpedroo.voltzmining.managers.DataManager;
import com.zpedroo.voltzmining.objects.general.Enchant;
import com.zpedroo.voltzmining.objects.player.PlayerData;
import com.zpedroo.voltzmining.utils.pickaxe.PickaxeUtils;
import com.zpedroo.voltzmining.utils.region.RegionUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.math.BigInteger;
import java.util.concurrent.ThreadLocalRandom;

public class MiningListeners implements Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        final Block block = event.getBlock();
        if (!RegionUtils.isOreArea(block.getLocation())) return;

        ItemStack item = player.getItemInHand();
        if (!PickaxeUtils.isPickaxe(item)) return;

        PlayerData data = DataManager.getInstance().getPlayerData(player);
        data.addBlocks(BigInteger.ONE);

        Enchant enchant = DataManager.getInstance().getEnchantByName("explosion");
        double chance = PickaxeUtils.getEnchantEffectByItem(item, enchant, EnchantProperty.CHANCE);
        double randomNumber = ThreadLocalRandom.current().nextDouble(0, 100);
        if (randomNumber > chance) return;

        int radius = (int) PickaxeUtils.getEnchantEffectByItem(item, enchant, EnchantProperty.RADIUS);
        explodeBlocks(player, block, radius/2);
    }

    private void explodeBlocks(Player player, Block referenceBlock, int radius) {
        final ItemStack item = player.getItemInHand();
        for (int xOff = -radius; xOff <= radius; ++xOff) {
            for (int yOff = -radius; yOff <= radius; ++yOff) {
                for (int zOff = -radius; zOff <= radius; ++zOff) {
                    Block blockFound = referenceBlock.getRelative(xOff, yOff, zOff);
                    if (!isBreakableRegion(blockFound.getLocation())) continue;

                    BlockExplodeEvent explodeEvent = new BlockExplodeEvent(player, blockFound, item);
                    Bukkit.getPluginManager().callEvent(explodeEvent);
                    if (explodeEvent.isCancelled()) return;

                    blockFound.setType(Material.AIR);
                }
            }
        }

        double x = referenceBlock.getX();
        double y = referenceBlock.getY();
        double z = referenceBlock.getZ();
        referenceBlock.getWorld().createExplosion(x, y, z, 1, false, false);
    }

    private boolean isBreakableRegion(Location location) {
        ApplicableRegionSet regions = WorldGuardPlugin.inst().getRegionManager(location.getWorld()).getApplicableRegions(location);
        for (ProtectedRegion region : regions.getRegions()) {
            if (region.getFlag(DefaultFlag.BUILD) == StateFlag.State.ALLOW) return true; // multiple regions
        }

        return false;
    }
}