package com.gtnewhorizon.newgunrizons.network;

import java.util.Arrays;
import java.util.List;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import com.gtnewhorizon.newgunrizons.attachment.AttachmentCategory;
import com.gtnewhorizon.newgunrizons.attachment.CompatibleAttachment;
import com.gtnewhorizon.newgunrizons.config.Tags;
import com.gtnewhorizon.newgunrizons.items.ItemAttachment;
import com.gtnewhorizon.newgunrizons.items.ItemBullet;
import com.gtnewhorizon.newgunrizons.items.ItemMagazine;
import com.gtnewhorizon.newgunrizons.util.InventoryUtils;
import com.gtnewhorizon.newgunrizons.weapon.ItemWeapon;
import com.gtnewhorizon.newgunrizons.weapon.PlayerWeaponInstance;
import com.gtnewhorizon.newgunrizons.weapon.WeaponAttachmentAspect;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;

/**
 * Server-side handler for {@link WeaponActionMessage}. Performs the authoritative
 * inventory operations (consuming ammo, swapping attachments, etc.) that were
 * previously handled by the permit evaluators.
 */
public class WeaponActionMessageHandler implements IMessageHandler<WeaponActionMessage, IMessage> {

    @Override
    public IMessage onMessage(WeaponActionMessage message, MessageContext ctx) {
        if (ctx.side != Side.SERVER) {
            return null;
        }

        EntityPlayerMP player = ctx.getServerHandler().playerEntity;
        int slotIndex = message.getSlotIndex();

        if (slotIndex < 0 || slotIndex >= player.inventory.mainInventory.length) {
            return null;
        }

        ItemStack itemStack = player.inventory.getStackInSlot(slotIndex);
        if (itemStack == null) {
            return null;
        }

        switch (message.getActionType()) {
            case WeaponActionMessage.WEAPON_LOAD:
                this.processWeaponLoad(player, itemStack);
                break;
            case WeaponActionMessage.WEAPON_UNLOAD:
                this.processWeaponUnload(player, itemStack);
                break;
            case WeaponActionMessage.MAGAZINE_LOAD:
                this.processMagazineLoad(player, itemStack);
                break;
            case WeaponActionMessage.CHANGE_ATTACHMENT:
                this.processChangeAttachment(player, itemStack, message.getAttachmentCategory());
                break;
        }

        return null;
    }

    private void processWeaponLoad(EntityPlayerMP player, ItemStack weaponStack) {
        if (!(weaponStack.getItem() instanceof ItemWeapon weapon)) {
            return;
        }

        if (weaponStack.stackTagCompound == null) {
            weaponStack.stackTagCompound = new NBTTagCompound();
        }

        PlayerWeaponInstance instance = Tags.getInstance(weaponStack, PlayerWeaponInstance.class);
        if (instance != null) {
            instance.setPlayer(player);
        }

        List<ItemMagazine> compatibleMagazines = weapon.getCompatibleMagazines();
        List<ItemAttachment> compatibleBullets = weapon.getCompatibleAttachments(ItemBullet.class);

        if (!compatibleMagazines.isEmpty()) {
            this.loadWithMagazine(player, weaponStack, weapon, instance, compatibleMagazines);
        } else if (!compatibleBullets.isEmpty()) {
            this.loadWithBullets(player, weaponStack, weapon, instance, compatibleBullets);
        } else if (weapon.builder.ammo != null) {
            this.loadWithGenericAmmo(player, weaponStack, weapon, instance);
        }
    }

    private void loadWithMagazine(EntityPlayerMP player, ItemStack weaponStack, ItemWeapon weapon,
        PlayerWeaponInstance instance, List<ItemMagazine> compatibleMagazines) {
        ItemAttachment existingMagazine = null;

        if (instance != null) {
            existingMagazine = WeaponAttachmentAspect.getActiveAttachment(AttachmentCategory.MAGAZINE, instance);
        }

        if (existingMagazine != null) {
            // Magazine already attached — ammo already set from existing magazine
            int ammo = Tags.getAmmo(weaponStack);
            instance.setAmmo(ammo);
            instance.setLoadIterationCount(0);
            Tags.setInstance(weaponStack, instance);
            return;
        }

        // Consume a compatible magazine from inventory
        ItemStack magazineStack = InventoryUtils.tryConsumingCompatibleItem(
            compatibleMagazines,
            1,
            player,
            (stack) -> Tags.getAmmo(stack) > 0,
            (stack) -> true);

        if (magazineStack != null) {
            int ammo = Tags.getAmmo(magazineStack);
            Tags.setAmmo(weaponStack, ammo);

            if (instance != null) {
                WeaponAttachmentAspect.addAttachment((ItemAttachment) magazineStack.getItem(), instance);
                instance.setAmmo(ammo);
                instance.setLoadIterationCount(0);
                Tags.setInstance(weaponStack, instance);
            }
            player.worldObj.playSoundToNearExcept(player, weapon.getReloadSound(), 1.0F, 1.0F);
        }
    }

    private void loadWithBullets(EntityPlayerMP player, ItemStack weaponStack, ItemWeapon weapon,
        PlayerWeaponInstance instance, List<ItemAttachment> compatibleBullets) {
        int currentAmmo = instance != null ? instance.getAmmo() : 0;
        int maxToLoad = Math.min(weapon.getMaxBulletsPerReload(), weapon.getAmmoCapacity() - currentAmmo);

        ItemStack consumedStack = InventoryUtils
            .tryConsumingCompatibleItem(compatibleBullets, maxToLoad, player, (stack) -> true);

        if (consumedStack != null) {
            int ammo = currentAmmo + consumedStack.stackSize;
            Tags.setAmmo(weaponStack, ammo);

            if (instance != null) {
                instance.setAmmo(ammo);
                if (weapon.hasIteratedLoad()) {
                    instance.setLoadIterationCount(consumedStack.stackSize);
                }
                Tags.setInstance(weaponStack, instance);
            }

            player.worldObj.playSoundToNearExcept(player, weapon.getReloadSound(), 1.0F, 1.0F);
        }
    }

    private void loadWithGenericAmmo(EntityPlayerMP player, ItemStack weaponStack, ItemWeapon weapon,
        PlayerWeaponInstance instance) {
        if (player.inventory.consumeInventoryItem(weapon.builder.ammo)) {
            Tags.setAmmo(weaponStack, weapon.builder.ammoCapacity);

            if (instance != null) {
                instance.setAmmo(weapon.builder.ammoCapacity);
                Tags.setInstance(weaponStack, instance);
            }
            player.worldObj.playSoundToNearExcept(player, weapon.getReloadSound(), 1.0F, 1.0F);
        }
    }

    private void processWeaponUnload(EntityPlayerMP player, ItemStack weaponStack) {
        if (!(weaponStack.getItem() instanceof ItemWeapon weapon)) {
            return;
        }

        if (weaponStack.stackTagCompound == null) {
            return;
        }

        PlayerWeaponInstance instance = Tags.getInstance(weaponStack, PlayerWeaponInstance.class);
        if (instance == null) {
            return;
        }
        instance.setPlayer(player);

        // Remove magazine attachment
        int[] activeAttachmentIds = instance.getActiveAttachmentIds();
        int magazineAttachmentId = activeAttachmentIds[AttachmentCategory.MAGAZINE.ordinal()];
        ItemAttachment currentMagazine = null;
        if (magazineAttachmentId > 0) {
            currentMagazine = (ItemAttachment) Item.getItemById(magazineAttachmentId);
        }

        if (currentMagazine instanceof ItemMagazine magazine) {
            // Create magazine ItemStack with current ammo and return to inventory
            ItemStack magazineItemStack = magazine.createItemStack();
            Tags.setAmmo(magazineItemStack, instance.getAmmo());
            player.inventory.addItemStackToInventory(magazineItemStack);

            // Clear attachment from instance
            activeAttachmentIds[AttachmentCategory.MAGAZINE.ordinal()] = -1;
            instance.setActiveAttachmentIds(activeAttachmentIds);
        }

        Tags.setAmmo(weaponStack, 0);
        instance.setAmmo(0);
        Tags.setInstance(weaponStack, instance);

        player.worldObj.playSoundToNearExcept(player, weapon.getUnloadSound(), 1.0F, 1.0F);
    }

    private void processMagazineLoad(EntityPlayerMP player, ItemStack magazineStack) {
        if (!(magazineStack.getItem() instanceof ItemMagazine magazine)) {
            return;
        }

        List<ItemBullet> compatibleBullets = magazine.getCompatibleBullets();
        int currentAmmo = Tags.getAmmo(magazineStack);
        int maxToLoad = magazine.getAmmo() - currentAmmo;

        ItemStack consumedStack = InventoryUtils
            .tryConsumingCompatibleItem(compatibleBullets, maxToLoad, player, (stack) -> true);

        if (consumedStack != null) {
            Tags.setAmmo(magazineStack, currentAmmo + consumedStack.stackSize);

            if (magazine.getReloadSound() != null) {
                player.playSound(magazine.getReloadSound(), 1.0F, 1.0F);
            }
        }
    }

    private void processChangeAttachment(EntityPlayerMP player, ItemStack weaponStack, byte categoryOrdinal) {
        if (!(weaponStack.getItem() instanceof ItemWeapon weapon)) {
            return;
        }

        if (categoryOrdinal < 0 || categoryOrdinal >= AttachmentCategory.VALUES.length) {
            return;
        }
        AttachmentCategory attachmentCategory = AttachmentCategory.VALUES[categoryOrdinal];

        PlayerWeaponInstance instance = Tags.getInstance(weaponStack, PlayerWeaponInstance.class);
        if (instance == null) {
            return;
        }
        instance.setPlayer(player);

        int[] originalActiveAttachmentIds = instance.getActiveAttachmentIds();
        int[] activeAttachmentIds = Arrays.copyOf(originalActiveAttachmentIds, originalActiveAttachmentIds.length);
        int activeAttachmentIdForThisCategory = activeAttachmentIds[attachmentCategory.ordinal()];

        ItemAttachment currentAttachment = null;
        if (activeAttachmentIdForThisCategory > 0) {
            currentAttachment = (ItemAttachment) Item.getItemById(activeAttachmentIdForThisCategory);
        }

        if (currentAttachment != null) {
            CompatibleAttachment currentCompatibleAttachment = weapon.getCompatibleAttachments()
                .get(currentAttachment);
            if (currentCompatibleAttachment != null && currentCompatibleAttachment.isPermanent()) {
                return;
            }
        }

        // Search for next compatible attachment in inventory
        byte[] selectedAttachmentIndexes = instance.getSelectedAttachmentIds();
        if (selectedAttachmentIndexes == null
            || selectedAttachmentIndexes.length != AttachmentCategory.VALUES.length) {
            return;
        }

        byte[] updatedIndexes = Arrays.copyOf(selectedAttachmentIndexes, selectedAttachmentIndexes.length);
        int activeIndex = updatedIndexes[attachmentCategory.ordinal()];
        int foundSlot = -1;
        CompatibleAttachment foundCompatibleAttachment = null;
        int offset = activeIndex + 1;

        for (int i = 0; i < 37; ++i) {
            int currentIndex = i + offset;
            if (currentIndex >= 36) {
                currentIndex -= 37;
            }
            if (currentIndex == -1) {
                break;
            }

            ItemStack slotItemStack = player.inventory.getStackInSlot(currentIndex);
            if (slotItemStack != null
                && slotItemStack.getItem() instanceof ItemAttachment attachmentFromInventory) {
                if (attachmentFromInventory.getCategory() == attachmentCategory) {
                    CompatibleAttachment compatibleAttachment = weapon.getCompatibleAttachments()
                        .get(attachmentFromInventory);
                    if (compatibleAttachment != null && attachmentFromInventory != currentAttachment) {
                        foundSlot = currentIndex;
                        foundCompatibleAttachment = compatibleAttachment;
                        break;
                    }
                }
            }
        }

        updatedIndexes[attachmentCategory.ordinal()] = (byte) foundSlot;
        instance.setSelectedAttachmentIndexes(updatedIndexes);

        // Remove current attachment handler
        if (currentAttachment != null && currentAttachment.getRemoveHandler() != null) {
            currentAttachment.getRemoveHandler()
                .apply(currentAttachment, instance);
        }

        // Apply new attachment or clear slot
        if (foundSlot >= 0) {
            ItemStack slotItemStack = player.inventory.getStackInSlot(foundSlot);
            ItemAttachment nextAttachment = (ItemAttachment) slotItemStack.getItem();

            if (nextAttachment != null && nextAttachment.getApplyHandler() != null) {
                nextAttachment.getApplyHandler()
                    .apply(nextAttachment, instance);
            } else if (foundCompatibleAttachment.getApplyHandler() != null) {
                foundCompatibleAttachment.getApplyHandler()
                    .apply(nextAttachment, instance);
            } else {
                ItemAttachment.AttachmentHandler handler = weapon.getEquivalentHandler(attachmentCategory);
                if (handler != null) {
                    handler.apply(null, instance);
                }
            }

            InventoryUtils.consumeInventoryItemFromSlot(player, foundSlot);
            activeAttachmentIds[attachmentCategory.ordinal()] = Item.getIdFromItem(nextAttachment);
        } else {
            activeAttachmentIds[attachmentCategory.ordinal()] = -1;
            ItemAttachment.AttachmentHandler handler = weapon.getEquivalentHandler(attachmentCategory);
            if (handler != null) {
                handler.apply(null, instance);
            }
        }

        // Return old attachment to inventory
        if (currentAttachment != null) {
            InventoryUtils.addItemToPlayerInventory(player, currentAttachment, foundSlot);
        }

        instance.setActiveAttachmentIds(activeAttachmentIds);
        Tags.setInstance(weaponStack, instance);
    }
}
