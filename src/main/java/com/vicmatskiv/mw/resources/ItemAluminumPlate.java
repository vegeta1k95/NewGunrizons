package com.vicmatskiv.mw.resources;

import net.minecraft.item.Item;

import com.vicmatskiv.mw.ModernWarfareMod;

public class ItemAluminumPlate extends Item {

    public ItemAluminumPlate() {
        this.setMaxStackSize(64);
        this.setUnlocalizedName("mw_AluminumPlate");
        this.setTextureName("mw:aluminumplate");
        this.setCreativeTab(ModernWarfareMod.gunsTab);
    }
}
