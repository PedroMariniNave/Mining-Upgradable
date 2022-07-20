package com.zpedroo.voltzmining.nms;

import com.zpedroo.voltzmining.VoltzMining;
import com.zpedroo.voltzmining.objects.general.Reward;
import com.zpedroo.voltzmining.utils.config.Settings;
import net.minecraft.server.v1_8_R3.EntitySheep;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftSheep;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

public class BabyRainbowSheep extends EntitySheep {

    public BabyRainbowSheep(Location location, Player player, Reward reward) {
        super(((CraftWorld) location.getWorld()).getHandle());

        this.setPosition(location.getX(), location.getY(), location.getZ());
        this.setCustomName(StringUtils.replace(Settings.REWARD_ENTITY_NAME, "{player}", player.getName()));
        this.setCustomNameVisible(true);
        this.getBukkitEntity().setMetadata("MiningReward", new FixedMetadataValue(VoltzMining.get(), reward));
        ((CraftSheep) getBukkitEntity()).setBaby();
    }
}