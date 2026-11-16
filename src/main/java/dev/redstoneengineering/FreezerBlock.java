package dev.redstoneengineering;

import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

/**
 * Prototype freezer: while powered, freezes non-player entities in the entire
 * 16x16 chunk containing this block. The chunk remains loaded and block/redstone
 * ticks continue normally.
 */
public final class FreezerBlock extends Block {
    private static final String FROZEN_TAG = "redstoneengineering_freezer_frozen";

    public FreezerBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos,
                                   Block sourceBlock, net.minecraft.world.level.redstone.Orientation orientation,
                                   boolean movedByPiston) {
        super.neighborChanged(state, level, pos, sourceBlock, orientation, movedByPiston);
        if (level instanceof ServerLevel serverLevel) {
            serverLevel.scheduleTick(pos, this, 1);
        }
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        super.onPlace(state, level, pos, oldState, movedByPiston);
        if (level instanceof ServerLevel serverLevel) {
            serverLevel.scheduleTick(pos, this, 1);
        }
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        boolean powered = level.hasNeighborSignal(pos);
        int chunkMinX = pos.getX() >> 4 << 4;
        int chunkMinZ = pos.getZ() >> 4 << 4;
        AABB chunkBox = new AABB(chunkMinX, level.getMinY(), chunkMinZ,
                chunkMinX + 16, level.getMaxY(), chunkMinZ + 16);
        List<Entity> entities = level.getEntitiesOfClass(Entity.class, chunkBox,
                entity -> !(entity instanceof Player));

        for (Entity entity : entities) {
            if (powered) {
                if (entity instanceof Mob mob) {
                    if (!mob.isNoAi()) {
                        mob.setNoAi(true);
                        mob.addTag(FROZEN_TAG);
                    }
                }
                entity.setDeltaMovement(Vec3.ZERO);
                entity.hasImpulse = true;
            } else if (entity.getTags().contains(FROZEN_TAG)) {
                if (entity instanceof Mob mob) {
                    mob.setNoAi(false);
                }
                entity.removeTag(FROZEN_TAG);
                entity.setDeltaMovement(Vec3.ZERO);
            }
        }

        // Keep checking while powered; one extra pass after power-off restores mobs.
        if (powered || entities.stream().anyMatch(e -> e.getTags().contains(FROZEN_TAG))) {
            level.scheduleTick(pos, this, 1);
        }
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (!state.is(newState.getBlock()) && level instanceof ServerLevel serverLevel) {
            int chunkMinX = pos.getX() >> 4 << 4;
            int chunkMinZ = pos.getZ() >> 4 << 4;
            AABB chunkBox = new AABB(chunkMinX, serverLevel.getMinY(), chunkMinZ,
                    chunkMinX + 16, serverLevel.getMaxY(), chunkMinZ + 16);
            for (Entity entity : serverLevel.getEntitiesOfClass(Entity.class, chunkBox,
                    e -> e.getTags().contains(FROZEN_TAG))) {
                if (entity instanceof Mob mob) {
                    mob.setNoAi(false);
                }
                entity.removeTag(FROZEN_TAG);
                entity.setDeltaMovement(Vec3.ZERO);
            }
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }
}
