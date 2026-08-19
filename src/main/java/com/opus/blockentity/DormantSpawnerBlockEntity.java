package com.opus.blockentity;

import com.opus.registry.ModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class DormantSpawnerBlockEntity extends BlockEntity {
    private static final int SPAWN_INTERVAL = 120;
    private String entityId = "opusvsexe:haiku_1_5";
    private int cooldown = SPAWN_INTERVAL;

    public DormantSpawnerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.DORMANT_SPAWNER, pos, state);
    }

    public void tick(Level level, BlockPos pos, BlockState state) {
        if (level.isClientSide) return;
        boolean active = state.getValue(com.opus.block.DormantSpawnerBlock.ACTIVE);
        if (!active) return;
        if (--cooldown > 0) return;
        cooldown = SPAWN_INTERVAL;

        int nearby = level.getEntitiesOfClass(Mob.class,
                new net.minecraft.world.phys.AABB(pos).inflate(12.0),
                e -> net.minecraft.world.entity.EntityType.getKey(e.getType()).toString().equals(entityId)).size();
        if (nearby >= 4) return;

        EntityType<?> type = net.minecraft.core.registries.BuiltInRegistries.ENTITY_TYPE
                .getOptional(new net.minecraft.resources.ResourceLocation(entityId)).orElse(null);
        if (type == null) return;
        if (type.create(level) instanceof Mob mob) {
            double x = pos.getX() + 0.5 + (level.random.nextDouble() - 0.5) * 4.0;
            double z = pos.getZ() + 0.5 + (level.random.nextDouble() - 0.5) * 4.0;
            double y = pos.getY();
            mob.moveTo(x, y, z, level.random.nextFloat() * 360.0f, 0.0f);
            mob.finalizeSpawn((ServerLevel) level, level.getCurrentDifficultyAt(pos), MobSpawnType.SPAWNER, null, null);
            level.addFreshEntity(mob);
        }
    }

    public void setEntityId(String id) {
        this.entityId = id;
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putString("entity_id", entityId);
        tag.putInt("cooldown", cooldown);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        entityId = tag.getString("entity_id");
        cooldown = tag.getInt("cooldown");
    }
}