package com.vicmatskiv.weaponlib;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

import com.vicmatskiv.weaponlib.animation.PlayerRawPitchAnimationManager;

public class Interceptors {

    public static boolean is3dRenderableItem(Item item) {
        return (item instanceof Weapon || item instanceof ItemBlock);
    }

    public static void setupCameraTransformAfterHurtCameraEffect(float partialTicks) {
        PlayerWeaponInstance weaponInstance = getPlayerWeaponInstance();
        EntityPlayer player = Minecraft.getMinecraft().thePlayer;
        if (weaponInstance != null) {
            ClientModContext context = (ClientModContext) weaponInstance.getWeapon()
                .getModContext();
            PlayerRawPitchAnimationManager yawPitchAnimationManager = context.getPlayerRawPitchAnimationManager();
            if (weaponInstance.isAimed()) {
                yawPitchAnimationManager.update(player);
            } else {
                yawPitchAnimationManager.reset(player);
            }
        }

    }

    private static PlayerWeaponInstance getPlayerWeaponInstance() {
        EntityPlayer player = Minecraft.getMinecraft().thePlayer;
        ItemStack itemStack = player.getHeldItem();
        PlayerWeaponInstance weaponInstance = null;
        if (itemStack != null) {
            Item item = itemStack.getItem();
            if (item instanceof Weapon weapon) {
                ClientModContext context = (ClientModContext) weapon.getModContext();
                weaponInstance = context.getMainHeldWeapon();
            }
        }

        return weaponInstance;
    }

}
