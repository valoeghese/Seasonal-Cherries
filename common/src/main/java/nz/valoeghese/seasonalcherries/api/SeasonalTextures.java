package nz.valoeghese.seasonalcherries.api;

import net.minecraft.resources.Identifier;
import sereneseasons.api.season.Season;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

public final class SeasonalTextures {
    private SeasonalTextures() {
    }

    private static final Map<Identifier, Function<Season.SubSeason, Identifier>> seasonalTextures = new HashMap<>();

    /**
     * Make a texture update with each subseason.
     * @param texture the location of the texture to update. E.g. {@code minecraft:block/cherry_leaves}.
     * @param source a map of which texture to update it to each season. Keep in mind if you return the original texture
     *              identifier, it will load the actual image: image loading here is not affected by the current seasonal texture.
     */
    public static void setSeasonalTexture(Identifier texture, Function<Season.SubSeason, Identifier> source) {
        seasonalTextures.put(texture, source);
    }

//    /**
//     * Remove the seasonal texture for the given identifier.
//     * @param texture the texture to clear seasonality for.
//     */
//    public static void clearSeasonalTexture(Identifier texture) {
//        seasonalTextures.remove(texture);
//        cleared.add(texture);
//    }

    /**
     * Iterate over each sesaonal texture for a season. Used by the implementation.
     * @param season the season for which to receive textures.
     * @param update callback to consume pairs of (dest, src).
     */
    public static void forEach(Season.SubSeason season, BiConsumer<Identifier, Identifier> update) {
        seasonalTextures.forEach((dest, srcFn) -> update.accept(dest, srcFn.apply(season)));
    }
}
