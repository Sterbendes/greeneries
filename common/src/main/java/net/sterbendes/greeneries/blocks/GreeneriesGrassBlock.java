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

public class GreeneriesGrassBlock extends TallGrassBlock {

    private final Supplier<@Nullable BlockState> nextBonemealState;

    public GreeneriesGrassBlock(Block copyProperties, @Nullable Holder<Block> nextBonemealState) {
        super(Properties.ofFullCopy(copyProperties));
        this.nextBonemealState = () -> nextBonemealState == null ? null : nextBonemealState.value().defaultBlockState();
    }

    public GreeneriesGrassBlock(Block copyProperties, @Nullable Block nextBonemealState) {
        super(Properties.ofFullCopy(copyProperties));
        this.nextBonemealState = () -> nextBonemealState == null ? null : nextBonemealState.defaultBlockState();
    }


    @Override
    public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state) {
        if (replaceWithNextState(level, pos)) return;

        if (tryPlace(level, pos.north(), state)) return;
        if (tryPlace(level, pos.east(), state)) return;
        if (tryPlace(level, pos.south(), state)) return;
        if (tryPlace(level, pos.west(), state)) return;

        if (tryPlace(level, pos.north().east(), state)) return;
        if (tryPlace(level, pos.north().west(), state)) return;
        if (tryPlace(level, pos.south().east(), state)) return;
        if (tryPlace(level, pos.south().west(), state)) return;
    }

    private boolean replaceWithNextState(ServerLevel level, BlockPos pos) {
        var nextState = nextBonemealState.get();
        if (nextState == null) return false;

        return tryPlace(level, pos.north(), nextState);
    }

    private boolean tryPlace(ServerLevel level, BlockPos pos, BlockState state) {
        if (!state.canSurvive(level, pos)) return false;
        if (!level.getBlockState(pos).canBeReplaced()) return false;

        return level.setBlock(pos, state, 2);
    }
}
