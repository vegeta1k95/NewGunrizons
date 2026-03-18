package com.gtnewhorizon.newgunrizons.items.instances;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;

import com.gtnewhorizon.newgunrizons.NewGunrizonsMod;
import com.gtnewhorizon.newgunrizons.attachment.AttachmentCategory;
import com.gtnewhorizon.newgunrizons.attachment.CompatibleAttachment;
import com.gtnewhorizon.newgunrizons.client.render.RenderableState;
import com.gtnewhorizon.newgunrizons.items.ItemAttachment;
import com.gtnewhorizon.newgunrizons.items.ItemBullet;
import com.gtnewhorizon.newgunrizons.items.ItemScope;
import com.gtnewhorizon.newgunrizons.items.ItemWeapon;
import com.gtnewhorizon.newgunrizons.network.StatusMessageManager;
import com.gtnewhorizon.newgunrizons.network.WeaponActionMessage;
import com.gtnewhorizon.newgunrizons.registry.Sounds;
import com.gtnewhorizon.newgunrizons.util.InventoryUtils;
import com.gtnewhorizon.newgunrizons.weapon.WeaponState;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import lombok.Getter;
import lombok.Setter;

public class ItemWeaponInstance extends ItemInstance {

    private static final String AMMO_TAG = "gunrizons_ammo";

    private static final long AIM_CHANGE_DURATION = 400L;
    private static final long ALERT_TIMEOUT = 500L;
    private static final long CLICK_SPAMMING_TIMEOUT = 150L;

    @Getter
    private WeaponState state = WeaponState.IDLE;
    @Getter
    private long stateUpdateTimestamp = System.currentTimeMillis();

    @Setter
    @Getter
    private int ammo;

    @Setter
    @Getter
    private float recoil;

    @Setter
    @Getter
    private int seriesShotCount;

    @Setter
    @Getter
    private long lastFireTimestamp;

    @Getter
    private boolean aimed;
    private long aimChangeTimestamp;

    @Setter
    @Getter
    private int maxShots;

    @Setter
    @Getter
    private float zoom = 1.0F;

    @Setter
    @Getter
    private boolean laserOn;

    @Setter
    @Getter
    private boolean nightVisionOn;

    @Getter
    @Setter
    private int loadIterationCount;

    @Setter
    private int[] activeAttachmentIds = new int[0];

    public ItemWeaponInstance() {}

    public ItemWeaponInstance(int itemInventoryIndex, EntityPlayer player, ItemStack itemStack) {
        super(itemInventoryIndex, player, itemStack);
    }

    public ItemWeaponInstance(int itemInventoryIndex, EntityPlayer player) {
        super(itemInventoryIndex, player);
    }

    @Override
    public void readFromBuf(ByteBuf buf) {
        super.readFromBuf(buf);
        this.state = WeaponState.values()[buf.readInt()];

        int lengthActive = buf.readByte();
        this.activeAttachmentIds = new int[lengthActive];
        for (int i = 0; i < lengthActive; ++i) {
            this.activeAttachmentIds[i] = buf.readInt();
        }

        this.ammo = buf.readInt();
        this.maxShots = buf.readInt();
        this.recoil = buf.readFloat();
        this.zoom = buf.readFloat();
        this.laserOn = buf.readBoolean();
    }

    @Override
    public void writeToBuf(ByteBuf buf) {
        super.writeToBuf(buf);
        buf.writeInt(this.state != null ? this.state.ordinal() : 0);

        buf.writeByte(this.activeAttachmentIds.length);
        for (int b : this.activeAttachmentIds) {
            buf.writeInt(b);
        }

        buf.writeInt(this.ammo);
        buf.writeInt(this.maxShots);
        buf.writeFloat(this.recoil);
        buf.writeFloat(this.zoom);
        buf.writeBoolean(this.laserOn);
    }

    @Override
    public void writeByteType(ByteBuf buf) {
        buf.writeByte(1);
    }

    public void setState(WeaponState state) {
        this.state = state;
        this.stateUpdateTimestamp = System.currentTimeMillis();
    }

    public ItemWeapon getWeapon() {
        return (ItemWeapon) this.item;
    }

    // ---- Update (automatic transitions) ----

    public void update() {

        if (this.state == WeaponState.NO_AMMO
            && System.currentTimeMillis() >= ALERT_TIMEOUT + this.stateUpdateTimestamp) {
            setState(WeaponState.IDLE);
        }

        boolean changed;
        do {
            changed = false;
            switch (this.state) {
                case RELOADING_START: {
                    long elapsed = System.currentTimeMillis() - this.stateUpdateTimestamp;
                    long animDuration = getWeapon().getAnimationDurationMs(RenderableState.RELOADING_START);
                    if (elapsed >= animDuration) {
                        if (this.loadIterationCount > 0) {
                            onStartLoadIteration();
                            setState(WeaponState.RELOADING_ITERATION);
                        } else {
                            setState(WeaponState.IDLE);
                        }
                        changed = true;
                    }
                    break;
                }
                case RELOADING_ITERATION: {
                    long elapsed = System.currentTimeMillis() - this.stateUpdateTimestamp;
                    long animDuration = getWeapon().getAnimationDurationMs(RenderableState.RELOADING_ITERATION);
                    if (elapsed >= animDuration) {
                        this.loadIterationCount--;
                        setState(WeaponState.RELOADING_ITERATION_COMPLETED);
                        changed = true;
                    }
                    break;
                }
                case RELOADING_ITERATION_COMPLETED: {
                    if (this.loadIterationCount > 0) {
                        onStartLoadIteration();
                        setState(WeaponState.RELOADING_ITERATION);
                    } else {
                        setState(WeaponState.RELOADING_END);
                    }
                    changed = true;
                    break;
                }
                case RELOADING_END: {
                    long elapsed = System.currentTimeMillis() - this.stateUpdateTimestamp;
                    long animDuration = getWeapon().getAnimationDurationMs(RenderableState.RELOADING_END);
                    if (elapsed >= animDuration) {
                        if (getWeapon().getAllReloadIterationsCompletedSound() != null) {
                            getPlayer().playSound(getWeapon().getAllReloadIterationsCompletedSound(), 1.0F, 1.0F);
                        }
                        setState(WeaponState.IDLE);
                        changed = true;
                    }
                    break;
                }
                default:
                    break;
            }
        } while (changed);
    }

    // ---- Fire methods (from WeaponFireAspect) ----

    @SideOnly(Side.CLIENT)
    public void tryFire() {
        if (this.state == WeaponState.IDLE) {
            if (this.ammo <= 0) {
                String message = StatCollector.translateToLocalFormatted("gui.noAmmo");
                StatusMessageManager.INSTANCE.addAlertMessage(message, 3, 250L, 200L);
                if (Sounds.DRY_FIRE != null) {
                    getPlayer().playSound(Sounds.DRY_FIRE, 1.0F, 1.0F);
                }

                setState(WeaponState.NO_AMMO);
            } else if (!getPlayer().isSprinting() && isFireRateReady()) {
                onFire();
                setState(WeaponState.SHOOTING);
            }
        } else if (this.state == WeaponState.SHOOTING) {
            if (this.ammo > 0 && !getPlayer().isSprinting()
                && this.seriesShotCount < this.maxShots
                && isFireRateReady()) {
                onFire();
                setState(WeaponState.SHOOTING);
            }
        }
    }

    @SideOnly(Side.CLIENT)
    public void tryStopFire() {
        if (this.state == WeaponState.SHOOTING) {
            this.seriesShotCount = 0;
            setState(WeaponState.IDLE);
        }
    }

    private boolean isFireRateReady() {
        return (float) (System.currentTimeMillis() - this.lastFireTimestamp) >= 50.0F / getWeapon().getFireRate();
    }

    @SideOnly(Side.CLIENT)
    private void onFire() {
        EntityPlayer player = getPlayer();
        ItemWeapon weapon = getWeapon();
        Random random = player.getRNG();
        NewGunrizonsMod.CHANNEL
            .sendToServer(new WeaponActionMessage(WeaponActionMessage.FIRE, player.inventory.currentItem));

        boolean silencerOn = isSilencerOn();
        String snd = silencerOn ? weapon.getSilencedShootSound() : weapon.getShootSound();
        float vol = silencerOn ? weapon.getSilencedShootSoundVolume() : weapon.getShootSoundVolume();

        if (snd != null) {
            player.playSound(snd, vol, 1.0F);
        }

        float recoilAmount = weapon.getRecoil();
        if (recoilAmount > 0) {
            float rotationYawFactor = -1.0F + random.nextFloat() * 2.0F;
            NewGunrizonsMod.proxy
                .applyCameraRecoil(-recoilAmount, recoilAmount * rotationYawFactor, weapon.getCameraRecoilDurationMs());
        }

        if (weapon.isSmokeEnabled()) {
            NewGunrizonsMod.proxy.onWeaponFireEffects(player);
        }

        this.seriesShotCount++;
        this.lastFireTimestamp = System.currentTimeMillis();
        this.ammo--;
    }

    public static void serverFire(EntityPlayer player, ItemStack itemStack, int slotIndex) {
        ItemWeapon weapon = (ItemWeapon) itemStack.getItem();
        ItemWeaponInstance instance = ItemInstance.fromStack(itemStack);

        if (instance == null) {
            instance = weapon.createItemInstance(player, itemStack, slotIndex);
            ItemInstance.toStack(itemStack, instance);
        }

        instance.setPlayer(player);

        int ammo = instance.getAmmo();
        if (ammo <= 0) {
            return;
        }

        instance.setAmmo(ammo - 1);
        ItemWeaponInstance.setAmmo(itemStack, ammo - 1);
        ItemInstance.toStack(itemStack, instance);

        for (int i = 0; i < weapon.getPellets(); ++i) {
            weapon.spawnBullet(player);
        }

        if (weapon.isShellCasingEjectEnabled()) {
            weapon.spawnShell(instance, player);
        }

        boolean silencerOn = instance.isSilencerOn();
        String snd = silencerOn ? weapon.getSilencedShootSound() : weapon.getShootSound();
        float vol = silencerOn ? weapon.getSilencedShootSoundVolume() : weapon.getShootSoundVolume();

        player.worldObj.playSoundToNearExcept(player, snd, vol, 1.0F);
    }

    // ---- Reload methods (from WeaponReloadAspect) ----

    @SideOnly(Side.CLIENT)
    public void tryReload() {
        if (this.state != WeaponState.IDLE) {
            return;
        }

        ItemWeapon weapon = getWeapon();
        if (hasCompatibleAmmo() && this.ammo < weapon.getAmmoCapacity()) {

            this.loadIterationCount = 0;
            List<ItemBullet> compatibleBullets = weapon.getCompatibleBullets();

            if (!compatibleBullets.isEmpty()) {
                int maxToLoad = weapon.getAmmoCapacity() - this.ammo;
                int loaded = countAvailableBullets(compatibleBullets, player, maxToLoad);
                if (loaded > 0) {
                    this.ammo += loaded;
                    this.loadIterationCount = weapon.hasIteratedLoad() ? loaded : 1;
                }
            } else if (weapon.getAmmo() != null && player.inventory.hasItem(weapon.getAmmo())) {
                this.ammo = weapon.getAmmoCapacity();
                this.loadIterationCount = 1;
            }

            if (weapon.getReloadSound() != null) {
                player.playSound(weapon.getReloadSound(), 1.0F, 1.0F);
            }

            NewGunrizonsMod.CHANNEL
                .sendToServer(new WeaponActionMessage(WeaponActionMessage.WEAPON_LOAD, getItemInventoryIndex()));

            setState(WeaponState.RELOADING_START);
        } else if (!hasCompatibleAmmo()) {

            StatusMessageManager.INSTANCE
                .addAlertMessage(StatCollector.translateToLocalFormatted("gui.noAmmo"), 3, 250L, 200L);

            setState(WeaponState.NO_AMMO);
        }
    }

    @SideOnly(Side.CLIENT)
    private boolean hasCompatibleAmmo() {
        EntityPlayer player = getPlayer();
        ItemWeapon weapon = getWeapon();
        List<ItemBullet> compatibleBullets = weapon.getCompatibleBullets();
        if (!compatibleBullets.isEmpty()) {
            return InventoryUtils.hasCompatibleItem(compatibleBullets, player, (s) -> true);
        }
        return weapon.getAmmo() != null && player.inventory.hasItem(weapon.getAmmo());
    }

    private int countAvailableBullets(List<ItemBullet> compatibleBullets, EntityPlayer player, int maxToLoad) {
        int total = 0;
        for (ItemStack stack : player.inventory.mainInventory) {
            if (stack == null) continue;
            for (ItemBullet bullet : compatibleBullets) {
                if (stack.getItem() == bullet) {
                    total += stack.stackSize;
                    if (total >= maxToLoad) {
                        return maxToLoad;
                    }
                    break;
                }
            }
        }
        return total;
    }

    private void onStartLoadIteration() {
        if (getWeapon().getReloadIterationSound() != null) {
            getPlayer().playSound(getWeapon().getReloadIterationSound(), 1.0F, 1.0F);
        }
    }

    // ---- Attachment methods (from WeaponAttachmentAspect) ----

    @SideOnly(Side.CLIENT)
    public void toggleAttachmentMode() {
        if (this.state == WeaponState.IDLE
            && System.currentTimeMillis() >= this.stateUpdateTimestamp + CLICK_SPAMMING_TIMEOUT) {
            setState(WeaponState.MODIFYING);
        } else if (this.state == WeaponState.MODIFYING
            && System.currentTimeMillis() >= this.stateUpdateTimestamp + CLICK_SPAMMING_TIMEOUT * 2L) {
                setState(WeaponState.IDLE);
            }
    }

    public List<CompatibleAttachment> getActiveAttachments() {
        List<CompatibleAttachment> activeAttachments = new ArrayList<>();
        int[] ids = getActiveAttachmentIds();
        ItemWeapon weapon = getWeapon();
        for (int activeIndex : ids) {
            if (activeIndex != 0) {
                Item item = Item.getItemById(activeIndex);
                if (item instanceof ItemAttachment) {
                    CompatibleAttachment compatibleAttachment = weapon.getCompatibleAttachments()
                        .get(item);
                    if (compatibleAttachment != null) {
                        activeAttachments.add(compatibleAttachment);
                    }
                }
            }
        }
        return activeAttachments;
    }

    public static List<CompatibleAttachment> getActiveAttachments(EntityLivingBase player, ItemStack itemStack) {
        if (itemStack.stackTagCompound == null) {
            itemStack.stackTagCompound = new NBTTagCompound();
        }
        ItemInstance itemInstance = ItemInstanceRegistry.getItemInstance(player, itemStack);
        if (itemInstance instanceof ItemWeaponInstance) {
            return ((ItemWeaponInstance) itemInstance).getActiveAttachments();
        }
        List<CompatibleAttachment> activeAttachments = new ArrayList<>();
        int[] activeAttachmentsIds = new int[AttachmentCategory.VALUES.length];
        for (CompatibleAttachment attachment : ((ItemWeapon) itemStack.getItem()).getCompatibleAttachments()
            .values()) {
            if (attachment.isDefault()) {
                activeAttachmentsIds[attachment.getAttachment()
                    .getCategory()
                    .ordinal()] = Item.getIdFromItem(attachment.getAttachment());
            }
        }
        ItemWeapon weapon = (ItemWeapon) itemStack.getItem();
        for (int activeIndex : activeAttachmentsIds) {
            if (activeIndex != 0) {
                Item item = Item.getItemById(activeIndex);
                if (item instanceof ItemAttachment) {
                    CompatibleAttachment compatibleAttachment = weapon.getCompatibleAttachments()
                        .get(item);
                    if (compatibleAttachment != null) {
                        activeAttachments.add(compatibleAttachment);
                    }
                }
            }
        }
        return activeAttachments;
    }

    public static ItemAttachment getActiveAttachment(AttachmentCategory category, ItemWeaponInstance weaponInstance) {
        ItemAttachment itemAttachment = null;
        int[] activeAttachmentIds = weaponInstance.getActiveAttachmentIds();
        for (int activeIndex : activeAttachmentIds) {
            if (activeIndex != 0) {
                Item item = Item.getItemById(activeIndex);
                if (item instanceof ItemAttachment) {
                    CompatibleAttachment compatibleAttachment = weaponInstance.getWeapon()
                        .getCompatibleAttachments()
                        .get(item);
                    if (compatibleAttachment != null && category == compatibleAttachment.getAttachment()
                        .getCategory()) {
                        itemAttachment = compatibleAttachment.getAttachment();
                        break;
                    }
                }
            }
        }
        return itemAttachment;
    }

    public static boolean isActiveAttachment(ItemAttachment attachment, ItemWeaponInstance weaponInstance) {
        int[] activeAttachmentIds = weaponInstance.getActiveAttachmentIds();
        return Arrays.stream(activeAttachmentIds)
            .anyMatch((attachmentId) -> attachment == Item.getItemById(attachmentId));
    }

    // ---- Existing methods ----

    public boolean isSilencerOn() {
        return getActiveAttachmentIds()[AttachmentCategory.SILENCER.ordinal()] > 0;
    }

    public boolean isAttachmentZoomEnabled() {
        Item scopeItem = this.getAttachmentItemWithCategory(AttachmentCategory.SCOPE);
        return scopeItem instanceof ItemScope;
    }

    public void setAimed(boolean aimed) {
        if (this.aimed != aimed) {
            this.aimed = aimed;
            this.aimChangeTimestamp = System.currentTimeMillis();
        }
    }

    public int[] getActiveAttachmentIds() {
        if (this.activeAttachmentIds == null || this.activeAttachmentIds.length != AttachmentCategory.VALUES.length) {
            this.activeAttachmentIds = new int[AttachmentCategory.VALUES.length];

            for (CompatibleAttachment attachment : this.getWeapon()
                .getCompatibleAttachments()
                .values()) {
                if (attachment.isDefault()) {
                    this.activeAttachmentIds[attachment.getAttachment()
                        .getCategory()
                        .ordinal()] = Item.getIdFromItem(attachment.getAttachment());
                }
            }
        }

        return this.activeAttachmentIds;
    }

    public ItemAttachment getAttachmentItemWithCategory(AttachmentCategory category) {
        if (this.activeAttachmentIds != null && this.activeAttachmentIds.length > category.ordinal()) {
            Item activeAttachment = Item.getItemById(this.activeAttachmentIds[category.ordinal()]);
            return activeAttachment instanceof ItemAttachment ? (ItemAttachment) activeAttachment : null;
        } else {
            return null;
        }
    }

    public boolean needsOpticalScopePerspective() {
        if (this.isAimed()) {
            ItemAttachment scope = this.getAttachmentItemWithCategory(AttachmentCategory.SCOPE);
            return scope instanceof ItemScope && ((ItemScope) scope).isOptical();
        }
        return false;
    }

    public float getAimChangeProgress() {
        float delta = (float) (System.currentTimeMillis() - this.aimChangeTimestamp) / AIM_CHANGE_DURATION;
        float p = Math.min(Math.max(delta, 0.0F), 1.0F);
        if (!this.isAimed()) {
            p = 1.0F - p;
        }
        return p;
    }

    public String toString() {
        return this.getWeapon()
            .getName() + "[weapon]";
    }

    public static int getAmmo(ItemStack itemStack) {
        return itemStack != null && itemStack.stackTagCompound != null ? itemStack.stackTagCompound.getInteger(AMMO_TAG)
            : 0;
    }

    public static void setAmmo(ItemStack itemStack, int ammo) {
        if (itemStack != null) {
            if (itemStack.stackTagCompound == null) {
                itemStack.stackTagCompound = new NBTTagCompound();
            }
            itemStack.stackTagCompound.setInteger(AMMO_TAG, ammo);
        }
    }
}
