package com.zpedroo.voltzmining.objects.mine;

import com.boydti.fawe.util.EditSessionBuilder;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.function.pattern.BlockPattern;
import com.sk89q.worldedit.function.pattern.RandomPattern;
import com.sk89q.worldedit.patterns.Pattern;
import com.sk89q.worldedit.regions.Region;
import com.zpedroo.voltzmining.tasks.MineTask;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class Mine {

    private final String name;
    private final Map<Material, Double> blocks;
    private Region region;
    private int resetDelayInSeconds;
    private final MineTask mineTask;

    public Mine(String name) {
        this(name, null, null, -1);
    }

    public Mine(String name, Map<Material, Double> blocks, Region region, int resetDelayInSeconds) {
        this.name = name;
        this.blocks = blocks == null ? new HashMap<>(2) : blocks;
        this.region = region;
        this.resetDelayInSeconds = resetDelayInSeconds;
        this.mineTask = new MineTask(this);
        this.mineTask.startTask();
    }

    public void reset() {
        if (region == null || region.getWorld() == null || blocks.isEmpty()) return;

        EditSession editSession = new EditSessionBuilder(region.getWorld()).fastmode(false).checkMemory(true).autoQueue(true).build();

        try {
            RandomPattern randomPattern = new RandomPattern();
            for (Map.Entry<Material, Double> entry : blocks.entrySet()) {
                Material blockMaterial = entry.getKey();
                double percentage = entry.getValue();

                randomPattern.add(new BlockPattern(new BaseBlock(blockMaterial.getId())), percentage);
            }

            editSession.setBlocks(region, (Pattern) randomPattern);
            Operations.completeLegacy(editSession.commit());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}