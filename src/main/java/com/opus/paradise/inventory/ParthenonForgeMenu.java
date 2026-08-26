package com.opus.paradise.inventory;

import com.opus.paradise.recipe.ParthenonForgingRecipe;
import com.opus.paradise.registry.ParadiseBlocks;
import com.opus.paradise.registry.ParadiseMenus;
import com.opus.paradise.registry.ParadiseRecipes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.NonNullList;

import java.util.Optional;

public final class ParthenonForgeMenu extends AbstractContainerMenu {
    private final TransientCraftingContainer inputs=new TransientCraftingContainer(this,3,3);
    private final ResultContainer result=new ResultContainer();
    private final ContainerLevelAccess access;
    private final Player player;

    public ParthenonForgeMenu(int id,Inventory inventory){this(id,inventory,ContainerLevelAccess.NULL);}
    public ParthenonForgeMenu(int id,Inventory inventory,ContainerLevelAccess access){
        super(ParadiseMenus.PARTHENON_FORGE,id);this.access=access;this.player=inventory.player;
        addSlot(new ForgeResultSlot(inventory.player,inputs,result,0,124,35));
        for(int row=0;row<3;row++)for(int col=0;col<3;col++)addSlot(new Slot(inputs,col+row*3,30+col*18,17+row*18));
        for(int row=0;row<3;row++)for(int col=0;col<9;col++)addSlot(new Slot(inventory,col+row*9+9,8+col*18,84+row*18));
        for(int col=0;col<9;col++)addSlot(new Slot(inventory,col,8+col*18,142));
    }
    @Override public void slotsChanged(Container container){super.slotsChanged(container);if(player.level().isClientSide)return;Optional<ParthenonForgingRecipe> match=player.level().getRecipeManager().getRecipeFor(ParadiseRecipes.PARTHENON_FORGING,inputs,player.level());ItemStack output=match.map(r->r.assemble(inputs,player.level().registryAccess())).orElse(ItemStack.EMPTY);match.ifPresent(result::setRecipeUsed);result.setItem(0,output);((ServerPlayer)player).connection.send(new net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket(containerId,incrementStateId(),0,output));}
    @Override public void removed(Player player){super.removed(player);access.execute((level,pos)->clearContainer(player,inputs));}
    @Override public boolean stillValid(Player player){return stillValid(access,player,ParadiseBlocks.PARTHENON_FORGE);}
    @Override public ItemStack quickMoveStack(Player player,int index){
        ItemStack empty=ItemStack.EMPTY;Slot slot=slots.get(index);if(!slot.hasItem())return empty;ItemStack stack=slot.getItem();ItemStack copy=stack.copy();
        if(index==0){if(!moveItemStackTo(stack,10,46,true))return empty;slot.onQuickCraft(stack,copy);}
        else if(index>=10){if(!moveItemStackTo(stack,1,10,false))return empty;}
        else if(!moveItemStackTo(stack,10,46,false))return empty;
        if(stack.isEmpty())slot.setByPlayer(ItemStack.EMPTY);else slot.setChanged();if(stack.getCount()==copy.getCount())return empty;slot.onTake(player,stack);return copy;
    }

    private static final class ForgeResultSlot extends ResultSlot {
        private final CraftingContainer inputs;
        private final Player player;
        private ForgeResultSlot(Player player,CraftingContainer inputs,Container result,int index,int x,int y){super(player,inputs,result,index,x,y);this.inputs=inputs;this.player=player;}
        @Override public void onTake(Player player,ItemStack crafted){
            checkTakeAchievements(crafted);
            NonNullList<ItemStack> remaining=this.player.level().getRecipeManager().getRemainingItemsFor(ParadiseRecipes.PARTHENON_FORGING,inputs,this.player.level());
            for(int index=0;index<remaining.size();index++){
                ItemStack input=inputs.getItem(index),remainder=remaining.get(index);
                if(!input.isEmpty()){inputs.removeItem(index,1);input=inputs.getItem(index);}
                if(remainder.isEmpty())continue;
                if(input.isEmpty())inputs.setItem(index,remainder);
                else if(ItemStack.isSameItemSameTags(input,remainder)){remainder.grow(input.getCount());inputs.setItem(index,remainder);}
                else if(!this.player.getInventory().add(remainder))this.player.drop(remainder,false);
            }
        }
    }
}
