package com.vicmatskiv.weaponlib;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import net.minecraft.client.resources.IResourcePack;
import net.minecraft.client.resources.data.IMetadataSection;
import net.minecraft.client.resources.data.IMetadataSerializer;
import net.minecraft.util.ResourceLocation;

public class WeaponResourcePack implements IResourcePack {

    private static final String WEAPONLIB_RESOURCE_DOMAIN = "weaponlib";
    private static final Set<String> RESOURCE_DOMAINS = new HashSet<>(Collections.singleton(WEAPONLIB_RESOURCE_DOMAIN));

    public InputStream getInputStream(ResourceLocation resourceLocation) {
        String resourcePath = this.modifyResourcePath(resourceLocation);
        return this.getClass()
            .getResourceAsStream(resourcePath);
    }

    private String modifyResourcePath(ResourceLocation resourceLocation) {
        String resourcePath = resourceLocation.getResourcePath();
        if (resourcePath.startsWith("textures")) {
            int lastIndexOfSlash = resourcePath.lastIndexOf(47);
            if (lastIndexOfSlash >= 0) {
                String fileName = resourcePath.substring(lastIndexOfSlash + 1);
                resourcePath = '/' + this.getClass()
                    .getPackage()
                    .getName()
                    .replace('.', '/') + "/resources/" + fileName;
            }
        }

        return resourcePath;
    }

    public boolean resourceExists(ResourceLocation resourceLocation) {
        String resourcePath = this.modifyResourcePath(resourceLocation);
        return WEAPONLIB_RESOURCE_DOMAIN.equals(resourceLocation.getResourceDomain()) && this.getClass()
            .getResource(resourcePath) != null;
    }

    public Set<String> getResourceDomains() {
        return RESOURCE_DOMAINS;
    }

    public IMetadataSection getPackMetadata(IMetadataSerializer p_135058_1_, String p_135058_2_) {
        return null;
    }

    public BufferedImage getPackImage() {
        return null;
    }

    public String getPackName() {
        return this.getClass()
            .getSimpleName();
    }
}
