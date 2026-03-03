package com.vicmatskiv.mw.resources;

import net.minecraft.item.Item;

import com.vicmatskiv.mw.ModernWarfareMod;

public class ItemBigSteelPlate extends Item {

    public ItemBigSteelPlate() {
        this.setMaxStackSize(64);
        this.setUnlocalizedName("mw_BigSteelPlate");
        this.setTextureName("mw:bigsteelplate");
        this.setCreativeTab(ModernWarfareMod.gunsTab);
    }
}
