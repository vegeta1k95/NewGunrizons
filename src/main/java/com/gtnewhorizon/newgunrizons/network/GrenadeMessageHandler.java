package com.gtnewhorizon.newgunrizons.network;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import com.gtnewhorizon.newgunrizons.grenade.GrenadeAttackAspect;
import com.gtnewhorizon.newgunrizons.items.ItemGrenade;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;

public class GrenadeMessageHandler implements IMessageHandler<GrenadeMessage, IMessage> {

    public IMessage onMessage(GrenadeMessage message, MessageContext ctx) {

        if (ctx.side != Side.SERVER)
            return null;

        EntityPlayer player = ctx.getServerHandler().playerEntity;
        ItemStack itemStack = player.getHeldItem();
        if (itemStack != null && itemStack.getItem() instanceof ItemGrenade) {
            message.getInstance().setPlayer(player);
            GrenadeAttackAspect.INSTANCE.serverThrowGrenade(
                player,
                message.getInstance(),
                message.getActivationTimestamp()
            );
        }

        return null;
    }
}
