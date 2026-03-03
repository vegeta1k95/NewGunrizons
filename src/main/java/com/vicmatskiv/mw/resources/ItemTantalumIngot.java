package com.vicmatskiv.mw.resources;

import net.minecraft.item.Item;

import com.vicmatskiv.mw.ModernWarfareMod;

public class ItemTantalumIngot extends Item {

    public ItemTantalumIngot() {
        this.setMaxStackSize(64);
        this.setUnlocalizedName("mw_TantalumIngot");
        this.setTextureName("mw:tantalumingot");
        this.setCreativeTab(ModernWarfareMod.gunsTab);
    }
}
