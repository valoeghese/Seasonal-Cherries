/*
 * Copyright (c) 2026 Valoeghese
 * Licensed under the BSD 3-Clause License.
 * See the LICENSE file in the project root for license information.
 */

package nz.valoeghese.seasonalcherries;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.*;
import glitchcore.event.client.RegisterColorsEvent;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.data.AtlasIds;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Mth;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.FoliageColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import nz.valoeghese.seasonalcherries.api.SeasonalTextures;
import nz.valoeghese.seasonalcherries.mixin.AccessorTextureAtlas;
import org.lwjgl.system.MemoryUtil;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import sereneseasons.api.season.ISeasonColorProvider;
import sereneseasons.api.season.ISeasonState;
import sereneseasons.api.season.Season;
import sereneseasons.api.season.SeasonHelper;
import sereneseasons.init.ModConfig;
import sereneseasons.init.ModTags;
import sereneseasons.season.SeasonTime;
import sereneseasons.util.SeasonColorUtil;

import javax.annotation.Nullable;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.OptionalInt;

public class SeasonalCherriesClient {
    // Seasonal Textures
    private static final Identifier CHERRY_LEAVES_NATURAL = Identifier.parse("seasonalcherries:block/cherry_leaves_natural");
    // Seasonal Texture Sources
    private static final Identifier CHERRY_LEAVES_EARLY = Identifier.parse("seasonalcherries:block/cherry_leaves_early_spring");
    private static final Identifier CHERRY_LEAVES = Identifier.parse("minecraft:block/cherry_leaves");
    private static final Identifier CHERRY_LEAVES_LATE = Identifier.parse("seasonalcherries:block/cherry_leaves_late_spring");
    private static final Identifier OAK_LEAVES = Identifier.parse("minecraft:block/oak_leaves");

    public static Season.SubSeason clientSeason;

    public static void initClient() {
        SeasonalTextures.setSeasonalTexture(CHERRY_LEAVES_NATURAL, season -> switch (season) {
            case EARLY_SPRING -> CHERRY_LEAVES_EARLY;
            case MID_SPRING -> CHERRY_LEAVES;
            case LATE_SPRING -> CHERRY_LEAVES_LATE;
            default -> OAK_LEAVES;
        });
    }

    public static void onBlockColoursRegister(RegisterColorsEvent.Block event) {
        event.register((BlockState state, @Nullable BlockAndTintGetter dimensionReader, @Nullable BlockPos pos, int tintIndex) -> {
            Level level = Minecraft.getInstance().player.level();
            ResourceKey<Level> dimension = Minecraft.getInstance().player.level().dimension();
            int colour = 0xFFFFFF;
            if (state.getValue(LeavesBlock.PERSISTENT)) {
                return colour;
            }

            if (level != null && pos != null && ModConfig.seasons.isDimensionWhitelisted(dimension))
            {
                Holder<Biome> biome = level.getBiome(pos);

                if (!biome.is(ModTags.Biomes.BLACKLISTED_BIOMES))
                {
                    ISeasonState calendar = SeasonHelper.getSeasonState(level);
                    ISeasonColorProvider colorProvider = biome.is(ModTags.Biomes.TROPICAL_BIOMES) ? calendar.getTropicalSeason() : calendar.getSubSeason();

                    if (calendar.getSeason() == Season.WINTER) {
                        colour = 0xb78d5f; // brown
                    } else if (calendar.getSeason() != Season.SPRING) {
                        colour = SeasonColorUtil.mixColours(colorProvider.getFoliageOverlay(), FoliageColor.FOLIAGE_DEFAULT, calendar.getSeason() == Season.AUTUMN ? 0.33F : 0.67f);
                    }
                }
            }

            return colour;
        }, Blocks.CHERRY_LEAVES);
    }

    public static void onDHColour(ClientLevel level, BlockState blockState, CallbackInfoReturnable<Integer> result) {
        if (blockState != null && blockState.is(Blocks.CHERRY_LEAVES) && !blockState.getValue(LeavesBlock.PERSISTENT)) {
            ISeasonState season = SeasonHelper.getSeasonState(level);

            if (season.getSeason() == Season.SPRING) {
                int colour = switch (season.getSubSeason()) {
                    case EARLY_SPRING -> 0xFF745A3F;
                    case LATE_SPRING ->  0xFF526841;
                    default -> 0xFFE6B2CB;
                };
                result.setReturnValue(colour);
            }
        }
    }

    public static void onSeasonChangedClient(Season.SubSeason subSeason) {
        Constants.LOG.info("Updating seasonal textures");

        TextureAtlas blockAtlas = Minecraft.getInstance().getAtlasManager().getAtlasOrThrow(AtlasIds.BLOCKS);
        AccessorTextureAtlas accessorBlockAtlas = (AccessorTextureAtlas) blockAtlas;

        final int mipCount = accessorBlockAtlas.getMipLevelCount();

        Object2IntOpenHashMap<TextureAtlasSprite> notAnimated = new Object2IntOpenHashMap<>();
        notAnimated.defaultReturnValue(-1);

        List<TextureAtlasSprite> notAnimatedList = accessorBlockAtlas.getSprites().stream().filter(p_460298_ -> !p_460298_.contents().isAnimated()).toList();
        for (int i = 0; i < notAnimatedList.size(); i++) {
            notAnimated.put(notAnimatedList.get(i), i);
        }

        int alignedUBOSize = Mth.roundToward(SpriteContents.UBO_SIZE, RenderSystem.getDevice().getUniformOffsetAlignment());
        int stride = alignedUBOSize * mipCount;
        ByteBuffer nonAnimatedByteBuffer = MemoryUtil.memAlloc(notAnimated.size() * stride);

        // Prepare Source and Destination Textures
        Map<TextureAtlasSprite, GpuTextureView[]> viewsCreated = new HashMap<>();

        SeasonalTextures.forEach(subSeason, (dest, src) -> {
            TextureAtlasSprite spriteDest = blockAtlas.getSprite(dest);
            TextureAtlasSprite spriteSrc = blockAtlas.getSprite(src);

            // Upload UBO coordinates to write to
            int cId = notAnimated.getInt(spriteDest);
            if (cId != -1) {
                spriteDest.uploadSpriteUbo(nonAnimatedByteBuffer, cId * stride, mipCount - 1, accessorBlockAtlas.getWidth(), accessorBlockAtlas.getHeight(), alignedUBOSize);
            }

            // Load texture and save mapping
            GpuTextureView[] textures = createTextureView(spriteSrc, mipCount);
            viewsCreated.put(spriteDest, textures);
        });

        // Render textures to atlas
        GpuSampler gpusampler = RenderSystem.getSamplerCache().getClampToEdge(FilterMode.NEAREST, true);

        try (GpuBuffer nonAnimatedBuffer = RenderSystem.getDevice().createBuffer(() -> "SpriteAnimationInfo", 128, nonAnimatedByteBuffer)) {
            for (int i = 0; i < mipCount; i++) {
                final int mip = i;

                try (RenderPass renderpass = RenderSystem.getDevice()
                        .createCommandEncoder()
                        .createRenderPass(() -> "Seasonal Texture Change", accessorBlockAtlas.getMipViews()[mip], OptionalInt.empty())) {
                    renderpass.setPipeline(RenderPipelines.ANIMATE_SPRITE_BLIT);

                    viewsCreated.forEach((dest, src) -> {
                        // get slice
                        int index = notAnimated.getInt(dest);

                        GpuBufferSlice bufferDest;
                        if (index == -1) {
                            ExtendedSpriteContents contentsDest = (ExtendedSpriteContents) dest.contents();
                            bufferDest = contentsDest.seasonalcherries$getAnimatedGpubufferSlices()[mip];
                        } else {
                            bufferDest = nonAnimatedBuffer.slice(index * stride + mip * alignedUBOSize, SpriteContents.UBO_SIZE);
                        }

                        drawTexture(bufferDest, gpusampler, renderpass, mip, src);
                    });
                }
            }
        }

        for (GpuTextureView[] viewSet : viewsCreated.values()) {
            for (GpuTextureView view : viewSet) {
                view.close();
                view.texture().close();
            }
        }

        MemoryUtil.memFree(nonAnimatedByteBuffer);
    }

    private static GpuTextureView[] createTextureView(TextureAtlasSprite spriteSrc, int mipCount) {
        // see TextureAtlas#uploadInitialContents
        GpuTexture gputexture = RenderSystem.getDevice().createTexture(
                () -> spriteSrc.contents().name().toString(),
                5,
                TextureFormat.RGBA8,
                spriteSrc.contents().width(),
                spriteSrc.contents().height(),
                1,
                mipCount
        );
        GpuTextureView[] views = new GpuTextureView[mipCount];

        for (int l = 0; l < mipCount; l++) {
            spriteSrc.uploadFirstFrame(gputexture, l);
            // RenderSystem.getDevice().createCommandEncoder().writeToTexture($$0, /*contentsSrc*/ this.byMipLevel[$$1], $$1, 0, 0, 0, this.width >> $$1, this.height >> $$1, 0, 0);
            views[l] = RenderSystem.getDevice().createTextureView(gputexture);
        }

        return views;
    }

    private static void drawTexture(GpuBufferSlice sliceDest, GpuSampler sampler, RenderPass pass, int mip, GpuTextureView[] src) {
        // Render
        pass.bindTexture("Sprite", src[mip], sampler);
        pass.setUniform("SpriteAnimationInfo", sliceDest);
        // param 0: progress towards next frame in blending textures
        pass.draw(0, 6);
    }
}
