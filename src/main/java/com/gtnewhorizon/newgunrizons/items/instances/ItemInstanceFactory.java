package com.gtnewhorizon.newgunrizons.items.instances;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public interface ItemInstanceFactory<T extends ItemInstance> {

    T createItemInstance(EntityPlayer player, ItemStack itemStack, int slot);
}
