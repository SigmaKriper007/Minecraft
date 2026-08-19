package com.opus.item;

import com.opus.registry.ModItems;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import org.jetbrains.annotations.Nullable;
import java.util.List;
import java.util.UUID;

public class ShadowAssassinArmorItem extends ArmorItem {
    private static final UUID SET_SPEED_UUID = UUID.fromString("b6f0c2a9-1e4d-4b9a-9f35-8e2a5c7d0f41");
    private static final UUID SET_DAMAGE_UUID = UUID.fromString("c84d71e0-2f6a-4b7c-8a3d-7f1e6b2a9c53");

    private static final AttributeModifier SET_SPEED =
        new AttributeModifier(SET_SPEED_UUID, "shadow_assassin_set_speed", 0.15, AttributeModifier.Operation.MULTIPLY_BASE);
    private static final AttributeModifier SET_DAMAGE =
        new AttributeModifier(SET_DAMAGE_UUID, "shadow_assassin_set_damage", 0.10, AttributeModifier.Operation.MULTIPLY_BASE);

    public ShadowAssassinArmorItem(ArmorMaterial material, ArmorItem.Type type, Item.Properties properties) {
        super(material, type, properties);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, net.minecraft.world.entity.Entity entity, int slot, boolean selected) {
        super.inventoryTick(stack, level, entity, slot, selected);
        if (!(entity instanceof Player player) || level.isClientSide) {
            return;
        }
        boolean fullSet = hasFullSet(player);
        applyAttribute(player, Attributes.MOVEMENT_SPEED, SET_SPEED, fullSet);
        applyAttribute(player, Attributes.ATTACK_DAMAGE, SET_DAMAGE, fullSet);
        if (fullSet && !player.hasEffect(MobEffects.NIGHT_VISION)) {
            player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 260, 0, false, false, false));
        }
    }

    private static boolean hasFullSet(Player player) {
        ItemStack boots = player.getInventory().getArmor(0);
        ItemStack leggings = player.getInventory().getArmor(1);
        ItemStack chestplate = player.getInventory().getArmor(2);
        ItemStack helmet = player.getInventory().getArmor(3);
        return boots.is(ModItems.SHADOW_BOOTS)
            && leggings.is(ModItems.SHADOW_LEGGINGS)
            && chestplate.is(ModItems.SHADOW_CHESTPLATE)
            && helmet.is(ModItems.SHADOW_HELMET);
    }

    private static void applyAttribute(Player player, net.minecraft.world.entity.ai.attributes.Attribute attribute, AttributeModifier modifier, boolean on) {
        AttributeInstance instance = player.getAttribute(attribute);
        if (instance == null) {
            return;
        }
        if (instance.getModifier(modifier.getId()) != null) {
            instance.removeModifier(modifier.getId());
        }
        if (on) {
            instance.addTransientModifier(modifier);
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable net.minecraft.world.level.Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        tooltip.add(Component.literal("§7Комплект Shadow Assassin:"));
        tooltip.add(Component.literal("§6+15% к скорости передвижения"));
        tooltip.add(Component.literal("§6+10% к урону"));
        tooltip.add(Component.literal("§6Ночное зрение"));
    }
}