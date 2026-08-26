package com.opus.mixin;

import com.opus.darkforest.item.DarkForestPlayerState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public abstract class DarkForestPlayerStateMixin extends LivingEntity implements DarkForestPlayerState {
    @Unique private long opusvsexe$darkTeleportReady;
    @Unique private boolean opusvsexe$darkSetEffects;
    @Unique private int opusvsexe$darkHasteAmplifier=-1;
    protected DarkForestPlayerStateMixin(EntityType<? extends LivingEntity> type,Level level){super(type,level);}
    @Override public long opusvsexe$getDarkTeleportReady(){return opusvsexe$darkTeleportReady;}@Override public void opusvsexe$setDarkTeleportReady(long tick){opusvsexe$darkTeleportReady=tick;}
    @Override public boolean opusvsexe$hasDarkSetEffects(){return opusvsexe$darkSetEffects;}@Override public void opusvsexe$setDarkSetEffects(boolean granted){opusvsexe$darkSetEffects=granted;}
    @Override public int opusvsexe$getDarkHasteAmplifier(){return opusvsexe$darkHasteAmplifier;}@Override public void opusvsexe$setDarkHasteAmplifier(int amplifier){opusvsexe$darkHasteAmplifier=amplifier;}
    @Inject(method="addAdditionalSaveData",at=@At("TAIL"))private void opusvsexe$saveDarkForestState(CompoundTag tag,CallbackInfo ci){tag.putLong("OpusDarkTeleportReady",opusvsexe$darkTeleportReady);}
    @Inject(method="readAdditionalSaveData",at=@At("TAIL"))private void opusvsexe$loadDarkForestState(CompoundTag tag,CallbackInfo ci){opusvsexe$darkTeleportReady=tag.getLong("OpusDarkTeleportReady");opusvsexe$darkSetEffects=false;opusvsexe$darkHasteAmplifier=-1;}
}
