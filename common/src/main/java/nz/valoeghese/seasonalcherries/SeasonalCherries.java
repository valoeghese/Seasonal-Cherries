package nz.valoeghese.seasonalcherries;

import glitchcore.event.EventManager;
import glitchcore.util.Environment;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ColorParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.ParticleUtils;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import sereneseasons.api.season.ISeasonState;
import sereneseasons.api.season.Season;
import sereneseasons.api.season.SeasonHelper;

public class SeasonalCherries {
    // The loader specific projects are able to import and use any code from the common project.
    public static void init() {
        if (Environment.isClient()) {
            EventManager.addListener(SeasonalCherriesClient::onBlockColoursRegister);
        }
    }

    public static boolean modifyLeafParticles(Level level, BlockPos pos, RandomSource rand) {
        ISeasonState state = SeasonHelper.getSeasonState(level);
        ColorParticleOption colour = ColorParticleOption.create(ParticleTypes.TINTED_LEAVES, state.getSubSeason() == Season.SubSeason.LATE_SPRING ? 0x00BB55 : level.getClientLeafTintColor(pos));

        switch (state.getSubSeason()) {
            case EARLY_SPRING:
                // less particles
                return rand.nextInt(10) == 0;
            case MID_SPRING:
                // normal behaviour
                return true;
            case LATE_SPRING:
                // default leaf particles 1/10 as often
                if (rand.nextInt(10) == 0) {
                    ParticleUtils.spawnParticleBelow(level, pos, rand, colour);
                    return false;
                } else {
                    // less cherry particles
                    return rand.nextInt(5) <= 0;
                }
            case EARLY_WINTER:
            case MID_WINTER:
            case LATE_WINTER:
                // no leaves falling
                return false;
            default:
                // default leaf particles 1/10 as often
                if (rand.nextInt(10) == 0) {
                    ParticleUtils.spawnParticleBelow(level, pos, rand, colour);
                }
                return false;
        }
    }
}