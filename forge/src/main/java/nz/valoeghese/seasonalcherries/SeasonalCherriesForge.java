package nz.valoeghese.seasonalcherries;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(Constants.MOD_ID)
public class SeasonalCherriesForge {

    public SeasonalCherriesForge(FMLJavaModLoadingContext context) {

        // Use Forge to bootstrap the Common mod.
        SeasonalCherries.init();

        FMLClientSetupEvent.getBus(context.getModBusGroup()).addListener(this::onClientSetup);

    }

    private void onClientSetup(final FMLClientSetupEvent event) {
        event.enqueueWork(SeasonalCherriesClient::initClient);
    }
}