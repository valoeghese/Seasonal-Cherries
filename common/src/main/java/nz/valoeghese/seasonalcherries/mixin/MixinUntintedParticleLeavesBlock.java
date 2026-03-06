package nz.valoeghese.seasonalcherries.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ColorParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.ParticleUtils;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.UntintedParticleLeavesBlock;
import nz.valoeghese.seasonalcherries.SeasonalCherries;
import nz.valoeghese.seasonalcherries.SeasonalCherriesClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import sereneseasons.api.season.ISeasonState;
import sereneseasons.api.season.SeasonHelper;

/**
 * Change cherry leaf particles when it's not mid spring.
 */
@Mixin(UntintedParticleLeavesBlock.class)
public class MixinUntintedParticleLeavesBlock {
    @Inject(method = "spawnFallingLeavesParticle",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/ParticleUtils;spawnParticleBelow(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/util/RandomSource;Lnet/minecraft/core/particles/ParticleOptions;)V",
                    ordinal = 0),
            cancellable = true)
    private void onSpawnParticle(Level level, BlockPos pos, RandomSource rand, CallbackInfo ci) {
        if ((Object) this == Blocks.CHERRY_LEAVES) {
            if (!SeasonalCherries.modifyLeafParticles(level, pos, rand)) {
                ci.cancel();
            }
        }
    }
}
