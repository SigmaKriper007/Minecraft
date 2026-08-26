package com.opus.block;

import com.opus.blockentity.AltarHeartBlockEntity;
import com.opus.blockentity.ModBlockEntities;
import com.opus.entity.haiku.HaikuOmegaEntity;
import com.opus.registry.ModEffects;
import com.opus.registry.ModEntities;
import com.opus.sound.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.TickTask;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

/**
 * Блок Сердце Алтаря - центральный блок Колизея Вечной Памяти
 * Активируется при использовании Ядра Haiku: звук, 3-секундная зарядка,
 * ослепление вспышкой всех игроков в радиусе 128 блоков и призыв Haiku Omega.
 * Block-entity нужен как «маяк» для клиентского эмбиента altar_heart_loop.
 */
public class AltarHeartBlock extends Block implements EntityBlock {
    public static final BooleanProperty ACTIVATED = BooleanProperty.create("activated");

    public AltarHeartBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(ACTIVATED, false));
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new AltarHeartBlockEntity(pos, state);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(ACTIVATED);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        ItemStack item = player.getItemInHand(hand);

        if (item.is(com.opus.registry.ModItems.ALTAR_HEART) && state.getValue(ACTIVATED)) {
            if (!level.isClientSide) {
                AABB arena = new AABB(pos).inflate(64.0D, 48.0D, 64.0D);
                boolean omegaAlive = !level.getEntitiesOfClass(HaikuOmegaEntity.class, arena,
                        boss -> boss.isAlive() && !boss.isRemoved()).isEmpty();
                if (omegaAlive) {
                    player.displayClientMessage(Component.translatable(
                            "message.opusvsexe.altar.omega_alive"), true);
                    return InteractionResult.FAIL;
                }
                if (!player.isCreative()) {
                    item.shrink(1);
                }
                level.setBlock(pos, state.setValue(ACTIVATED, false), 3);
                level.playSound(null, pos, ModSounds.BOSS_CORE_HIT, SoundSource.BLOCKS, 1.2F, 1.35F);
                if (level instanceof ServerLevel serverLevel) {
                    serverLevel.sendParticles(ParticleTypes.END_ROD,
                            pos.getX() + 0.5D, pos.getY() + 1.2D, pos.getZ() + 0.5D,
                            42, 0.45D, 0.65D, 0.45D, 0.06D);
                }
                player.displayClientMessage(Component.translatable(
                        "message.opusvsexe.altar.restored"), true);
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        if (item.getItem() == com.opus.registry.ModItems.HAIKU_CORE && !state.getValue(ACTIVATED)) {
            if (!level.isClientSide) {
                if (!player.isCreative()) {
                    item.shrink(1);
                }
                level.setBlock(pos, state.setValue(ACTIVATED, true), 3);

                Vec3 center = new Vec3(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
                level.playSound(null, pos, ModSounds.HAIKU_SUMMON, SoundSource.BLOCKS, 1.3f, 0.5f);

                if (player instanceof ServerPlayer serverPlayer) {
                    serverPlayer.getAdvancements().award(
                            serverPlayer.getServer().getAdvancements()
                                    .getAdvancement(new ResourceLocation("opusvsexe", "summon_haiku_omega")),
                            "summoned");
                }

                if (level instanceof ServerLevel serverLevel) {
                    serverLevel.getServer().tell(new TickTask(60, () -> summonHaikuOmega(serverLevel, pos, center)));
                }
            }
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }

    private void summonHaikuOmega(ServerLevel level, BlockPos pos, Vec3 center) {
        // Ослепление всех игроков в радиусе 128 блоков
        double radiusSq = 128.0 * 128.0;
        for (Player p : level.players()) {
            if (p != null && p.distanceToSqr(center) <= radiusSq) {
                p.addEffect(new MobEffectInstance(ModEffects.FLASH_BLINDNESS, 60, 0));
            }
        }

        // Взрыв частиц без урона по блокам
        level.sendParticles(ParticleTypes.EXPLOSION_EMITTER, center.x, pos.getY() + 4.0, center.z, 1, 0.0, 0.0, 0.0, 0.0);
        level.sendParticles(ParticleTypes.SONIC_BOOM, center.x, pos.getY() + 4.0, center.z, 1, 0.0, 0.0, 0.0, 0.0);

        // Призыв босса над алтарем
        HaikuOmegaEntity boss = new HaikuOmegaEntity(ModEntities.HAIKU_OMEGA, level);
        boss.setPos(center.x, pos.getY() + 4.0, center.z);
        boss.setLeashAnchor(center); // якорь регена — алтарь (задача 20)
        level.addFreshEntity(boss);

        for (Player p : level.players()) {
            p.displayClientMessage(Component.translatable("boss.summon.haiku_omega"), true);
        }
    }
}
