package com.zpedroo.voltzmining.objects.mine;

import com.zpedroo.voltzmining.enums.EditType;
import lombok.Data;
import org.bukkit.Material;
import org.bukkit.entity.Player;

@Data
public class EditMine {

    private final Player player;
    private final Mine mine;
    private final Material blockMaterial;
    private final EditType editType;
}