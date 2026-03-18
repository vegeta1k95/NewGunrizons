package com.gtnewhorizon.newgunrizons;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraftforge.client.IItemRenderer;

import com.gtnewhorizon.newgunrizons.entities.EntityBullet;
import com.gtnewhorizon.newgunrizons.entities.EntityGrenade;
import com.gtnewhorizon.newgunrizons.entities.EntityShellCasing;
import com.gtnewhorizon.newgunrizons.network.BlockHitMessage;
import com.gtnewhorizon.newgunrizons.network.ExplosionMessage;
import com.gtnewhorizon.newgunrizons.network.GrenadeMessage;
import com.gtnewhorizon.newgunrizons.network.GrenadeMessageHandler;
import com.gtnewhorizon.newgunrizons.network.SpawnParticleMessage;
import com.gtnewhorizon.newgunrizons.network.WeaponActionMessage;
import com.gtnewhorizon.newgunrizons.network.WeaponActionMessageHandler;
import com.gtnewhorizon.newgunrizons.registry.Attachments;
import com.gtnewhorizon.newgunrizons.registry.Bullets;
import com.gtnewhorizon.newgunrizons.registry.Grenades;
import com.gtnewhorizon.newgunrizons.registry.Guns;

import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;

public class CommonProxy {

    private int modEntityID = 256;

    public void init(Object mod, FMLPreInitializationEvent event) {
        SimpleNetworkWrapper channel = NewGunrizonsMod.CHANNEL;

        channel.registerMessage(new WeaponActionMessageHandler(), WeaponActionMessage.class, 15, Side.SERVER);
        channel.registerMessage(new GrenadeMessageHandler(), GrenadeMessage.class, 20, Side.SERVER);
        registerClientMessageHandlers(channel);

        EntityRegistry
            .registerModEntity(EntityBullet.class, "Ammo" + this.modEntityID, this.modEntityID++, mod, 64, 3, true);
        EntityRegistry.registerModEntity(
            EntityShellCasing.class,
            "ShellCasing" + this.modEntityID,
            this.modEntityID++,
            mod,
            64,
            500,
            true);
        EntityRegistry.registerModEntity(
            EntityGrenade.class,
            "Grenade" + this.modEntityID,
            this.modEntityID++,
            mod,
            64,
            10000,
            false);

        Attachments.init();
        Bullets.init();
        Guns.init();
        Grenades.init();
    }

    protected void registerClientMessageHandlers(SimpleNetworkWrapper channel) {
        channel.registerMessage((msg, ctx) -> null, SpawnParticleMessage.class, 18, Side.CLIENT);
        channel.registerMessage((msg, ctx) -> null, BlockHitMessage.class, 19, Side.CLIENT);
        channel.registerMessage((msg, ctx) -> null, ExplosionMessage.class, 21, Side.CLIENT);
    }

    public void onWeaponFireEffects(EntityLivingBase player) {
        // No-op on server; overridden in ClientProxy
    }

    public void applyCameraRecoil(float pitchDelta, float yawDelta, int durationMs) {
        // No-op on server; overridden in ClientProxy
    }

    public boolean isLocalPlayer(EntityLivingBase player) {
        return false;
    }

    public void registerItem(String name, Item item, IItemRenderer renderer) {
        GameRegistry.registerItem(item, name);
    }

}
