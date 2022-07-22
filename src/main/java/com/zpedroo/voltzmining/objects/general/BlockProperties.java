package com.zpedroo.voltzmining.objects.general;

import lombok.Data;
import org.bukkit.Material;

import java.math.BigInteger;

@Data
public class BlockProperties {

    private final Material material;
    private final double expAmount;
    private final BigInteger pointsAmount;
}