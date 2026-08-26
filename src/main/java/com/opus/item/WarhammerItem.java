package com.opus.item;

import com.opus.sound.ModSounds;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import java.util.List;

public class WarhammerItem extends SwordItem {
    public WarhammerItem(Tier tier,int attackDamage,float attackSpeed,Properties properties){super(tier,attackDamage,attackSpeed,properties);}
    @Override public boolean isFoil(ItemStack stack){return true;}
    @Override public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker){
        stack.hurtAndBreak(1,attacker,e->e.broadcastBreakEvent(EquipmentSlot.MAINHAND));
        target.knockback(1.3,attacker.getX()-target.getX(),attacker.getZ()-target.getZ());
        attacker.level().playSound(null,target.blockPosition(),ModSounds.HAMMER_HIT,SoundSource.PLAYERS,0.9f,.8f); return true;
    }
    @Override public InteractionResultHolder<ItemStack> use(Level level,Player player,InteractionHand hand){
        ItemStack stack=player.getItemInHand(hand);
        if(!player.getCooldowns().isOnCooldown(this)){
            CombatEffects.shockwave(player,6.0,16.0f,2.8,true);
            if(!level.isClientSide && level instanceof ServerLevel server) server.sendParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE,player.getX(),player.getY()+.2,player.getZ(),90,1.2,.25,1.2,.08);
            level.playSound(null,player.blockPosition(),ModSounds.HAMMER_ULTIMATE,SoundSource.PLAYERS,1.0f,.55f);
            player.getCooldowns().addCooldown(this,100);
        }
        return InteractionResultHolder.sidedSuccess(stack,level.isClientSide);
    }
    @Override public void appendHoverText(ItemStack stack,Level level,List<Component> tooltip,TooltipFlag flag){tooltip.add(Component.translatable("item.opusvsexe.hammer_lore").withStyle(ChatFormatting.DARK_PURPLE)); tooltip.add(Component.literal("ПКМ: Громовой раскол").withStyle(ChatFormatting.GOLD));}
}
