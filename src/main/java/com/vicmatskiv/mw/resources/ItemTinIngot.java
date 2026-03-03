package com.vicmatskiv.mw.resources;

import net.minecraft.item.Item;

import com.vicmatskiv.mw.ModernWarfareMod;

public class ItemTinIngot extends Item {

    public ItemTinIngot() {
        this.setMaxStackSize(64);
        this.setUnlocalizedName("mw_TinIngot");
        this.setTextureName("mw:tiningot");
        this.setCreativeTab(ModernWarfareMod.gunsTab);
    }
}
