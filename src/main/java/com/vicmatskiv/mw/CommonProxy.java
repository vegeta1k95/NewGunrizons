package com.vicmatskiv.mw;

import net.minecraft.item.Item;

import com.vicmatskiv.mw.items.ItemLaserPointer;
import com.vicmatskiv.mw.parts.ItemCapacitor;
import com.vicmatskiv.mw.parts.ItemCopperWiring;
import com.vicmatskiv.mw.parts.ItemDiode;
import com.vicmatskiv.mw.parts.ItemInductor;
import com.vicmatskiv.mw.parts.ItemResistor;
import com.vicmatskiv.mw.parts.ItemTransistor;
import com.vicmatskiv.mw.resources.ItemAluminumPlate;
import com.vicmatskiv.mw.resources.ItemBigSteelPlate;
import com.vicmatskiv.mw.resources.ItemCloth;
import com.vicmatskiv.mw.resources.ItemElectronics;
import com.vicmatskiv.mw.resources.ItemGreenCloth;
import com.vicmatskiv.mw.resources.ItemMetalComponents;
import com.vicmatskiv.mw.resources.ItemMiniSteelPlate;
import com.vicmatskiv.mw.resources.ItemOpticGlass;
import com.vicmatskiv.mw.resources.ItemPiston;
import com.vicmatskiv.mw.resources.ItemPlastic;
import com.vicmatskiv.mw.resources.ItemSteelPlate;
import com.vicmatskiv.mw.resources.ItemTanCloth;

import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;

public class CommonProxy {

    public static Item ElectronicCircuitBoard;
    public static Item OpticGlass;
    public static Item Cloth;
    public static Item TanCloth;
    public static Item GreenCloth;
    public static Item Inductor;
    public static Item Transistor;
    public static Item Resistor;
    public static Item Diode;
    public static Item Capacitor;
    public static Item CopperWiring;
    public static Item Piston;
    public static Item LaserPointer;
    public static Item AluminumPlate;
    public static Item SteelPlate;
    public static Item BigSteelPlate;
    public static Item MiniSteelPlate;
    public static Item MetalComponents;
    public static Item Plastic;

    protected boolean isClient() {
        return false;
    }

    public void init(Object mod, FMLPreInitializationEvent event) {

        ModernWarfareMod.MOD_CONTEXT.init(mod, "mw", ModernWarfareMod.CHANNEL);
        ModernWarfareMod.MOD_CONTEXT.setChangeZoomSound("OpticZoom");
        ModernWarfareMod.MOD_CONTEXT.setChangeFireModeSound("GunFireModeSwitch");
        ModernWarfareMod.MOD_CONTEXT.setNoAmmoSound("dryfire");
        ModernWarfareMod.MOD_CONTEXT.setExplosionSound("grenadeexplosion");

        ElectronicCircuitBoard = new ItemElectronics();
        OpticGlass = new ItemOpticGlass();
        Cloth = new ItemCloth();
        TanCloth = new ItemTanCloth();
        GreenCloth = new ItemGreenCloth();
        Inductor = new ItemInductor();
        Resistor = new ItemResistor();
        Transistor = new ItemTransistor();
        Diode = new ItemDiode();
        Capacitor = new ItemCapacitor();
        CopperWiring = new ItemCopperWiring();
        Piston = new ItemPiston();
        LaserPointer = new ItemLaserPointer();
        Plastic = new ItemPlastic();
        AluminumPlate = new ItemAluminumPlate();
        SteelPlate = new ItemSteelPlate();
        BigSteelPlate = new ItemBigSteelPlate();
        MiniSteelPlate = new ItemMiniSteelPlate();
        MetalComponents = new ItemMetalComponents();

        GameRegistry.registerItem(ElectronicCircuitBoard, "Electronics");
        GameRegistry.registerItem(OpticGlass, "OpticGlass");
        GameRegistry.registerItem(Cloth, "Cloth");
        GameRegistry.registerItem(TanCloth, "TanCloth");
        GameRegistry.registerItem(GreenCloth, "GreenCloth");
        GameRegistry.registerItem(AluminumPlate, "AluminumPlate");
        GameRegistry.registerItem(SteelPlate, "SteelPlate");
        GameRegistry.registerItem(BigSteelPlate, "BigSteelPlate");
        GameRegistry.registerItem(MiniSteelPlate, "MiniSteelPlate");
        GameRegistry.registerItem(MetalComponents, "MetalComponents");
        GameRegistry.registerItem(Transistor, "Transistor");
        GameRegistry.registerItem(Resistor, "Resistor");
        GameRegistry.registerItem(Inductor, "Inductor");
        GameRegistry.registerItem(Diode, "Diode");
        GameRegistry.registerItem(Capacitor, "Capacitor");
        GameRegistry.registerItem(CopperWiring, "CopperWiring");
        GameRegistry.registerItem(Piston, "Piston");
        GameRegistry.registerItem(LaserPointer, "LaserPointer");
        GameRegistry.registerItem(Plastic, "plastic");

        Ores.init();
        Attachments.init();
        AuxiliaryAttachments.init();
        Bullets.init();
        Magazines.init();
        Guns.init(this);
        Grenades.init(this);
    }
}
