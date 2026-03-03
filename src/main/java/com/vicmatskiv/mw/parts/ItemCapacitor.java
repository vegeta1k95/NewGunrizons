package com.vicmatskiv.mw.parts;

import net.minecraft.item.Item;

import com.vicmatskiv.mw.ModernWarfareMod;

public class ItemCapacitor extends Item {

    public ItemCapacitor() {
        this.setMaxStackSize(64);
        this.setUnlocalizedName("mw_Capacitor");
        this.setTextureName("mw:capacitor");
        this.setCreativeTab(ModernWarfareMod.gunsTab);
    }
}
