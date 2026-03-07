/*
 * Copyright (c) 2026 Valoeghese
 * Licensed under the BSD 3-Clause License.
 * See the LICENSE file in the project root for license information.
 */

package nz.valoeghese.seasonalcherries.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import nz.valoeghese.seasonalcherries.SeasonalCherriesClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import sereneseasons.season.SeasonHandlerClient;
import sereneseasons.season.SeasonTime;

@Mixin(SeasonHandlerClient.class)
public class MixinSeasonHandlerClient {
    @Inject(method = "onClientTick", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/LevelRenderer;allChanged()V"
    ))
    private static void init(CallbackInfo info, @Local SeasonTime calendar) {
        SeasonalCherriesClient.clientSeason = calendar.getSubSeason();
        SeasonalCherriesClient.onSeasonChangedClient(calendar.getSubSeason());
    }
}