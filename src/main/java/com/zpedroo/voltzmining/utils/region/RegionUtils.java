package com.zpedroo.voltzmining.utils.region;

import com.sk89q.worldedit.Vector;
import com.zpedroo.voltzmining.managers.DataManager;
import com.zpedroo.voltzmining.objects.mine.Mine;
import org.bukkit.Location;

public class RegionUtils {

    public static boolean isOreArea(Location location) {
        for (Mine mine : DataManager.getInstance().getCache().getMines().values()) {
            if (isOreArea(location, mine)) return true;
        }

        return false;
    }

    public static boolean isOreArea(Location location, Mine mine) {
        if (mine.getRegion() == null) return false;

        Vector min = mine.getRegion().getMinimumPoint();
        Vector max = mine.getRegion().getMaximumPoint();

        return min.getX() <= location.getX()
                && min.getY() <= location.getY()
                && min.getZ() <= location.getZ()
                && max.getX() >= location.getX()
                && max.getY() >= location.getY()
                && max.getZ() >= location.getZ();
    }
}