package com.zpedroo.voltzmining.managers.cache;

import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import com.zpedroo.voltzmining.VoltzMining;
import com.zpedroo.voltzmining.enums.EnchantProperty;
import com.zpedroo.voltzmining.mysql.DBConnection;
import com.zpedroo.voltzmining.objects.general.Enchant;
import com.zpedroo.voltzmining.objects.general.Reward;
import com.zpedroo.voltzmining.objects.general.SoundProperties;
import com.zpedroo.voltzmining.objects.mine.Mine;
import com.zpedroo.voltzmining.objects.player.PlayerData;
import com.zpedroo.voltzmining.utils.FileUtils;
import com.zpedroo.voltzmining.utils.builder.ItemBuilder;
import com.zpedroo.voltzmining.utils.color.Colorize;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

@Getter
@Setter
public class DataCache {

    private List<PlayerData> topBrokenBlocks;
    private final List<Reward> rewards = getRewardsFromFile();
    private final Map<String, Enchant> enchants = getEnchantsFromConfig();
    private final Map<Player, PlayerData> playerData = new HashMap<>(64);
    private Map<String, Mine> mines = null;

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

    private List<Reward> getRewardsFromFile() {
        FileUtils.Files file = FileUtils.Files.REWARDS;
        List<Reward> ret = new LinkedList<>();
        for (String str : FileUtils.get().getSection(file, "Rewards")) {
            double chance = FileUtils.get().getDouble(file, "Rewards." + str + ".chance");
            String entityName = FileUtils.get().getString(file, "Rewards." + str + ".entity");
            List<String> spawnMessages = Colorize.getColored(FileUtils.get().getStringList(file, "Rewards." + str + ".messages.spawn"));
            List<String> collectMessages = Colorize.getColored(FileUtils.get().getStringList(file, "Rewards." + str + ".messages.collect"));
            List<String> commands = FileUtils.get().getStringList(file, "Rewards." + str + ".commands");
            SoundProperties spawnSound = loadSound("Rewards." + str + ".sounds.spawn", file);
            SoundProperties collectSound = loadSound("Rewards." + str + ".sounds.collect", file);
            ItemStack itemToGive = null;
            if (FileUtils.get().getFile(file).get().contains("Rewards." + str + ".item-to-give")) {
                itemToGive = ItemBuilder.build(FileUtils.get().getFile(file).get(), "Rewards." + str + ".item-to-give").build();
            }

            ret.add(new Reward(chance, entityName, spawnMessages, collectMessages, commands, spawnSound, collectSound, itemToGive));
        }

        return ret;
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

    private SoundProperties loadSound(String where, FileUtils.Files file) {
        boolean enabled = FileUtils.get().getBoolean(file, where + ".enabled");
        Sound sound = Sound.valueOf(FileUtils.get().getString(file, where + ".sound"));
        float volume = FileUtils.get().getFloat(file, where + ".volume");
        float pitch = FileUtils.get().getFloat(file, where + ".pitch");

        return new SoundProperties(enabled, sound, volume, pitch);
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