package com.zpedroo.voltzmining.utils.loader;

import com.zpedroo.voltzmining.objects.general.BlockProperties;
import com.zpedroo.voltzmining.utils.formatter.NumberFormatter;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.BigInteger;
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
        String[] split = str.split(",");
        if (split.length <= 2) return null;

        Material material = Material.getMaterial(split[0].toUpperCase());
        if (material == null) return null;

        double expAmount = Double.parseDouble(split[1]);
        BigInteger pointsAmount = NumberFormatter.getInstance().filter(split[2]);

        return new BlockProperties(material, expAmount, pointsAmount);
    }
}