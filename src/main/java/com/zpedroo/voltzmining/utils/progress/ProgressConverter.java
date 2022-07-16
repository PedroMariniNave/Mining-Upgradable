package com.zpedroo.voltzmining.utils.progress;

import com.google.common.base.Strings;
import com.zpedroo.voltzmining.utils.config.Progress;
import com.zpedroo.voltzmining.utils.config.Quality;
import com.zpedroo.voltzmining.utils.formula.ExperienceManager;

public class ProgressConverter {

    public static String convertExperience(double experience) {
        double percentage = getPercentage(experience) / 100;
        int completedProgressBars = (int) (Progress.DISPLAY_AMOUNT * percentage);
        int incompleteProgressBars = Progress.DISPLAY_AMOUNT - completedProgressBars;

        return Progress.COMPLETE_COLOR + Strings.repeat(Progress.SYMBOL, completedProgressBars) +
                Progress.INCOMPLETE_COLOR + Strings.repeat(Progress.SYMBOL, incompleteProgressBars);
    }

    public static String convertQuality(int qualityLevel) {
        if (qualityLevel >= Quality.MAX) return Quality.COMPLETE_COLOR + Strings.repeat(Quality.SYMBOL, Quality.MAX);

        double percentage = (double) qualityLevel / Quality.MAX;
        int completedProgressBars = (int) (Quality.MAX * percentage);
        int incompleteProgressBars = Quality.MAX - completedProgressBars;

        return Quality.COMPLETE_COLOR + Strings.repeat(Quality.SYMBOL, completedProgressBars) +
                Quality.INCOMPLETE_COLOR + Strings.repeat(Quality.SYMBOL, incompleteProgressBars);
    }

    public static double getPercentage(double experience) {
        int level = ExperienceManager.getLevel(experience);
//       if (level >= Settings.MAX_LEVEL) return 100;

        double xpToActualLevel = ExperienceManager.getFullLevelExperience(level-1);
        double xpToNextLevel = ExperienceManager.getFullLevelExperience(level);

        double requiredXPToUpgradeLevel = xpToNextLevel - xpToActualLevel;
        double has = experience - xpToActualLevel;

        double percentage = (has / requiredXPToUpgradeLevel) * 100;

        return percentage > 0 ? percentage : 0;
    }
}