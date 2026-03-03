package com.vicmatskiv.weaponlib;

import net.minecraft.client.settings.KeyBinding;

import cpw.mods.fml.client.registry.ClientRegistry;

public class KeyBindings {

    public static KeyBinding reloadKey;
    public static KeyBinding attachmentKey;
    public static KeyBinding upArrowKey;
    public static KeyBinding downArrowKey;
    public static KeyBinding leftArrowKey;
    public static KeyBinding rightArrowKey;
    public static KeyBinding laserSwitchKey;
    public static KeyBinding nightVisionSwitchKey;
    public static KeyBinding addKey;
    public static KeyBinding subtractKey;
    public static KeyBinding fireModeKey;

    public static void init() {
        reloadKey = new KeyBinding("key.reload", 19, "key.categories.weaponlib");
        laserSwitchKey = new KeyBinding("key.laser", 38, "key.categories.weaponlib");
        nightVisionSwitchKey = new KeyBinding("key.nightVision", 49, "key.categories.weaponlib");
        attachmentKey = new KeyBinding("key.attachment", 50, "key.categories.weaponlib");
        upArrowKey = new KeyBinding("key.scope", 200, "key.categories.weaponlib");
        downArrowKey = new KeyBinding("key.recoil_fitter", 208, "key.categories.weaponlib");
        leftArrowKey = new KeyBinding("key.silencer", 203, "key.categories.weaponlib");
        rightArrowKey = new KeyBinding("key.texture_change", 205, "key.categories.weaponlib");
        addKey = new KeyBinding("key.add", 23, "key.categories.weaponlib");
        subtractKey = new KeyBinding("key.subtract", 24, "key.categories.weaponlib");
        fireModeKey = new KeyBinding("key.fire_mode", 54, "key.categories.weaponlib");

        ClientRegistry.registerKeyBinding(reloadKey);
        ClientRegistry.registerKeyBinding(attachmentKey);
        ClientRegistry.registerKeyBinding(upArrowKey);
        ClientRegistry.registerKeyBinding(downArrowKey);
        ClientRegistry.registerKeyBinding(leftArrowKey);
        ClientRegistry.registerKeyBinding(rightArrowKey);
        ClientRegistry.registerKeyBinding(laserSwitchKey);
        ClientRegistry.registerKeyBinding(nightVisionSwitchKey);
        ClientRegistry.registerKeyBinding(addKey);
        ClientRegistry.registerKeyBinding(subtractKey);
        ClientRegistry.registerKeyBinding(fireModeKey);
    }
}
