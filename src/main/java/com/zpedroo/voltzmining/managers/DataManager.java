package com.zpedroo.voltzmining.managers;

import com.zpedroo.voltzmining.managers.cache.DataCache;
import com.zpedroo.voltzmining.mysql.DBConnection;
import com.zpedroo.voltzmining.objects.general.Enchant;
import com.zpedroo.voltzmining.objects.mine.Mine;
import com.zpedroo.voltzmining.objects.player.PlayerData;
import com.zpedroo.voltzmining.utils.FileUtils;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class DataManager {

    private static DataManager instance;
    public static DataManager getInstance() { return instance; }

    private final DataCache dataCache = new DataCache();

    public DataManager() {
        instance = this;
    }

    @NotNull
    public PlayerData getPlayerData(Player player) {
        PlayerData data = dataCache.getPlayerData().get(player);
        if (data == null) {
            data = DBConnection.getInstance().getDBManager().getPlayerData(player);
            dataCache.getPlayerData().put(player, data);
        }

        return data;
    }

    @Nullable
    public Mine getMineByName(String mineName) {
        return dataCache.getMines().get(mineName);
    }

    @Nullable
    public Enchant getEnchantByName(String enchantName) {
        return dataCache.getEnchants().get(enchantName.toUpperCase());
    }

    @NotNull
    public Collection<Enchant> getEnchants() {
        return dataCache.getEnchants().values();
    }

    public void savePlayerData(Player player) {
        PlayerData data = getPlayerData(player);
        if (!data.isQueueUpdate()) return;

        DBConnection.getInstance().getDBManager().save(data);
        data.setQueueUpdate(false);
    }

    public void saveMineData(Mine mine) {
        FileUtils.Files file = FileUtils.Files.MINES;
        FileUtils.FileManager fileManager = FileUtils.get().getFile(file);
        FileConfiguration fileConfiguration = fileManager.get();

        if (mine.getRegion() != null && mine.getRegion().getWorld() != null) {
            fileConfiguration.set("Mines." + mine.getName() + ".world", mine.getRegion().getWorld().getName());
            fileConfiguration.set("Mines." + mine.getName() + ".min-point.x", mine.getRegion().getMinimumPoint().getX());
            fileConfiguration.set("Mines." + mine.getName() + ".min-point.y", mine.getRegion().getMinimumPoint().getY());
            fileConfiguration.set("Mines." + mine.getName() + ".min-point.z", mine.getRegion().getMinimumPoint().getZ());
            fileConfiguration.set("Mines." + mine.getName() + ".max-point.x", mine.getRegion().getMaximumPoint().getX());
            fileConfiguration.set("Mines." + mine.getName() + ".max-point.y", mine.getRegion().getMaximumPoint().getY());
            fileConfiguration.set("Mines." + mine.getName() + ".max-point.z", mine.getRegion().getMaximumPoint().getZ());
        }

        if (mine.getBlocks() != null) {
            Map<Material, Double> blocks = mine.getBlocks();
            List<String> blocksList = new ArrayList<>(blocks.size());
            for (Map.Entry<Material, Double> entry : blocks.entrySet()) {
                Material blockMaterial = entry.getKey();
                double percentage = entry.getValue();

                blocksList.add(blockMaterial.toString() + "," + percentage);
            }

            fileConfiguration.set("Mines." + mine.getName() + ".blocks", blocksList);
        }

        fileConfiguration.set("Mines." + mine.getName() + ".reset-delay", mine.getResetDelayInSeconds());
        fileManager.save();
    }

    public void saveAllPlayersData() {
        dataCache.getPlayerData().keySet().forEach(this::savePlayerData);
    }

    public void saveAllMinesData() {
        dataCache.getMines().values().forEach(this::saveMineData);
    }

    public DataCache getCache() {
        return dataCache;
    }
}