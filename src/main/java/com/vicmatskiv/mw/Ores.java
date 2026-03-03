package com.vicmatskiv.mw;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.oredict.OreDictionary;

import com.vicmatskiv.mw.blocks.BlockBauxiteOre;
import com.vicmatskiv.mw.blocks.BlockCopperOre;
import com.vicmatskiv.mw.blocks.BlockLeadOre;
import com.vicmatskiv.mw.blocks.BlockRubyOre;
import com.vicmatskiv.mw.blocks.BlockSiliconOre;
import com.vicmatskiv.mw.blocks.BlockSulfurOre;
import com.vicmatskiv.mw.blocks.BlockTantalumOre;
import com.vicmatskiv.mw.blocks.BlockTinOre;
import com.vicmatskiv.mw.blocks.BlockTitaniumOre;
import com.vicmatskiv.mw.resources.ItemAluminumIngot;
import com.vicmatskiv.mw.resources.ItemCopperIngot;
import com.vicmatskiv.mw.resources.ItemLeadIngot;
import com.vicmatskiv.mw.resources.ItemRuby;
import com.vicmatskiv.mw.resources.ItemSilicon;
import com.vicmatskiv.mw.resources.ItemSteelDust;
import com.vicmatskiv.mw.resources.ItemSteelIngot;
import com.vicmatskiv.mw.resources.ItemSulfurDust;
import com.vicmatskiv.mw.resources.ItemTantalumIngot;
import com.vicmatskiv.mw.resources.ItemTinIngot;
import com.vicmatskiv.mw.resources.ItemTitaniumIngot;

import cpw.mods.fml.common.registry.GameRegistry;

public class Ores {

    public static final String DUST_SULFUR = "dustSulfur";
    public static final String INGOT_TITANIUM = "ingotTitanium";
    public static final String INGOT_STEEL = "ingotSteel";
    public static final String INGOT_COPPER = "ingotCopper";
    public static final String INGOT_TANTALUM = "ingotTantalum";
    public static final String INGOT_ALUMINIUM = "ingotAluminium";
    public static final String INGOT_TIN = "ingotTin";
    public static final String INGOT_LEAD = "ingotLead";

    public static Block TitaniumOre;
    public static Item TitaniumIngot;
    public static Block LeadOre;
    public static Item LeadIngot;
    public static Block CopperOre;
    public static Item CopperIngot;
    public static Block TinOre;
    public static Item TinIngot;
    public static Block SulfurOre;
    public static Item SulfurDust;
    public static Block BauxiteOre;
    public static Item AluminumIngot;
    public static Block SiliconOre;
    public static Block TantalumOre;
    public static Item TantalumIngot;
    public static Block RubyOre;
    public static Item Ruby;
    public static Item SteelDust;
    public static Item SteelIngot;
    public static Item Silicon;

    public static void init() {
        TitaniumOre = new BlockTitaniumOre();
        TitaniumIngot = new ItemTitaniumIngot();
        LeadOre = new BlockLeadOre();
        LeadIngot = new ItemLeadIngot();
        CopperOre = new BlockCopperOre();
        CopperIngot = new ItemCopperIngot();
        TinOre = new BlockTinOre();
        TinIngot = new ItemTinIngot();
        SulfurOre = new BlockSulfurOre();
        SulfurDust = new ItemSulfurDust();
        BauxiteOre = new BlockBauxiteOre();
        AluminumIngot = new ItemAluminumIngot();
        RubyOre = new BlockRubyOre();
        Ruby = new ItemRuby();
        SiliconOre = new BlockSiliconOre();
        Silicon = new ItemSilicon();
        TantalumOre = new BlockTantalumOre();
        TantalumIngot = new ItemTantalumIngot();
        SteelDust = new ItemSteelDust();
        SteelIngot = new ItemSteelIngot();

        GameRegistry.registerBlock(TitaniumOre, "TitaniumOre");
        GameRegistry.registerItem(TitaniumIngot, "TitaniumIngot");
        GameRegistry.registerBlock(LeadOre, "LeadOre");
        GameRegistry.registerItem(LeadIngot, "LeadIngot");
        GameRegistry.registerBlock(CopperOre, "CopperOre");
        GameRegistry.registerItem(CopperIngot, "CopperIngot");
        GameRegistry.registerBlock(TinOre, "TinOre");
        GameRegistry.registerItem(TinIngot, "TinIngot");
        GameRegistry.registerBlock(SulfurOre, "SulfurOre");
        GameRegistry.registerItem(SulfurDust, "SulfurDust");
        GameRegistry.registerBlock(BauxiteOre, "BauxiteOre");
        GameRegistry.registerItem(AluminumIngot, "AluminumIngot");
        GameRegistry.registerBlock(SiliconOre, "SiliconOre");
        GameRegistry.registerItem(Silicon, "Silicon");
        GameRegistry.registerBlock(TantalumOre, "TantalumOre");
        GameRegistry.registerItem(TantalumIngot, "TantalumIngot");
        GameRegistry.registerBlock(RubyOre, "RubyOre");
        GameRegistry.registerItem(Ruby, "Ruby");
        GameRegistry.registerItem(SteelDust, "SteelDust");
        GameRegistry.registerItem(SteelIngot, "SteelIngot");

        OreDictionary.registerOre("ingotTitanium", TitaniumIngot);
        OreDictionary.registerOre("ingotTantalum", TantalumIngot);
        OreDictionary.registerOre("ingotLead", LeadIngot);
        OreDictionary.registerOre("ingotCopper", CopperIngot);
        OreDictionary.registerOre("ingotTin", TinIngot);
        OreDictionary.registerOre("dustSulfur", SulfurDust);
        OreDictionary.registerOre("ingotAluminium", AluminumIngot);
        OreDictionary.registerOre("Ruby", Ruby);
        OreDictionary.registerOre("ingotSteel", SteelIngot);
    }
}
