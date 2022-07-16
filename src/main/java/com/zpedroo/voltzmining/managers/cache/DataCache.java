package com.zpedroo.voltzmining.managers.cache;

import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import com.zpedroo.voltzmining.VoltzMining;
import com.zpedroo.voltzmining.enums.EnchantProperty;
import com.zpedroo.voltzmining.mysql.DBConnection;
import com.zpedroo.voltzmining.objects.general.Enchant;
import com.zpedroo.voltzmining.objects.mine.Mine;
import com.zpedroo.voltzmining.objects.player.PlayerData;
import com.zpedroo.voltzmining.utils.FileUtils;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class DataCache {

    private final Map<String, Enchant> enchants = getEnchantsFromConfig();
    private final Map<Player, PlayerData> playerData = new HashMap<>(64);
    private Map<String, Mine> mines = null;
    private List<PlayerData> topBrokenBlocks;

    public DataCache() {
        VoltzMining.get().getServer().getScheduler().runTaskLaterAsynchronously(VoltzMining.get(), () -> {
            this.mines = loadMinesFromFile();
        }, 20L);
    }

    public List<PlayerData> getTopBrokenBlocks() {
        if (topBrokenBlocks == null) {
            this.topBrokenBlocks = DBConnection.getInstance().getDBManager().getTopBrokenBlocks();
        }
        
        return topBrokenBlocks;
    }

    private Map<String, Enchant> getEnchantsFromConfig() {
        FileUtils.Files file = FileUtils.Files.CONFIG;
        Map<String, Enchant> ret = new HashMap<>(4);
        for (String enchantName : FileUtils.get().getSection(file, "Enchants")) {
            Enchant enchant = loadEnchant(enchantName, file);
            ret.put(enchantName.toUpperCase(), enchant);
        }

        return ret;
    }

    private Map<String, Mine> loadMinesFromFile() {
        FileUtils.Files file = FileUtils.Files.MINES;

        Map<String, Mine> ret = new HashMap<>(4);

        for (String mineName : FileUtils.get().getSection(file, "Mines")) {
            int resetDelayInSeconds = FileUtils.get().getInt(file, "Mines." + mineName + ".reset-delay");
            Region region = null;

            if (FileUtils.get().getFile(file).get().contains("Mines." + mineName + ".min-point")) {
                World world = Bukkit.getWorld(FileUtils.get().getString(file, "Mines." + mineName + ".world"));
                if (world == null) continue;

                double minPointX = FileUtils.get().getDouble(file, "Mines." + mineName + ".min-point.x");
                double minPointY = FileUtils.get().getDouble(file, "Mines." + mineName + ".min-point.y");
                double minPointZ = FileUtils.get().getDouble(file, "Mines." + mineName + ".min-point.z");

                double maxPointX = FileUtils.get().getDouble(file, "Mines." + mineName + ".max-point.x");
                double maxPointY = FileUtils.get().getDouble(file, "Mines." + mineName + ".max-point.y");
                double maxPointZ = FileUtils.get().getDouble(file, "Mines." + mineName + ".max-point.z");

                Vector minPointVector = new Vector(minPointX, minPointY, minPointZ);
                Vector maxPointVector = new Vector(maxPointX, maxPointY, maxPointZ);

                region = new CuboidRegion(new BukkitWorld(world), minPointVector, maxPointVector);
            }

            Map<Material, Double> blocks = null;

            if (FileUtils.get().getFile(file).get().contains("Mines." + mineName + ".blocks")) {
                List<String> blocksStr = FileUtils.get().getStringList(file, "Mines." + mineName + ".blocks");
                blocks = new HashMap<>(blocksStr.size());

                for (String str : blocksStr) {
                    String[] split = str.split(",");
                    if (split.length < 2) continue;

                    Material blockMaterial = Material.getMaterial(split[0].toUpperCase());
                    if (blockMaterial == null) continue;

                    double percentage = Double.parseDouble(split[1]);

                    blocks.put(blockMaterial, percentage);
                }
            }

            ret.put(mineName, new Mine(mineName, blocks, region, resetDelayInSeconds));
        }

        return ret;
    }

    private Enchant loadEnchant(String enchantName, FileUtils.Files file) {
        if (!FileUtils.get().getFile(file).get().contains("Enchants." + enchantName)) return null;

        int initialLevel = FileUtils.get().getInt(file, "Enchants." + enchantName + ".level.initial");
        int maxLevel = FileUtils.get().getInt(file, "Enchants." + enchantName + ".level.max");
        int requiredLevel = FileUtils.get().getInt(file, "Enchants." + enchantName + ".level.requirement-per-upgrade");
        int costPerLevel = FileUtils.get().getInt(file, "Enchants." + enchantName + ".cost-per-level");
        double chanceInitialValue = FileUtils.get().getDouble(file, "Enchants." + enchantName + ".chance.initial-value");
        double chancePerLevel = FileUtils.get().getDouble(file, "Enchants." + enchantName + ".chance.upgrade-per-level");
        double multiplierPerLevel = FileUtils.get().getDouble(file, "Enchants." + enchantName + ".multiplier-per-level");
        int radiusInitialValue = FileUtils.get().getInt(file, "Enchants." + enchantName + ".radius.initial-value");
        int radiusPerLevel = FileUtils.get().getInt(file, "Enchants." + enchantName + ".radius.upgrade-per-level");

        Map<EnchantProperty, Number> enchantProperties = new HashMap<>(EnchantProperty.values().length);
        enchantProperties.put(EnchantProperty.CHANCE, chancePerLevel);
        enchantProperties.put(EnchantProperty.MULTIPLIER, multiplierPerLevel);
        enchantProperties.put(EnchantProperty.RADIUS, radiusPerLevel);

        Map<EnchantProperty, Number> initialValues = new HashMap<>(EnchantProperty.values().length);
        initialValues.put(EnchantProperty.CHANCE, chanceInitialValue);
        initialValues.put(EnchantProperty.RADIUS, radiusInitialValue);

        return new Enchant(enchantName, initialLevel, maxLevel, requiredLevel, costPerLevel, enchantProperties, initialValues);
    }
}