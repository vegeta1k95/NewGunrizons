package com.vicmatskiv.mw.resources;

import net.minecraft.item.Item;

import com.vicmatskiv.mw.ModernWarfareMod;

public class ItemPiston extends Item {

    public ItemPiston() {
        this.setMaxStackSize(16);
        this.setUnlocalizedName("mw_Piston");
        this.setTextureName("mw:piston");
        this.setCreativeTab(ModernWarfareMod.gunsTab);
    }
}
