package com.vicmatskiv.mw.resources;

import net.minecraft.item.Item;

import com.vicmatskiv.mw.ModernWarfareMod;

public class ItemSteelIngot extends Item {

    public ItemSteelIngot() {
        this.setMaxStackSize(64);
        this.setUnlocalizedName("mw_SteelIngot");
        this.setTextureName("mw:steelingot");
        this.setCreativeTab(ModernWarfareMod.gunsTab);
    }
}
