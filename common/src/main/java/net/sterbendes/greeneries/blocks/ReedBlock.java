package net.sterbendes.greeneries.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.LiquidBlockContainer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class ReedBlock extends DoublePlantBlock implements BucketPickup, LiquidBlockContainer {

    public static final Property<Boolean> WATERLOGGED = BlockStateProperties.WATERLOGGED;

    public ReedBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(WATERLOGGED, false));
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        var blockPos = context.getClickedPos();
        var level = context.getLevel();

        if (blockPos.getY() >= level.getMaxBuildHeight() - 1 ) return null;
        if (!level.getBlockState(blockPos.above()).canBeReplaced(context)) return null;
        if (level.getFluidState(blockPos).isSourceOfType(Fluids.WATER))
            return defaultBlockState().setValue(WATERLOGGED, true);
        if (level.getFluidState(blockPos).isEmpty())
            return defaultBlockState();
        return null;
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        var below = pos.below();
        BlockState stateBelow = level.getBlockState(below);
        if (state.getValue(HALF) == DoubleBlockHalf.UPPER) {
            return stateBelow.is(this) && stateBelow.getValue(HALF) == DoubleBlockHalf.LOWER;
        } else {
            if (!level.getFluidState(pos.above()).isEmpty()) return false;
            FluidState fluidState = level.getFluidState(pos);
            return (fluidState.is(FluidTags.WATER) || fluidState.isEmpty()) && mayPlaceOn(stateBelow, level, below);
        }
    }

    @Override
    protected boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos) {
        return state.is(ModBlockTags.reed_may_place_on);
    }

    @Override
    protected FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ?
            Fluids.WATER.getSource(false)
            : Fluids.EMPTY.defaultFluidState();
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean canPlaceLiquid(@Nullable Player player, BlockGetter level, BlockPos pos, BlockState state,
                                  Fluid fluid) {
        return fluid.is(FluidTags.WATER) && state.getValue(HALF) == DoubleBlockHalf.LOWER;
    }

    @Override
    public boolean placeLiquid(LevelAccessor level, BlockPos pos, BlockState state, FluidState fluidState) {
        if (fluidState.is(FluidTags.WATER) && state.getValue(HALF) == DoubleBlockHalf.LOWER && !state.getValue(WATERLOGGED)) {
            if (!level.isClientSide()) {
                level.setBlock(pos, state.setValue(BlockStateProperties.WATERLOGGED, true), 3);
                level.scheduleTick(pos, fluidState.getType(), fluidState.getType().getTickDelay(level));
            }

            return true;
        }
        return false;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(WATERLOGGED);
    }

    @Override
    public ItemStack pickupBlock(@Nullable Player player, LevelAccessor level, BlockPos pos, BlockState state) {
        if (state.getValue(BlockStateProperties.WATERLOGGED)) {
            level.setBlock(pos, state.setValue(BlockStateProperties.WATERLOGGED, false), 3);

            return new ItemStack(Items.WATER_BUCKET);
        } else {
            return ItemStack.EMPTY;
        }
    }

    @Override
    public Optional<SoundEvent> getPickupSound() {
        return Fluids.WATER.getPickupSound();
    }
}
