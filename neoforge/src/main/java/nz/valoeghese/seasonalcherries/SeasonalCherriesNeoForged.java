package nz.valoeghese.seasonalcherries;


import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

@Mod(Constants.MOD_ID)
public class SeasonalCherriesNeoForged {

    public SeasonalCherriesNeoForged(IEventBus eventBus) {

        // Use NeoForge to bootstrap the Common mod.
        SeasonalCherries.init();

        eventBus.addListener(this::onClientSetup);

    }

    private void onClientSetup(final FMLClientSetupEvent event) {
        event.enqueueWork(SeasonalCherriesClient::initClient);
    }
}