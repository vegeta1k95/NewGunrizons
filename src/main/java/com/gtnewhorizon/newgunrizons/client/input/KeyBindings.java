package com.gtnewhorizon.newgunrizons.client.input;

import net.minecraft.client.settings.KeyBinding;

import cpw.mods.fml.client.registry.ClientRegistry;

public class KeyBindings {

    public static KeyBinding reloadKey;
    public static KeyBinding attachmentKey;
    public static KeyBinding laserSwitchKey;
    public static KeyBinding nightVisionSwitchKey;
    public static KeyBinding addKey;
    public static KeyBinding subtractKey;
    public static KeyBinding fireModeKey;
    public static KeyBinding debugKey;

    public static void init() {
        reloadKey = new KeyBinding("key.reload", 19, "key.categories.newgunrizons");
        laserSwitchKey = new KeyBinding("key.laser", 38, "key.categories.newgunrizons");
        nightVisionSwitchKey = new KeyBinding("key.nightVision", 49, "key.categories.newgunrizons");
        attachmentKey = new KeyBinding("key.attachment", 50, "key.categories.newgunrizons");
        addKey = new KeyBinding("key.add", 27, "key.categories.newgunrizons");
        subtractKey = new KeyBinding("key.subtract", 26, "key.categories.newgunrizons");
        fireModeKey = new KeyBinding("key.fire_mode", 54, "key.categories.newgunrizons");
        debugKey = new KeyBinding("key.position_debug", 64, "key.categories.newgunrizons");
        ClientRegistry.registerKeyBinding(reloadKey);
        ClientRegistry.registerKeyBinding(attachmentKey);
        ClientRegistry.registerKeyBinding(laserSwitchKey);
        ClientRegistry.registerKeyBinding(nightVisionSwitchKey);
        ClientRegistry.registerKeyBinding(addKey);
        ClientRegistry.registerKeyBinding(subtractKey);
        ClientRegistry.registerKeyBinding(fireModeKey);
        ClientRegistry.registerKeyBinding(debugKey);
    }
}
