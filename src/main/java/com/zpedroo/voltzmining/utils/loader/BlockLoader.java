package com.zpedroo.voltzmining.utils.loader;

import com.zpedroo.voltzmining.objects.general.BlockProperties;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class BlockLoader {

    @NotNull
    public static List<BlockProperties> load(List<String> list) {
        List<BlockProperties> ret = new ArrayList<>(list.size());
        for (String str : list) {
            ret.add(load(str));
        }

        return ret;
    }

    @Nullable
    public static BlockProperties load(String str) {
        String[] split = str.split(":");
        if (split.length <= 1) return null;

        Material material = Material.getMaterial(split[0].toUpperCase());
        if (material == null) return null;

        double xp = Double.parseDouble(split[1]);

        return new BlockProperties(material, xp);
    }
}