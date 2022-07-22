package com.zpedroo.voltzmining.utils.menus;

import com.boydti.fawe.Fawe;
import com.google.common.collect.Lists;
import com.sk89q.worldedit.regions.Region;
import com.zpedroo.voltzmining.enums.EditType;
import com.zpedroo.voltzmining.listeners.EditMineListeners;
import com.zpedroo.voltzmining.managers.DataManager;
import com.zpedroo.voltzmining.objects.general.Enchant;
import com.zpedroo.voltzmining.objects.mine.EditMine;
import com.zpedroo.voltzmining.objects.mine.Mine;
import com.zpedroo.voltzmining.objects.player.PlayerData;
import com.zpedroo.voltzmining.utils.FileUtils;
import com.zpedroo.voltzmining.utils.builder.InventoryBuilder;
import com.zpedroo.voltzmining.utils.builder.InventoryUtils;
import com.zpedroo.voltzmining.utils.builder.ItemBuilder;
import com.zpedroo.voltzmining.utils.color.Colorize;
import com.zpedroo.voltzmining.utils.config.Messages;
import com.zpedroo.voltzmining.utils.formatter.NumberFormatter;
import com.zpedroo.voltzmining.utils.pickaxe.PickaxeUtils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

import static com.zpedroo.voltzmining.utils.config.Settings.QUALITY_CURRENCY;

public class Menus extends InventoryUtils {

    private static Menus instance;
    public static Menus getInstance() { return instance; }

    private final ItemStack nextPageItem = ItemBuilder.build(FileUtils.get().getFile(FileUtils.Files.CONFIG).get(), "Next-Page").build();
    private final ItemStack previousPageItem = ItemBuilder.build(FileUtils.get().getFile(FileUtils.Files.CONFIG).get(), "Previous-Page").build();

    public Menus() {
        instance = this;
    }

    public void openMainMenu(Player player) {
        FileUtils.Files file = FileUtils.Files.MAIN;

        String title = ChatColor.translateAlternateColorCodes('&', FileUtils.get().getString(file, "Inventory.title"));
        int size = FileUtils.get().getInt(file, "Inventory.size");

        InventoryBuilder inventory = new InventoryBuilder(title, size);
        PlayerData data = DataManager.getInstance().getPlayerData(player);

        for (String str : FileUtils.get().getSection(file, "Inventory.items")) {
            ItemStack item = ItemBuilder.build(FileUtils.get().getFile(file).get(), "Inventory.items." + str, new String[]{
                    "{player}",
                    "{broken_blocks}",
                    "{available_blocks}"
            }, new String[]{
                    player.getName(),
                    NumberFormatter.getInstance().format(data.getBrokenBlocks()),
                    NumberFormatter.getInstance().format(data.getAvailableBlocks())
            }).build();
            String action = FileUtils.get().getString(file, "Inventory.items." + str + ".action");

            int slot = FileUtils.get().getInt(file, "Inventory.items." + str + ".slot");

            inventory.addItem(item, slot, () -> {
                if (StringUtils.contains(action, ":")) {
                    String[] split = action.split(":");
                    String command = split.length > 1 ? split[1] : null;
                    if (command == null) return;

                    switch (split[0].toUpperCase()) {
                        case "PLAYER":
                            player.chat("/" + command);
                            break;
                        case "CONSOLE":
                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), StringUtils.replaceEach(command, new String[]{
                                    "{player}"
                            }, new String[]{
                                    player.getName()
                            }));
                            break;
                    }
                }

                switch (action.toUpperCase()) {
                    case "TOP":
                        openTopMenu(player);
                        break;
                }
            }, ActionType.ALL_CLICKS);
        }

        inventory.open(player);
    }

    public void openTopMenu(Player player) {
        FileUtils.Files file = FileUtils.Files.TOP;

        String title = ChatColor.translateAlternateColorCodes('&', FileUtils.get().getString(file, "Inventory.title"));
        int size = FileUtils.get().getInt(file, "Inventory.size");

        InventoryBuilder inventory = new InventoryBuilder(title, size);

        int pos = 0;
        String[] slots = FileUtils.get().getString(file, "Inventory.slots").replace(" ", "").split(",");

        for (PlayerData data : DataManager.getInstance().getCache().getTopBrokenBlocks()) {
            ItemStack item = ItemBuilder.build(FileUtils.get().getFile(file).get(), "Item", new String[]{
                    "{player}",
                    "{pos}",
                    "{broken_blocks}",
                    "{available_blocks}"
            }, new String[]{
                    Bukkit.getOfflinePlayer(data.getUUID()).getName(),
                    String.valueOf(++pos),
                    NumberFormatter.getInstance().format(data.getBrokenBlocks()),
                    NumberFormatter.getInstance().format(data.getAvailableBlocks())
            }).build();

            int slot = Integer.parseInt(slots[pos - 1]);

            inventory.addItem(item, slot);
        }

        inventory.open(player);
    }

    public void openEditMineMenu(Player player, Mine mine) {
        FileUtils.Files file = FileUtils.Files.EDIT_MINE;

        String title = ChatColor.translateAlternateColorCodes('&', FileUtils.get().getString(file, "Inventory.title"));
        int size = FileUtils.get().getInt(file, "Inventory.size");

        InventoryBuilder inventory = new InventoryBuilder(title, size);

        for (String str : FileUtils.get().getSection(file, "Inventory.items")) {
            ItemStack item = ItemBuilder.build(FileUtils.get().getFile(file).get(), "Inventory.items." + str, new String[]{
                    "{reset}"
            }, new String[]{
                    String.valueOf(mine.getResetDelayInSeconds())
            }).build();
            String action = FileUtils.get().getString(file, "Inventory.items." + str + ".action");

            int slot = FileUtils.get().getInt(file, "Inventory.items." + str + ".slot");

            inventory.addItem(item, slot, () -> {
                switch (action.toUpperCase()) {
                    case "RESET":
                        mine.reset();
                        inventory.close(player);
                        break;

                    case "EDIT_RESET":
                        inventory.close(player);
                        EditMineListeners.getEditingPlayers().put(player, new EditMine(player, mine, null, EditType.RESET_DELAY));

                        clearChat(player);
                        for (String msg : Messages.CHOOSE_RESET_TIME) {
                            player.sendMessage(StringUtils.replaceEach(msg, new String[]{
                                    "{mine}",
                                    "{time}"
                            }, new String[]{
                                    mine.getName(),
                                    String.valueOf(mine.getResetDelayInSeconds())
                            }));
                        }
                        break;

                    case "EDIT_REGION":
                        Region selectedRegion = Fawe.get().getCachedPlayer(player.getUniqueId()).getSelection();
                        if (selectedRegion == null) {
                            player.sendMessage(Messages.INVALID_REGION);
                            return;
                        }

                        mine.setRegion(selectedRegion);
                        break;

                    case "MINE_BLOCKS":
                        openMineBlocksMenu(player, mine);
                        break;
                }
            }, ActionType.ALL_CLICKS);
        }

        inventory.open(player);
    }

    public void openMineBlocksMenu(Player player, Mine mine) {
        FileUtils.Files file = FileUtils.Files.MINE_BLOCKS;

        String title = ChatColor.translateAlternateColorCodes('&', FileUtils.get().getString(file, "Inventory.title"));
        int size = FileUtils.get().getInt(file, "Inventory.size");

        int nextPageSlot = FileUtils.get().getInt(file, "Inventory.next-page-slot");
        int previousPageSlot = FileUtils.get().getInt(file, "Inventory.previous-page-slot");

        InventoryBuilder inventory = new InventoryBuilder(title, size, previousPageItem, previousPageSlot, nextPageItem, nextPageSlot);

        Map<Material, Double> mineBlocks = mine.getBlocks();
        if (!mineBlocks.isEmpty()) {
            String[] slots = FileUtils.get().getString(file, "Inventory.slots").replace(" ", "").split(",");
            int i = -1;
            for (Map.Entry<Material, Double> entry : mineBlocks.entrySet()) {
                if (++i >= slots.length) i = 0;

                Material blockMaterial = entry.getKey();
                double percentage = entry.getValue();

                ItemStack item = ItemBuilder.build(FileUtils.get().getFile(file).get(), "Item", new String[]{
                        "{material}",
                        "{percentage}"
                }, new String[]{
                        blockMaterial.toString(),
                        String.format("%.1f", percentage)
                }).build();
                int slot = Integer.parseInt(slots[i]);

                inventory.addItem(item, slot, () -> {
                    mine.getBlocks().remove(blockMaterial);

                    openMineBlocksMenu(player, mine);
                }, ActionType.LEFT_CLICK);

                inventory.addAction(slot, () -> {
                    inventory.close(player);
                    EditMineListeners.getEditingPlayers().put(player, new EditMine(player, mine, blockMaterial, EditType.BLOCK_PERCENTAGE));

                    clearChat(player);
                    for (String msg : Messages.CHOOSE_BLOCK_PERCENTAGE) {
                        player.sendMessage(StringUtils.replaceEach(msg, new String[]{
                                "{mine}",
                                "{block}",
                                "{percentage}"
                        }, new String[]{
                                mine.getName(),
                                blockMaterial.toString(),
                                String.valueOf(mine.getBlocks().getOrDefault(blockMaterial, 0D))
                        }));
                    }
                }, ActionType.RIGHT_CLICK);
            }
        } else {
            ItemStack nothingItem = ItemBuilder.build(FileUtils.get().getFile(file).get(), "Nothing").build();
            int nothingSlot = FileUtils.get().getInt(file, "Nothing.slot");

            inventory.addItem(nothingItem, nothingSlot);
        }

        ItemStack addBlockItem = ItemBuilder.build(FileUtils.get().getFile(file).get(), "Add-Block").build();
        int addBlockSlot = FileUtils.get().getInt(file, "Add-Block.slot");

        inventory.addItem(addBlockItem, addBlockSlot, () -> {
            inventory.close(player);

            if (player.getItemInHand() == null || player.getItemInHand().getType().equals(Material.AIR) || !player.getItemInHand().getType().isBlock()) {
                player.sendMessage(Messages.INVALID_BLOCK_MATERIAL);
                return;
            }

            Material blockMaterial = player.getItemInHand().getType();
            EditMineListeners.getEditingPlayers().put(player, new EditMine(player, mine, blockMaterial, EditType.BLOCK_PERCENTAGE));

            clearChat(player);
            for (String msg : Messages.CHOOSE_BLOCK_PERCENTAGE) {
                player.sendMessage(StringUtils.replaceEach(msg, new String[]{
                        "{mine}",
                        "{block}",
                        "{percentage}"
                }, new String[]{
                        mine.getName(),
                        blockMaterial.toString(),
                        String.valueOf(mine.getBlocks().getOrDefault(blockMaterial, 0D))
                }));
            }
        }, ActionType.ALL_CLICKS);

        inventory.open(player);
    }

    public void openUpgradeMenu(Player player, ItemStack itemToUpgrade) {
        FileUtils.Files file = FileUtils.Files.UPGRADE;
        FileConfiguration fileConfiguration = FileUtils.get().getFile(file).get();

        String title = Colorize.getColored(FileUtils.get().getString(file, "Inventory.title"));
        int size = FileUtils.get().getInt(file, "Inventory.size");

        InventoryBuilder inventory = new InventoryBuilder(title, size);

        for (String items : FileUtils.get().getSection(file, "Inventory.items")) {
            String action = FileUtils.get().getString(file, "Inventory.items." + items + ".action");
            String[] split = action.split(":");
            String upgradeElementName = split.length > 1 ? split[1] : "NULL";
            Enchant enchant = DataManager.getInstance().getEnchantByName(upgradeElementName);
            ItemStack item = buildUpgradeItem(player, itemToUpgrade, fileConfiguration, items, action, enchant);
            int slot = FileUtils.get().getInt(file, "Inventory.items." + items + ".slot");

            inventory.addItem(item, slot, () -> {
                if (!StringUtils.containsIgnoreCase(action, "UPGRADE:")) return;

                ItemStack newItem = null;
                switch (upgradeElementName.toUpperCase()) {
                    case "QUALITY":
                        if (!PickaxeUtils.canUpgradeQuality(player, itemToUpgrade)) {
                            player.playSound(player.getLocation(), Sound.VILLAGER_NO, 1f, 1f);
                            return;
                        }

                        newItem = PickaxeUtils.upgradeQuality(player, itemToUpgrade);
                        break;
                    default:
                        if (enchant == null) return;
                        if (!PickaxeUtils.canUpgradeEnchant(itemToUpgrade, enchant)) {
                            player.playSound(player.getLocation(), Sound.VILLAGER_NO, 1f, 1f);
                            return;
                        }

                        newItem = PickaxeUtils.upgradeEnchantment(itemToUpgrade, enchant);
                        break;
                }

                openUpgradeMenu(player, newItem);
                player.setItemInHand(newItem);
                player.playSound(player.getLocation(), Sound.LEVEL_UP, 1f, 4f);
            }, ActionType.ALL_CLICKS);
        }

        int itemSlot = FileUtils.get().getInt(file, "Inventory.item-slot");
        if (itemSlot != -1) {
            inventory.addItem(itemToUpgrade, itemSlot);
        }

        inventory.open(player);
    }

    @NotNull
    private ItemStack buildUpgradeItem(Player player, ItemStack itemToUpgrade, FileConfiguration fileConfiguration, String items, String action, Enchant enchant) {
        ItemStack item = null;
        String toGet = getElementToGet(player, itemToUpgrade, action, enchant);

        if (fileConfiguration.contains("Inventory.items." + items + "." + toGet)) {
            String[] placeholders = getUpgradePlaceholders();
            String[] replacers = getUpgradeReplacers(itemToUpgrade, enchant);

            item = ItemBuilder.build(fileConfiguration, "Inventory.items." + items + "." + toGet, placeholders, replacers).build();
        } else {
            item = ItemBuilder.build(fileConfiguration, "Inventory.items." + items).build();
        }

        return item;
    }

    @Nullable
    private String getElementToGet(Player player, ItemStack itemToUpgrade, String action, Enchant enchant) {
        String toGet = null;
        if (enchant != null) {
            toGet = getItemEnchantStatus(itemToUpgrade, enchant);
        } else if (StringUtils.containsIgnoreCase(action, "QUALITY")) {
            toGet = getItemQualityStatus(player, itemToUpgrade);
        }

        return toGet;
    }

    private String[] getUpgradePlaceholders() {
        List<String> placeholders = Lists.newArrayList(PickaxeUtils.getPlaceholders());
        placeholders.add("{cost}");
        placeholders.add("{required_level}");

        return placeholders.toArray(new String[0]);
    }

    private String[] getUpgradeReplacers(@NotNull ItemStack item, @Nullable Enchant enchant) {
        List<String> replacers = Lists.newArrayList(PickaxeUtils.getReplacers(item));
        BigInteger upgradeCost;
        int upgradeLevelRequired;
        if (enchant == null) {
            upgradeCost = PickaxeUtils.getQualityUpgradeCost(item);
            upgradeLevelRequired = PickaxeUtils.getQualityUpgradeLevelRequired(item);

            replacers.add(QUALITY_CURRENCY == null ? NumberFormatter.getInstance().format(upgradeCost) : QUALITY_CURRENCY.getAmountDisplay(upgradeCost));
            replacers.add(NumberFormatter.getInstance().formatThousand(upgradeLevelRequired));
        } else {
            upgradeCost = BigInteger.valueOf(PickaxeUtils.getEnchantUpgradeCost(item, enchant));
            upgradeLevelRequired = PickaxeUtils.getEnchantUpgradeLevelRequired(item, enchant);

            replacers.add(NumberFormatter.getInstance().format(upgradeCost));
            replacers.add(NumberFormatter.getInstance().formatThousand(upgradeLevelRequired));
        }

        return replacers.toArray(new String[0]);
    }

    private String getItemEnchantStatus(@NotNull ItemStack item, @Nullable Enchant enchant) {
        if (enchant == null) return "undefined";
        if (PickaxeUtils.isMaxEnchantLevel(item, enchant)) {
            return "max-level";
        } else if (!PickaxeUtils.isUnlockedEnchant(item, enchant)) {
            return "locked";
        } else if (!PickaxeUtils.canUpgradeEnchant(item, enchant)) {
            return "can-not-upgrade";
        }

        return "can-upgrade";
    }

    private String getItemQualityStatus(@Nullable Player player, @NotNull ItemStack item) {
        if (PickaxeUtils.isMaxQualityLevel(item)) {
            return "max-level";
        } else if (!PickaxeUtils.isUnlockedQuality(item)) {
            return "locked";
        } else if (!PickaxeUtils.canUpgradeQuality(player, item)) {
            return "can-not-upgrade";
        }

        return "can-upgrade";
    }

    private void clearChat(Player player) {
        for (int i = 0; i < 25; ++i) {
            player.sendMessage("");
        }
    }
}