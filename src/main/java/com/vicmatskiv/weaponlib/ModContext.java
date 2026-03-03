package com.vicmatskiv.weaponlib;

import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;

import com.vicmatskiv.weaponlib.crafting.RecipeManager;
import com.vicmatskiv.weaponlib.grenade.GrenadeAttackAspect;
import com.vicmatskiv.weaponlib.grenade.GrenadeRenderer;
import com.vicmatskiv.weaponlib.grenade.ItemGrenade;

import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;

public interface ModContext {

    boolean isClient();

    void init(Object var1, String var2, SimpleNetworkWrapper var4);

    void registerWeapon(String var1, Weapon var2, WeaponRenderer var3);

    SimpleNetworkWrapper getChannel();

    void runSyncTick(Runnable var1);

    void registerRenderableItem(String var1, Item var2, Object var3);

    String registerSound(String var1);

    PlayerItemInstanceRegistry getPlayerItemInstanceRegistry();

    WeaponReloadAspect getWeaponReloadAspect();

    WeaponFireAspect getWeaponFireAspect();

    WeaponAttachmentAspect getAttachmentAspect();

    MagazineReloadAspect getMagazineReloadAspect();

    PlayerWeaponInstance getMainHeldWeapon();

    StatusMessageCenter getStatusMessageCenter();

    RecipeManager getRecipeManager();

    String getZoomSound();

    void setChangeZoomSound(String var1);

    String getChangeFireModeSound();

    void setChangeFireModeSound(String var1);

    String getNoAmmoSound();

    void setNoAmmoSound(String var1);

    String getExplosionSound();

    void setExplosionSound(String var1);

    void registerGrenadeWeapon(String var1, ItemGrenade var2, GrenadeRenderer var3);

    ResourceLocation getNamedResource(String var1);

    GrenadeAttackAspect getGrenadeAttackAspect();

    String getModId();

    EffectManager getEffectManager();
}
