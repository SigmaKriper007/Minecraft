package com.opus.client;
import com.mojang.blaze3d.platform.InputConstants;
import com.opus.network.ModNetwork;
import com.opusvsexe.entity.custom.ExosuitEntity;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;
public final class ExoAbilityKeybind {
 private static final String CATEGORY="key.categories.opusvsexe";
 private static final KeyMapping[] KEYS={new KeyMapping("key.opusvsexe.ability",InputConstants.Type.KEYSYM,GLFW.GLFW_KEY_F,CATEGORY),new KeyMapping("key.opusvsexe.ability_2",InputConstants.Type.KEYSYM,GLFW.GLFW_KEY_G,CATEGORY),new KeyMapping("key.opusvsexe.ability_3",InputConstants.Type.KEYSYM,GLFW.GLFW_KEY_H,CATEGORY),new KeyMapping("key.opusvsexe.armor_shockwave",InputConstants.Type.KEYSYM,GLFW.GLFW_KEY_V,CATEGORY)};
 private ExoAbilityKeybind(){}
 public static void init(){for(KeyMapping key:KEYS)KeyBindingHelper.registerKeyBinding(key); ClientTickEvents.END_CLIENT_TICK.register(client->{if(client==null||client.player==null)return; for(int i=0;i<KEYS.length;i++)while(KEYS[i].consumeClick()){var b=PacketByteBufs.create(); b.writeVarInt(i); if(i==3){ClientPlayNetworking.send(ModNetwork.ARMOR_SHOCKWAVE,b);} else if(client.player.getVehicle() instanceof ExosuitEntity)ClientPlayNetworking.send(ModNetwork.EXO_ABILITY,b);}});}
}
