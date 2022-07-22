package com.zpedroo.voltzmining.utils.config;

import com.zpedroo.voltzmining.managers.DataManager;
import com.zpedroo.voltzmining.objects.general.Enchant;
import com.zpedroo.voltzmining.utils.FileUtils;
import com.zpedroo.voltzmining.utils.builder.ItemBuilder;
import com.zpedroo.voltzmining.utils.formatter.NumberFormatter;
import com.zpedroo.voltzmining.utils.pickaxe.PickaxeUtils;
import de.tr7zw.nbtapi.NBTItem;
import org.apache.commons.lang.StringUtils;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class Items {

    private static final ItemStack POINTS_ITEM = ItemBuilder.build(FileUtils.get().getFile(FileUtils.Files.CONFIG).get(), "Points-Item").build();
    private static final ItemStack PICKAXE_ITEM = ItemBuilder.build(FileUtils.get().getFile(FileUtils.Files.CONFIG).get(), "Pickaxe-Item").build();

    @NotNull
    public static ItemStack getPointsItem(long amount) {
        NBTItem nbt = new NBTItem(POINTS_ITEM.clone());
        nbt.setLong(PickaxeUtils.POINTS_ITEM_NBT, amount);

        String[] placeholders = new String[]{
                "{amount}"
        };
        String[] replacers = new String[]{
                NumberFormatter.getInstance().format(BigInteger.valueOf(amount))
        };

        return replaceItemPlaceholders(nbt.getItem(), placeholders, replacers);
    }

    @NotNull
    public static ItemStack getPickaxeItem() {
        NBTItem nbt = new NBTItem(PICKAXE_ITEM.clone());
        nbt.setBoolean(PickaxeUtils.IDENTIFIER_NBT, true);

        ItemStack item = nbt.getItem();
        return replaceItemPlaceholders(item, PickaxeUtils.getPlaceholders(), PickaxeUtils.getReplacers(item));
    }

    @NotNull
    public static ItemStack getPickaxeItem(@NotNull ItemStack baseItem) {
        NBTItem nbt = new NBTItem(PICKAXE_ITEM.clone());
        nbt.setBoolean(PickaxeUtils.IDENTIFIER_NBT, true);

        for (Enchant enchant : DataManager.getInstance().getEnchants()) {
            int level = PickaxeUtils.getEnchantmentLevel(baseItem, enchant);
            if (level <= enchant.getInitialLevel()) continue;

            String enchantName = enchant.getName();
            nbt.setInteger(enchantName, level);
            Enchantment enchantment = Enchantment.getByName(enchantName.toUpperCase());
            if (enchantment != null) {
                int finalLevel = PickaxeUtils.applyQualityBonus(baseItem, level);
                nbt.getItem().addUnsafeEnchantment(enchantment, finalLevel);
            }
        }

        nbt.setDouble(PickaxeUtils.EXPERIENCE_NBT, PickaxeUtils.getItemExperience(baseItem));
        nbt.setLong(PickaxeUtils.PICKAXE_POINTS_NBT, PickaxeUtils.getItemPoints(baseItem));
        nbt.setInteger(PickaxeUtils.QUALITY_NBT, PickaxeUtils.getItemQuality(baseItem));

        ItemStack item = nbt.getItem();
        return replaceItemPlaceholders(item, PickaxeUtils.getPlaceholders(), PickaxeUtils.getReplacers(item));
    }

    @NotNull
    private static ItemStack replaceItemPlaceholders(ItemStack item, String[] placeholders, String[] replacers) {
        if (item.getItemMeta() != null) {
            String displayName = item.getItemMeta().hasDisplayName() ? item.getItemMeta().getDisplayName() : null;
            List<String> lore = item.getItemMeta().hasLore() ? item.getItemMeta().getLore() : null;
            ItemMeta meta = item.getItemMeta();
            if (displayName != null) meta.setDisplayName(StringUtils.replaceEach(displayName, placeholders, replacers));
            if (lore != null) {
                List<String> newLore = new ArrayList<>(lore.size());
                for (String str : lore) {
                    newLore.add(StringUtils.replaceEach(str, placeholders, replacers));
                }

                meta.setLore(newLore);
            }

            item.setItemMeta(meta);
        }

        return item;
    }
}