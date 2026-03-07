/*
 * Copyright (c) 2026 Valoeghese
 * Licensed under the BSD 3-Clause License.
 * See the LICENSE file in the project root for license information.
 */

package nz.valoeghese.seasonalcherries.mixin;

import com.seibel.distanthorizons.core.dataObjects.fullData.sources.FullDataSourceV2;
import com.seibel.distanthorizons.core.pos.blockPos.DhBlockPos;
import com.seibel.distanthorizons.core.wrapperInterfaces.block.IBlockStateWrapper;
import com.seibel.distanthorizons.core.wrapperInterfaces.world.IBiomeWrapper;
import loaderCommon.neoforge.com.seibel.distanthorizons.common.wrappers.block.BlockStateWrapper;
import loaderCommon.neoforge.com.seibel.distanthorizons.common.wrappers.world.ClientLevelWrapper;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.level.block.state.BlockState;
import nz.valoeghese.seasonalcherries.SeasonalCherriesClient;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(ClientLevelWrapper.class)
public class NeoforgeDistantHorizonsCompat {
    @Shadow @Final private ClientLevel level;

    @Inject(
            method = "getBlockColor",
            at = @At("HEAD"),
            cancellable = true
    )
    private void onGetColour(DhBlockPos blockPos, IBiomeWrapper biome, FullDataSourceV2 fullDataSource, IBlockStateWrapper blockWrapper,
                             CallbackInfoReturnable<Integer> cir) {
        BlockState blockState = ((BlockStateWrapper) blockWrapper).blockState;
        SeasonalCherriesClient.onDHColour(level, blockState, cir);
    }
}
