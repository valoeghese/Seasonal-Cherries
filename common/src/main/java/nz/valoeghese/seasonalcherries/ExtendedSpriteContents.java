package nz.valoeghese.seasonalcherries;

import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.platform.NativeImage;

public interface ExtendedSpriteContents {
    GpuBufferSlice[] seasonalcherries$getAnimatedGpubufferSlices();

    NativeImage[] seasonalcherries$getMipmappedImages();
}
