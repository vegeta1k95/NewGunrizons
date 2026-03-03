package com.vicmatskiv.mw.parts;

import net.minecraft.item.Item;

import com.vicmatskiv.mw.ModernWarfareMod;

public class ItemDiode extends Item {

    public ItemDiode() {
        this.setMaxStackSize(64);
        this.setUnlocalizedName("mw_Diode");
        this.setTextureName("mw:diode");
        this.setCreativeTab(ModernWarfareMod.gunsTab);
    }
}
