package net.sterbendes.greeneries.blocks;

import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.FoliageColor;
import net.minecraft.world.level.GrassColor;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public abstract class ModBlockColors {

    public static final GBlockColor VARYING_GRASS_BLOCK_COLOR = (blockState, blockAndTintGetter, blockPos, i) -> {
        var color = blockAndTintGetter != null && blockPos != null
            ? BiomeColors.getAverageGrassColor(blockAndTintGetter, blockPos)
            : GrassColor.getDefaultColor();

        if (blockPos == null) return color;
        var randomSource = RandomSource.create(BlockPos.asLong(blockPos.getX(), 0, blockPos.getZ()));

        var rand1 = (randomSource.nextInt() / 22) >> 8 & 0b11111111_00000000_00000000; // red
        var rand2 = (randomSource.nextInt() / 18) >> 16 & 0b00000000_11111111_00000000; // green
        var rand3 = (randomSource.nextInt() / 24) >> 24 & 0b00000000_00000000_11111111; // blue

        return color + rand1 + rand2 + rand3;
    };

    public static final GBlockColor VARYING_FERN_BLOCK_COLOR = (blockState, blockAndTintGetter, blockPos, i) -> {
        var color = blockAndTintGetter != null && blockPos != null
            ? BiomeColors.getAverageGrassColor(blockAndTintGetter, blockPos)
            : GrassColor.getDefaultColor();

        if (blockPos == null) return color;
        var randomSource = RandomSource.create(BlockPos.asLong(blockPos.getX(), 0, blockPos.getZ()));

        var rand1 = (randomSource.nextInt() / 22) >> 8 & 0b11111111_00000000_00000000; // red
        var rand2 = (randomSource.nextInt() / 18) >> 16 & 0b00000000_11111111_00000000; // green
        var rand3 = (randomSource.nextInt() / 24) >> 24 & 0b00000000_00000000_11111111; // blue

        return color + rand1 + rand2 + rand3;
    };

    public static final GBlockColor FOLIAGE_COLOR = (blockState, blockAndTintGetter, blockPos, i) ->
        blockAndTintGetter != null && blockPos != null
            ? BiomeColors.getAverageFoliageColor(blockAndTintGetter, blockPos)
            : FoliageColor.FOLIAGE_DEFAULT;


    public interface GBlockColor {
        int getColor(BlockState blockState, @Nullable BlockAndTintGetter blockAndTintGetter, @Nullable BlockPos blockPos, int i);
    }
}
