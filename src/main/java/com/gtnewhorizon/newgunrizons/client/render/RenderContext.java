package com.gtnewhorizon.newgunrizons.client.render;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;

import com.gtnewhorizon.newgunrizons.items.instances.ItemInstance;
import com.gtnewhorizon.newgunrizons.items.instances.ItemInstanceRegistry;
import com.gtnewhorizon.newgunrizons.items.instances.ItemWeaponInstance;

import lombok.Getter;
import lombok.Setter;

@Getter
public class RenderContext {

    private final EntityLivingBase player;
    private final ItemStack itemStack;

    @Setter
    private float limbSwing;
    @Setter
    private float limbSwingAmount;
    @Setter
    private float ageInTicks;
    @Setter
    private float netHeadYaw;
    @Setter
    private float headPitch;
    @Setter
    private float scale;
    @Setter
    private float transitionProgress;
    @Setter
    private TransformType transformType;
    @Setter
    private RenderableState fromState;
    @Setter
    private RenderableState toState;
    @Setter
    private ItemInstance itemInstance;

    public RenderContext(EntityLivingBase player, ItemStack itemStack) {
        this.player = player;
        this.itemStack = itemStack;
    }

    public ItemWeaponInstance getWeaponInstance() {
        if (this.itemInstance instanceof ItemWeaponInstance) {
            return (ItemWeaponInstance) this.itemInstance;
        } else {
            ItemInstance itemInstance = ItemInstanceRegistry.getItemInstance(this.player, this.itemStack);
            return itemInstance instanceof ItemWeaponInstance ? (ItemWeaponInstance) itemInstance : null;
        }
    }
}
