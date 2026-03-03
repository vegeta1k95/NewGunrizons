package com.vicmatskiv.mw.resources;

import net.minecraft.item.Item;

import com.vicmatskiv.mw.ModernWarfareMod;

public class ItemSteelPlate extends Item {

    public ItemSteelPlate() {
        this.setMaxStackSize(64);
        this.setUnlocalizedName("mw_SteelPlate");
        this.setTextureName("mw:steelplate");
        this.setCreativeTab(ModernWarfareMod.gunsTab);
    }
}
