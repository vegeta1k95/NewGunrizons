package com.gtnewhorizon.newgunrizons.client.debug;

import net.minecraft.client.Minecraft;

import com.gtnewhorizon.newgunrizons.client.input.KeyBindings;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;

public class DebugInputHandler {

    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        if (KeyBindings.debugKey.isPressed()) {
            Minecraft mc = Minecraft.getMinecraft();
            if (mc.currentScreen == null) {
                PositionDebugger.setActive(true);
                PositionDebugger.setScreenOpen(true);
                mc.displayGuiScreen(new PositionDebugScreen());
            } else if (mc.currentScreen instanceof PositionDebugScreen) {
                mc.displayGuiScreen(null);
            }
        }
    }
}
