package com.vicmatskiv.mw.resources;

import net.minecraft.item.Item;

import com.vicmatskiv.mw.ModernWarfareMod;

public class ItemTitaniumIngot extends Item {

    public ItemTitaniumIngot() {
        this.setMaxStackSize(64);
        this.setUnlocalizedName("mw_TitaniumIngot");
        this.setTextureName("mw:titaniumingot");
        this.setCreativeTab(ModernWarfareMod.gunsTab);
    }
}
