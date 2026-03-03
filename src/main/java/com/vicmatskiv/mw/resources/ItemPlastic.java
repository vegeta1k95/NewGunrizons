package com.vicmatskiv.mw.resources;

import net.minecraft.item.Item;

import com.vicmatskiv.mw.ModernWarfareMod;

public class ItemPlastic extends Item {

    public ItemPlastic() {
        this.setMaxStackSize(16);
        this.setUnlocalizedName("mw_Plastic");
        this.setTextureName("mw:plastic");
        this.setCreativeTab(ModernWarfareMod.gunsTab);
    }
}
