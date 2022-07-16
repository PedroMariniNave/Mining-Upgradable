package com.zpedroo.voltzmining.objects.general;

import com.zpedroo.voltzmining.enums.EnchantProperty;
import com.zpedroo.voltzmining.utils.formatter.NumberFormatter;
import com.zpedroo.voltzmining.utils.pickaxe.PickaxeUtils;
import lombok.Data;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
public class Enchant {

    private final String name;
    private final int initialLevel;
    private final int maxLevel;
    private final int requiredLevel;
    private final int costPerLevel;
    private final Map<EnchantProperty, Number> enchantProperties;
    private final Map<EnchantProperty, Number> initialValues;

    public Number getPropertyEffect(EnchantProperty property) {
        return enchantProperties.getOrDefault(property, 0);
    }

    public Number getPropertyInitialValue(EnchantProperty property) {
        return initialValues.getOrDefault(property, 0);
    }

    public List<String> getPlaceholders() {
        List<String> placeholders = new ArrayList<>();
        placeholders.add("{" + name + "}");
        placeholders.add("{" + name + "_next}");

        for (EnchantProperty property : EnchantProperty.values()) {
            placeholders.add("{" + name + "_" + property.name().toLowerCase() + "}");
            placeholders.add("{" + name + "_next_" + property.name().toLowerCase() + "}");
        }

        return placeholders;
    }

    public List<String> getReplacers(@NotNull ItemStack item) {
        List<String> replacers = new ArrayList<>();

        int quality = PickaxeUtils.getItemQuality(item);
        int level = PickaxeUtils.getEnchantmentLevel(item, this);
        int nextLevel = level + 1;

        replacers.add(NumberFormatter.getInstance().convertToRoman(level));
        replacers.add(NumberFormatter.getInstance().convertToRoman(nextLevel));

        for (EnchantProperty property : EnchantProperty.values()) {
            replacers.add(NumberFormatter.getInstance().formatDecimal(PickaxeUtils.getEnchantEffectByLevel(this, property, level, quality)));
            replacers.add(NumberFormatter.getInstance().formatDecimal(PickaxeUtils.getEnchantEffectByLevel(this, property, nextLevel, quality)));
        }

        return replacers;
    }
}