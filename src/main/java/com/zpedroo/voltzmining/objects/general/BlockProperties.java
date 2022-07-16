package com.zpedroo.voltzmining.objects.general;

import lombok.Data;
import org.bukkit.Material;

@Data
public class BlockProperties {

    private final Material material;
    private final double expAmount;
}