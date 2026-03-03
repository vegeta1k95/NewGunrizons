package com.vicmatskiv.mw.resources;

import net.minecraft.item.Item;

import com.vicmatskiv.mw.ModernWarfareMod;

public class ItemAluminumIngot extends Item {

    public ItemAluminumIngot() {
        this.setMaxStackSize(64);
        this.setUnlocalizedName("mw_AluminumIngot");
        this.setTextureName("mw:aluminumingot");
        this.setCreativeTab(ModernWarfareMod.gunsTab);
    }
}
