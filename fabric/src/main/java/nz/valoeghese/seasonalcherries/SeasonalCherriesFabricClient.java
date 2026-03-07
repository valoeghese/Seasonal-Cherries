/*
 * Copyright (c) 2026 Valoeghese
 * Licensed under the BSD 3-Clause License.
 * See the LICENSE file in the project root for license information.
 */

package nz.valoeghese.seasonalcherries;

import net.fabricmc.api.ClientModInitializer;

public class SeasonalCherriesFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        SeasonalCherriesClient.initClient();
    }
}
