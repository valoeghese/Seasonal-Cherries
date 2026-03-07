/*
 * Copyright (c) 2026 Valoeghese
 * Licensed under the BSD 3-Clause License.
 * See the LICENSE file in the project root for license information.
 */

package nz.valoeghese.seasonalcherries;

import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.platform.NativeImage;

public interface ExtendedSpriteContents {
    GpuBufferSlice[] seasonalcherries$getAnimatedGpubufferSlices();

    NativeImage[] seasonalcherries$getMipmappedImages();
}
