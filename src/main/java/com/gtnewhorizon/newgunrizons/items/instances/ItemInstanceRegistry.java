package com.gtnewhorizon.newgunrizons.items.instances;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.WeakHashMap;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import com.gtnewhorizon.newgunrizons.util.InventoryUtils;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemInstanceRegistry {

    private static final Map<UUID, Map<Integer, ItemInstance>> registry = new HashMap<>();
    private static final WeakHashMap<ItemStack, ItemInstance> itemStackCache = new WeakHashMap<>();

    @SideOnly(Side.CLIENT)
    public static ItemWeaponInstance getMainHeldWeapon() {
        EntityPlayer player = Minecraft.getMinecraft().thePlayer;
        return getMainHandItemInstance(player, ItemWeaponInstance.class);
    }

    public static <T extends ItemInstance> T getMainHandItemInstance(EntityPlayer player,
        Class<T> targetClass) {
        if (player == null) {
            return null;
        }
        ItemInstance instance = getItemInstance(player, player.inventory.currentItem);
        return targetClass.isInstance(instance) ? targetClass.cast(instance) : null;
    }

    public static ItemInstance getItemInstance(EntityPlayer player, int slot) {
        Map<Integer, ItemInstance> slotInstances = registry
            .computeIfAbsent(player.getPersistentID(), p -> new HashMap<>());
        ItemInstance result = slotInstances.get(slot);

        if (result == null) {
            result = createItemInstance(player, slot);
            if (result != null) {
                slotInstances.put(slot, result);
            }
            return result;
        }

        ItemStack slotItemStack = player.inventory.getStackInSlot(slot);
        if (slotItemStack != null && slotItemStack.getItem() != result.getItem()) {
            result = createItemInstance(player, slot);
            if (result != null) {
                slotInstances.put(slot, result);
            }
        }

        if (result != null && result.getItemInventoryIndex() != slot) {
            result.setItemInventoryIndex(slot);
        }

        if (result != null && result.getPlayer() != player) {
            result.setPlayer(player);
        }

        return result;
    }

    private static ItemInstance createItemInstance(EntityPlayer player, int slot) {
        ItemStack itemStack = player.inventory.getStackInSlot(slot);
        if (itemStack == null) {
            return null;
        }

        Item item = itemStack.getItem();
        if (!(item instanceof ItemInstanceFactory)) {
            return null;
        }

        ItemInstance result = null;
        try {
            result = ItemInstance.fromStack(itemStack);
        } catch (RuntimeException ignored) {}

        if (result == null) {
            result = ((ItemInstanceFactory<?>) item).createItemInstance(player, itemStack, slot);
        }

        result.setItemInventoryIndex(slot);
        result.setPlayer(player);
        return result;
    }

    /**
     * Resolves an {@link ItemInstance} for an arbitrary ItemStack, used during rendering
     * when only the ItemStack is available (not a slot number).
     * <p>
     * Lookup order: weak cache → slot-based registry → NBT deserialization → factory creation.
     */
    public static ItemInstance getItemInstance(EntityLivingBase player, ItemStack itemStack) {
        ItemInstance cached = itemStackCache.get(itemStack);
        if (cached != null && cached.getItem() == itemStack.getItem()) {
            return cached;
        }

        ItemInstance instance = null;

        if (player.worldObj != null && player.worldObj.isRemote
            && player instanceof EntityPlayer
            && com.gtnewhorizon.newgunrizons.NewGunrizonsMod.proxy.isLocalPlayer(player)) {
            int slot = InventoryUtils.getInventorySlot((EntityPlayer) player, itemStack);
            if (slot >= 0) {
                instance = getItemInstance((EntityPlayer) player, slot);
            }
        }

        if (instance == null || instance.getItem() != itemStack.getItem()) {
            try {
                instance = ItemInstance.fromStack(itemStack);
            } catch (RuntimeException ignored) {}
        }

        if ((instance == null || instance.getItem() != itemStack.getItem())
            && itemStack.getItem() instanceof ItemInstanceFactory) {
            instance = ((ItemInstanceFactory<?>) itemStack.getItem()).createItemInstance(player, itemStack, -1);
            instance.setPlayer(player);
        }

        if (instance != null) {
            itemStackCache.put(itemStack, instance);
        }

        return instance;
    }

    /** Removes registry entries for items no longer in the player's inventory. */
    public static void update(EntityPlayer player) {
        if (player == null) return;

        Map<Integer, ItemInstance> slotContexts = registry.get(player.getPersistentID());
        if (slotContexts == null) return;

        Iterator<Entry<Integer, ItemInstance>> it = slotContexts.entrySet()
            .iterator();
        while (it.hasNext()) {
            Entry<Integer, ItemInstance> entry = it.next();
            ItemStack slotStack = player.inventory.getStackInSlot(entry.getKey());
            if (slotStack == null || slotStack.getItem() != entry.getValue()
                .getItem()) {
                it.remove();
            }
        }
    }
}
