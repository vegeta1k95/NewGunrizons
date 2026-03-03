package com.vicmatskiv.mw.parts;

import net.minecraft.item.Item;

import com.vicmatskiv.mw.ModernWarfareMod;

public class ItemCopperWiring extends Item {

    public ItemCopperWiring() {
        this.setMaxStackSize(64);
        this.setUnlocalizedName("mw_CopperWiring");
        this.setTextureName("mw:copperwiring");
        this.setCreativeTab(ModernWarfareMod.gunsTab);
    }
}
