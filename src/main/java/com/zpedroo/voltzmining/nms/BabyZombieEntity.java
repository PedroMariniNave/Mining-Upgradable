package com.zpedroo.voltzmining.nms;

import com.zpedroo.voltzmining.VoltzMining;
import com.zpedroo.voltzmining.objects.general.Reward;
import com.zpedroo.voltzmining.utils.config.Settings;
import net.minecraft.server.v1_8_R3.EntityZombie;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftZombie;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

public class BabyZombieEntity extends EntityZombie {

    public BabyZombieEntity(Location location, Player player, Reward reward) {
        super(((CraftWorld) location.getWorld()).getHandle());

        this.setPosition(location.getX(), location.getY(), location.getZ());
        this.setCustomName(StringUtils.replace(Settings.REWARD_ENTITY_NAME, "{player}", player.getName()));
        this.setCustomNameVisible(true);
        this.getBukkitEntity().setMetadata("MiningRewardOwner", new FixedMetadataValue(VoltzMining.get(), player.getName()));
        this.getBukkitEntity().setMetadata("MiningRewardObject", new FixedMetadataValue(VoltzMining.get(), reward));
        ((CraftZombie) getBukkitEntity()).setBaby(true);
        ((CraftZombie) getBukkitEntity()).setTarget(player);
        ((CraftZombie) getBukkitEntity()).getEquipment().setItemInHand(new ItemStack(Material.GOLD_INGOT));
    }
}