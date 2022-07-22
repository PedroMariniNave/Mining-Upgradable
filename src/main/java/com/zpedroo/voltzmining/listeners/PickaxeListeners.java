package com.zpedroo.voltzmining.listeners;

import com.zpedroo.voltzmining.enums.EnchantProperty;
import com.zpedroo.voltzmining.managers.DataManager;
import com.zpedroo.voltzmining.objects.general.BlockProperties;
import com.zpedroo.voltzmining.objects.general.Enchant;
import com.zpedroo.voltzmining.utils.config.Blocks;
import com.zpedroo.voltzmining.utils.config.Titles;
import com.zpedroo.voltzmining.utils.formatter.NumberFormatter;
import com.zpedroo.voltzmining.utils.pickaxe.PickaxeUtils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

public class PickaxeListeners implements Listener {

    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.isCancelled()) return;

        Player player = event.getPlayer();
        if (Blocks.DISABLED_WORLDS.contains(player.getWorld().getName())) return;

        ItemStack item = player.getItemInHand();
        if (!PickaxeUtils.isPickaxe(item)) return;

        Block block = event.getBlock();
        if (!hasBlockProperties(block)) return;

        BlockProperties blockProperties = getBlockProperties(block);
        final int oldLevel = PickaxeUtils.getItemLevel(item);
        Enchant enchant = DataManager.getInstance().getEnchantByName("exp");
        double bonus = 1 + PickaxeUtils.getEnchantEffectByItem(item, enchant, EnchantProperty.MULTIPLIER);
        double expAmount = blockProperties.getExpAmount();
        double expToGive = expAmount * bonus;

        if (Blocks.FORTUNE_ENABLED) {
            int enchantmentLevel = item.getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS);
            double multiplier = 1 + (enchantmentLevel * Blocks.FORTUNE_MULTIPLIER);
            expToGive *= multiplier;
        }

        ItemStack newItem = PickaxeUtils.addItemExperience(item, expToGive);
        newItem = PickaxeUtils.addItemPoints(newItem, blockProperties.getPointsAmount());
        int newLevel = PickaxeUtils.getItemLevel(newItem);

        if (isNewLevel(oldLevel, newLevel)) {
            sendUpgradeTitle(player, oldLevel, newLevel);
        }

        player.setItemInHand(newItem);
    }

    @Nullable
    private BlockProperties getBlockProperties(Block block) {
        return Blocks.LIST.stream().filter(blockProperties -> blockProperties.getMaterial().equals(block.getType())).findAny().orElse(null);
    }

    private void sendUpgradeTitle(Player player, int oldLevel, int newLevel) {
        String[] placeholders = new String[]{ "{old_level}", "{new_level}" };
        String[] replacers = new String[]{
                NumberFormatter.getInstance().formatThousand(oldLevel), NumberFormatter.getInstance().formatThousand(newLevel)
        };
        player.sendTitle(
                StringUtils.replaceEach(Titles.PICKAXE_UPGRADE[0], placeholders, replacers),
                StringUtils.replaceEach(Titles.PICKAXE_UPGRADE[1], placeholders, replacers)
        );
    }

    private boolean hasBlockProperties(Block block) {
        return getBlockProperties(block) != null;
    }

    private boolean isNewLevel(int oldLevel, int newLevel) {
        return oldLevel != newLevel;
    }
}