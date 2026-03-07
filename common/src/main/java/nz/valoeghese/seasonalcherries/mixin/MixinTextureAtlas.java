/*
 * Copyright (c) 2026 Valoeghese
 * Licensed under the BSD 3-Clause License.
 * See the LICENSE file in the project root for license information.
 */

package nz.valoeghese.seasonalcherries.mixin;

import net.minecraft.client.renderer.texture.TextureAtlas;
import nz.valoeghese.seasonalcherries.SeasonalCherriesClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TextureAtlas.class)
public class MixinTextureAtlas {
    @Inject(at = @At("RETURN"), method = "uploadInitialContents")
    private void afterUploadInitialContents(CallbackInfo ci) {
        if (SeasonalCherriesClient.clientSeason != null) {
            SeasonalCherriesClient.onSeasonChangedClient(SeasonalCherriesClient.clientSeason);
        }
    }
}
