package com.zpedroo.voltzmining.listeners;

import com.gmail.nossr50.datatypes.skills.SkillType;
import com.gmail.nossr50.events.skills.abilities.McMMOPlayerAbilityActivateEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class McMMOListeners implements Listener {

    @EventHandler
    public void onActivate(McMMOPlayerAbilityActivateEvent event) {
        if (event.getSkill() == SkillType.MINING) event.setCancelled(true);
    }
}