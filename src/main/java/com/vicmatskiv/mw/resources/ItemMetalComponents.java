package com.vicmatskiv.mw.resources;

import net.minecraft.item.Item;

import com.vicmatskiv.mw.ModernWarfareMod;

public class ItemMetalComponents extends Item {

    public ItemMetalComponents() {
        this.setMaxStackSize(64);
        this.setUnlocalizedName("mw_MetalComponents");
        this.setTextureName("mw:metalcomponents");
        this.setCreativeTab(ModernWarfareMod.gunsTab);
    }
}
