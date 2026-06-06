package net.sterbendes.greeneries.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.TallGrassBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

/**
 * A custom grass class with bonemealing behavior:
 * Either a nextBonemealState is set, which the current block will be replaced with when bonemeal is applied.
// * ((Or, the block will clone itself to an adjacent block when bonemeal is applied, preferably replacing air or
// * replacing any replaceable block.))
 */
public class GreeneriesGrassBlock extends TallGrassBlock {

    private final Supplier<@Nullable BlockState> nextBonemealState;

    public GreeneriesGrassBlock(Block copyProperties, Supplier<@Nullable BlockState> nextBonemealState) {
        super(Properties.ofFullCopy(copyProperties));
        this.nextBonemealState = nextBonemealState;
    }

    public GreeneriesGrassBlock(Block copyProperties, @Nullable Holder<Block> nextBonemealState) {
        this(copyProperties, () -> nextBonemealState == null ? null : nextBonemealState.value().defaultBlockState());
    }

    public GreeneriesGrassBlock(Block copyProperties, @Nullable Block nextBonemealState) {
        this(copyProperties, () -> nextBonemealState == null ? null : nextBonemealState.defaultBlockState());
    }


    @Override
    public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state) {
        if (replaceWithNextState(level, pos)) return;

//        if (tryPlace(level, pos.north(), state, true)) return;
//        if (tryPlace(level, pos.east(), state, true)) return;
//        if (tryPlace(level, pos.south(), state, true)) return;
//        if (tryPlace(level, pos.west(), state, true)) return;
//
//        if (tryPlace(level, pos.north(), state, false)) return;
//        if (tryPlace(level, pos.east(), state, false)) return;
//        if (tryPlace(level, pos.south(), state, false)) return;
//        if (tryPlace(level, pos.west(), state, false)) return;
//
//        if (tryPlace(level, pos.north().east(), state, false)) return;
//        if (tryPlace(level, pos.north().west(), state, false)) return;
//        if (tryPlace(level, pos.south().east(), state, false)) return;
//        if (tryPlace(level, pos.south().west(), state, false)) return;
    }

    private boolean replaceWithNextState(ServerLevel level, BlockPos pos) {
        var nextState = nextBonemealState.get();
        if (nextState == null) return false;

        return tryPlace(level, pos, nextState, false);
    }

    private boolean tryPlace(ServerLevel level, BlockPos pos, BlockState state, boolean onlyIfAir) {
        if (onlyIfAir && !level.getBlockState(pos).isAir()) return false;
        if (!state.canSurvive(level, pos)) return false;
        if (!level.getBlockState(pos).canBeReplaced()) return false;

        return level.setBlock(pos, state, 2);
    }
}
