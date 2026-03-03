package com.vicmatskiv.mw.parts;

import net.minecraft.item.Item;

import com.vicmatskiv.mw.ModernWarfareMod;

public class ItemTransistor extends Item {

    public ItemTransistor() {
        this.setMaxStackSize(64);
        this.setUnlocalizedName("mw_Transistor");
        this.setTextureName("mw:transistor");
        this.setCreativeTab(ModernWarfareMod.gunsTab);
    }
}
