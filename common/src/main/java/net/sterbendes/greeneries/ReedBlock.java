package net.sterbendes.greeneries;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.LiquidBlockContainer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.Nullable;

public class ReedBlock extends DoublePlantBlock implements LiquidBlockContainer {

    public ReedBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
//        return true;
        var below = pos.below();
        BlockState stateBelow = level.getBlockState(below);
        if (state.getValue(HALF) == DoubleBlockHalf.UPPER) {
            return stateBelow.is(this) && stateBelow.getValue(HALF) == DoubleBlockHalf.LOWER;
        } else {
            if (!level.getFluidState(pos.above()).isEmpty()) return false;
            FluidState fluidState = level.getFluidState(pos);
            return mayPlaceOn(stateBelow, level, below) && fluidState.is(FluidTags.WATER) && fluidState.getAmount() == 8;
        }
    }

    @Override
    protected FluidState getFluidState(BlockState state) {
        return state.getValue(HALF) == DoubleBlockHalf.LOWER ?
            Fluids.WATER.getSource(false)
            : Fluids.EMPTY.defaultFluidState();
    }

    @Override
    public boolean canPlaceLiquid(@Nullable Player player, BlockGetter level, BlockPos pos, BlockState state, Fluid fluid) {
        return state.getValue(HALF) == DoubleBlockHalf.UPPER;
    }

    @Override
    public boolean placeLiquid(LevelAccessor level, BlockPos pos, BlockState state, FluidState fluidState) {
        return state.getValue(HALF) == DoubleBlockHalf.UPPER;
    }
}
