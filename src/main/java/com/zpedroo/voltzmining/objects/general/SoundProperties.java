package com.zpedroo.voltzmining.objects.general;

import lombok.Data;
import org.bukkit.Sound;

@Data
public class SoundProperties {

    private final boolean enabled;
    private final Sound sound;
    private final float volume;
    private final float pitch;
}