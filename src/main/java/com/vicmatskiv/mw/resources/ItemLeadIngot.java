package com.vicmatskiv.mw.resources;

import net.minecraft.item.Item;

import com.vicmatskiv.mw.ModernWarfareMod;

public class ItemLeadIngot extends Item {

    public ItemLeadIngot() {
        this.setMaxStackSize(64);
        this.setUnlocalizedName("mw_LeadIngot");
        this.setTextureName("mw:leadingot");
        this.setCreativeTab(ModernWarfareMod.gunsTab);
    }
}
