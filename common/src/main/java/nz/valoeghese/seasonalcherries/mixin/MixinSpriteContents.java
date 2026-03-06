package nz.valoeghese.seasonalcherries.mixin;

import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.renderer.texture.SpriteContents;
import nz.valoeghese.seasonalcherries.ExtendedSpriteContents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SpriteContents.class)
public class MixinSpriteContents implements ExtendedSpriteContents {
    @Shadow
    NativeImage[] byMipLevel;
    @Unique
    GpuBufferSlice[] seasonalcherries$agpubufferslice;

    @Inject(at = @At("HEAD"), method = "createAnimationState")
    private void onCreateAnimationState(GpuBufferSlice slice, int $$1, CallbackInfoReturnable<SpriteContents.AnimationState> cir) {
        GpuBufferSlice[] agpubufferslice = new GpuBufferSlice[this.byMipLevel.length];

        for (int i1 = 0; i1 < this.byMipLevel.length; i1++) {
            agpubufferslice[i1] = slice.slice(i1 * $$1, $$1);
        }

        this.seasonalcherries$agpubufferslice = agpubufferslice;
    }

    @Override
    public GpuBufferSlice[] seasonalcherries$getAnimatedGpubufferSlices() {
        return this.seasonalcherries$agpubufferslice;
    }

    @Override
    public NativeImage[] seasonalcherries$getMipmappedImages() {
        return this.byMipLevel;
    }
}
