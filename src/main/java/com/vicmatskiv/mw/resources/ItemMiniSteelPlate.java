package com.vicmatskiv.mw.resources;

import net.minecraft.item.Item;

import com.vicmatskiv.mw.ModernWarfareMod;

public class ItemMiniSteelPlate extends Item {

    public ItemMiniSteelPlate() {
        this.setMaxStackSize(64);
        this.setUnlocalizedName("mw_MiniSteelPlate");
        this.setTextureName("mw:ministeelplate");
        this.setCreativeTab(ModernWarfareMod.gunsTab);
    }
}
