package com.gtnewhorizon.newgunrizons.network;

import net.minecraft.item.ItemStack;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.gtnewhorizon.newgunrizons.config.PlayerContext;
import com.gtnewhorizon.newgunrizons.config.Tags;
import com.gtnewhorizon.newgunrizons.weapon.PlayerItemInstance;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;

/**
 * Server-side handler for {@link SyncMessage}. Persists the client's
 * {@code PlayerItemInstance} state into the corresponding {@code ItemStack}'s NBT.
 */
public class SyncMessageHandler implements IMessageHandler<SyncMessage, IMessage> {

    public IMessage onMessage(SyncMessage message, MessageContext ctx) {
        if (ctx.side == Side.SERVER) {
            Object context = message.getContext();
            if (context instanceof PlayerContext playerContext) {
                playerContext.setPlayer(ctx.getServerHandler().playerEntity);
            }
            if (context instanceof PlayerItemInstance<?> instance) {
                ItemStack itemStack = instance.getItemStack();
                if (itemStack != null) {
                    if (instance.getItem() == itemStack.getItem()) {
                        Tags.setInstance(itemStack, instance);
                    }
                }
            }
        }
        return null;
    }
}
