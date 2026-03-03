package com.vicmatskiv.mw.resources;

import net.minecraft.item.Item;

import com.vicmatskiv.mw.ModernWarfareMod;

public class ItemRuby extends Item {

    public ItemRuby() {
        this.setMaxStackSize(16);
        this.setUnlocalizedName("mw_Ruby");
        this.setTextureName("mw:ruby");
        this.setCreativeTab(ModernWarfareMod.gunsTab);
    }
}
