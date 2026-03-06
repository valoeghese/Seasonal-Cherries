package nz.valoeghese.seasonalcherries;

import net.fabricmc.api.ClientModInitializer;

public class SeasonalCherriesFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        SeasonalCherriesClient.initClient();
    }
}
