package com.vicmatskiv.mw.parts;

import net.minecraft.item.Item;

import com.vicmatskiv.mw.ModernWarfareMod;

public class ItemInductor extends Item {

    public ItemInductor() {
        this.setMaxStackSize(64);
        this.setUnlocalizedName("mw_Inductor");
        this.setTextureName("mw:inductor");
        this.setCreativeTab(ModernWarfareMod.gunsTab);
    }
}
