package com.opus.paradise.block;

import com.opus.paradise.inventory.ParthenonForgeMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CraftingTableBlock;
import net.minecraft.world.level.block.state.BlockState;

public final class ParthenonForgeBlock extends CraftingTableBlock {
    private static final Component TITLE=Component.translatable("container.opusvsexe.parthenon_forge");
    public ParthenonForgeBlock(Properties properties){super(properties);}
    @Override public MenuProvider getMenuProvider(BlockState state,Level level,BlockPos pos){return new SimpleMenuProvider((id,inventory,player)->new ParthenonForgeMenu(id,inventory,ContainerLevelAccess.create(level,pos)),TITLE);}
}
