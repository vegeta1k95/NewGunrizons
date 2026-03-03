package com.vicmatskiv.mw.parts;

import net.minecraft.item.Item;

import com.vicmatskiv.mw.ModernWarfareMod;

public class ItemResistor extends Item {

    public ItemResistor() {
        this.setMaxStackSize(64);
        this.setUnlocalizedName("mw_Resistor");
        this.setTextureName("mw:resistor");
        this.setCreativeTab(ModernWarfareMod.gunsTab);
    }
}
