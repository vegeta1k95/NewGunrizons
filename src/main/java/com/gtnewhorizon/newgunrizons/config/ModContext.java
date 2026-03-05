package com.gtnewhorizon.newgunrizons.config;

import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;

import com.gtnewhorizon.newgunrizons.client.render.WeaponRenderer;
import com.gtnewhorizon.newgunrizons.crafting.RecipeManager;
import com.gtnewhorizon.newgunrizons.grenade.GrenadeAttackAspect;
import com.gtnewhorizon.newgunrizons.grenade.GrenadeRenderer;
import com.gtnewhorizon.newgunrizons.items.ItemGrenade;
import com.gtnewhorizon.newgunrizons.items.ItemWeapon;
import com.gtnewhorizon.newgunrizons.items.instances.ItemInstanceRegistry;
import com.gtnewhorizon.newgunrizons.items.instances.ItemWeaponInstance;
import com.gtnewhorizon.newgunrizons.network.StatusMessageCenter;
import com.gtnewhorizon.newgunrizons.weapon.MagazineReloadAspect;
import com.gtnewhorizon.newgunrizons.weapon.WeaponAttachmentAspect;
import com.gtnewhorizon.newgunrizons.weapon.WeaponFireAspect;
import com.gtnewhorizon.newgunrizons.weapon.WeaponReloadAspect;

import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;

public interface ModContext {

    boolean isClient();

    void init(Object mod, SimpleNetworkWrapper channel);

    void registerWeapon(String name, ItemWeapon weapon, WeaponRenderer renderer);

    SimpleNetworkWrapper getChannel();

    void registerRenderableItem(String name, Item item, Object renderer);

    ItemInstanceRegistry getItemInstanceRegistry();

    WeaponReloadAspect getWeaponReloadAspect();

    WeaponFireAspect getWeaponFireAspect();

    WeaponAttachmentAspect getAttachmentAspect();

    MagazineReloadAspect getMagazineReloadAspect();

    ItemWeaponInstance getMainHeldWeapon();

    StatusMessageCenter getStatusMessageCenter();

    RecipeManager getRecipeManager();

    void registerGrenadeWeapon(String name, ItemGrenade grenade, GrenadeRenderer renderer);

    ResourceLocation getNamedResource(String name);

    GrenadeAttackAspect getGrenadeAttackAspect();
}
