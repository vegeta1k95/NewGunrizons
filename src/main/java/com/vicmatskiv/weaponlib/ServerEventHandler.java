package com.vicmatskiv.weaponlib;

import lombok.Getter;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.vicmatskiv.weaponlib.grenade.EntityGrenade;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class ServerEventHandler {

    private final ModContext modContext;
    @Getter
    private final String modId;

    public ServerEventHandler(ModContext modContext, String modId) {
        this.modContext = modContext;
        this.modId = modId;
    }

    @SubscribeEvent
    public void onEntityUpdate(LivingUpdateEvent e) {}

    @SubscribeEvent
    public void onItemToss(ItemTossEvent itemTossEvent) {}

    @SubscribeEvent
    public void onEntityJoinWorld(EntityJoinWorldEvent e) {
        if (e.entity instanceof EntityGrenade) {
            ((EntityGrenade) e.entity).setContext(this.modContext);
        }
    }

}
