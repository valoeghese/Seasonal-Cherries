package nz.valoeghese.seasonalcherries;

import net.minecraftforge.fml.common.Mod;

@Mod(Constants.MOD_ID)
public class SeasonalCherriesForge {

    public SeasonalCherriesForge() {

        // Use Forge to bootstrap the Common mod.
        SeasonalCherries.init();

    }
}