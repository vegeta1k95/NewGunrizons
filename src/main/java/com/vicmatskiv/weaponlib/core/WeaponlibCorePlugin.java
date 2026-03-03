package com.vicmatskiv.weaponlib.core;

import java.util.Map;

import cpw.mods.fml.relauncher.IFMLLoadingPlugin;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin.MCVersion;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin.Name;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin.TransformerExclusions;

@Name("weaponlib")
@TransformerExclusions({ "com.vicmatskiv.weaponlib.core" })
@MCVersion("1.7.10")
public class WeaponlibCorePlugin implements IFMLLoadingPlugin {

    public String getAccessTransformerClass() {
        return null;
    }

    public String[] getASMTransformerClass() {
        return new String[] { "com.vicmatskiv.weaponlib.core.WeaponlibClassTransformer" };
    }

    public String getModContainerClass() {
        return null;
    }

    public String getSetupClass() {
        return null;
    }

    public void injectData(Map<String, Object> data) {}
}
