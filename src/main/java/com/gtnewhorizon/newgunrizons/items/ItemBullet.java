package com.gtnewhorizon.newgunrizons.items;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import com.gtnewhorizon.newgunrizons.NewGunrizonsMod;
import com.gtnewhorizon.newgunrizons.model.BedrockModel;

import lombok.Getter;
import lombok.Setter;

public class ItemBullet extends Item {

    @Getter
    @Setter
    private String name;

    @Getter
    @Setter
    private BedrockModel model;

    @Getter
    private String modelTextureName;

    public void setModelTextureName(String modelTextureName) {
        this.modelTextureName = modelTextureName;
    }

    @Override
    public void onCreated(ItemStack stack, World worldIn, EntityPlayer playerIn) {
        super.onCreated(stack, worldIn, playerIn);
        stack.setTagCompound(new NBTTagCompound());
    }

    public static final class Builder {

        private String name;
        private BedrockModel model;
        private String textureName;
        private CreativeTabs tab;
        private int maxStackSize = 64;

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public Builder withModel(BedrockModel model) {
            this.model = model;
            return this;
        }

        public Builder withTextureName(String textureName) {
            this.textureName = textureName.toLowerCase();
            return this;
        }

        public Builder withCreativeTab(CreativeTabs tab) {
            this.tab = tab;
            return this;
        }

        public Builder withMaxStackSize(int maxStackSize) {
            this.maxStackSize = maxStackSize;
            return this;
        }

        public ItemBullet build() {
            ItemBullet bullet = new ItemBullet();
            bullet.setUnlocalizedName(NewGunrizonsMod.MODID + "_" + this.name);
            bullet.setCreativeTab(this.tab);
            bullet.setMaxStackSize(this.maxStackSize);
            bullet.name = this.name;
            if (this.model != null) {
                bullet.model = this.model;
                bullet.modelTextureName = this.textureName != null && !this.textureName.endsWith(".png")
                    ? this.textureName + ".png"
                    : this.textureName;
            }
            NewGunrizonsMod.proxy.registerItem(this.name, bullet, null);
            return bullet;
        }
    }
}
