package nz.valoeghese.seasonalcherries;

import glitchcore.event.EventManager;
import glitchcore.util.Environment;

public class SeasonalCherries {
    // The loader specific projects are able to import and use any code from the common project.
    public static void init() {
        if (Environment.isClient()) {
            EventManager.addListener(SeasonalCherriesClient::onBlockColoursRegister);
        }
    }
}