package nz.valoeghese.seasonalcherries;


import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod(Constants.MOD_ID)
public class SeasonalCherriesNeoForged {

    public SeasonalCherriesNeoForged(IEventBus eventBus) {

        // Use NeoForge to bootstrap the Common mod.
        SeasonalCherries.init();

    }
}