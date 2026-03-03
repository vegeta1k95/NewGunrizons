package com.vicmatskiv.mw;

import java.io.File;

import javax.xml.transform.stream.StreamSource;

import net.minecraft.creativetab.CreativeTabs;

import com.vicmatskiv.weaponlib.ModContext;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;

@Mod(
    modid = ModernWarfareMod.MODID,
    version = ModernWarfareMod.VERSION)
public class ModernWarfareMod {

    private static final String DEFAULT_CONFIG_RESOURCE = "/mw.cfg";
    private static final String MODERN_WARFARE_CONFIG_FILE_NAME = "ModernWarfare.cfg";

    public static final String MODID = "mw";
    public static final String VERSION = "1.0.0";

    @SidedProxy(
        serverSide = "com.vicmatskiv.weaponlib.CommonModContext",
        clientSide = "com.vicmatskiv.weaponlib.ClientModContext")
    public static ModContext MOD_CONTEXT;

    public static final SimpleNetworkWrapper CHANNEL;

    public static CreativeTabs gunsTab;
    public static CreativeTabs AssaultRiflesTab;
    public static CreativeTabs PistolsTab;
    public static CreativeTabs SMGTab;
    public static CreativeTabs ShotgunsTab;
    public static CreativeTabs SnipersTab;
    public static CreativeTabs AmmoTab;
    public static CreativeTabs AttachmentsTab;
    public static CreativeTabs GrenadesTab;
    public static CreativeTabs FunGunsTab;

    @SidedProxy(
        serverSide = "com.vicmatskiv.mw.CommonProxy",
        clientSide = "com.vicmatskiv.mw.ClientProxy")
    public static CommonProxy proxy;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        proxy.init(this, event);
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {

    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        RecipeManager.init(MOD_CONTEXT);
    }

    static {
        CHANNEL = NetworkRegistry.INSTANCE.newSimpleChannel(MODID);
        gunsTab = new GunsTab(CreativeTabs.getNextID(), "guns_tab");
        AssaultRiflesTab = new AssaultRiflesTab(CreativeTabs.getNextID(), "AssaultRifles_tab");
        PistolsTab = new PistolsTab(CreativeTabs.getNextID(), "Pistols_tab");
        SMGTab = new SMGTab(CreativeTabs.getNextID(), "SMG_tab");
        ShotgunsTab = new ShotgunsTab(CreativeTabs.getNextID(), "ShotgunsTab");
        SnipersTab = new SnipersTab(CreativeTabs.getNextID(), "SnipersTab");
        AmmoTab = new AmmoTab(CreativeTabs.getNextID(), "AmmoTab");
        AttachmentsTab = new AttachmentsTab(CreativeTabs.getNextID(), "AttachmentsTab");
        GrenadesTab = new GrenadesTab(CreativeTabs.getNextID(), "GrenadesTab");
        FunGunsTab = new FunGunsTab(CreativeTabs.getNextID(), "FunGuns_tab");
    }
}
