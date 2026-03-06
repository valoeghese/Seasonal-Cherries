package nz.valoeghese.seasonalcherries;

import nz.valoeghese.seasonalcherries.platform.Services;

// however it will be compatible with all supported mod loaders.
public class SeasonalCherries {

    // The loader specific projects are able to import and use any code from the common project.
    public static void init() {

        // It is common for all supported loaders to provide a similar feature that can not be used directly in the
        // common code. A popular way to get around this is using Java's built-in service loader feature to create
        // your own abstraction layer. You can learn more about this in our provided services class. In this example
        // we have an interface in the common code and use a loader specific implementation to delegate our call to
        // the platform specific approach.
        if (Services.PLATFORM.isModLoaded("examplemod")) {

            Constants.LOG.info("Hello to examplemod");
        }
    }
}