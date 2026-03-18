package com.gtnewhorizon.newgunrizons.items.instances;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

import com.gtnewhorizon.newgunrizons.NewGunrizonsMod;
import com.gtnewhorizon.newgunrizons.entities.EntityGrenade;
import com.gtnewhorizon.newgunrizons.entities.Explosion;
import com.gtnewhorizon.newgunrizons.grenade.GrenadeState;
import com.gtnewhorizon.newgunrizons.items.ItemGrenade;
import com.gtnewhorizon.newgunrizons.network.GrenadeMessage;
import com.gtnewhorizon.newgunrizons.network.StatusMessageManager;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import lombok.Getter;
import lombok.Setter;

public class ItemGrenadeInstance extends ItemInstance {

    private static final int SAFETY_PIN_ALERT_TIMEOUT = 1000;

    @Getter
    private GrenadeState state = GrenadeState.IDLE;
    @Getter
    private long stateUpdateTimestamp = System.currentTimeMillis();
    @Getter
    private int ammo;
    @Setter
    @Getter
    private long activationTimestamp;
    @Setter
    @Getter
    private long lastSafetyPinAlertTimestamp;
    @Getter
    @Setter
    private boolean throwingFar;

    public ItemGrenadeInstance() {}

    public ItemGrenadeInstance(int itemInventoryIndex, EntityPlayer player, ItemStack itemStack) {
        super(itemInventoryIndex, player, itemStack);
    }

    public void setState(GrenadeState state) {
        this.state = state;
        this.stateUpdateTimestamp = System.currentTimeMillis();
    }

    public void readFromBuf(ByteBuf buf) {
        super.readFromBuf(buf);
        this.state = GrenadeState.values()[buf.readInt()];
        this.ammo = buf.readInt();
    }

    public void writeToBuf(ByteBuf buf) {
        super.writeToBuf(buf);
        buf.writeInt(this.state != null ? this.state.ordinal() : 0);
        buf.writeInt(this.ammo);
    }

    @Override
    public void writeByteType(ByteBuf buf) {
        buf.writeByte(2);
    }

    protected void setAmmo(int ammo) {
        if (ammo != this.ammo) {
            this.ammo = ammo;
        }
    }

    public ItemGrenade getWeapon() {
        return (ItemGrenade) this.item;
    }

    public String toString() {
        return this.getWeapon()
            .getName() + "[grenade]";
    }

    // ---- Attack methods (from GrenadeAttackAspect) ----

    @SideOnly(Side.CLIENT)
    public void onAttackButtonClick(boolean throwingFar) {
        this.throwingFar = throwingFar;
        if (this.state == GrenadeState.IDLE) {
            if (getWeapon().hasSafetyPin()) {
                onTakeSafetyPinOff();
                setState(GrenadeState.SAFETY_PIN_OFF);
            } else {
                setState(GrenadeState.THROWING);
            }
        } else if (this.state == GrenadeState.STRIKER_LEVER_RELEASED) {
            setState(GrenadeState.THROWING);
        }
    }

    @SideOnly(Side.CLIENT)
    public void onAttackButtonUp(boolean throwingFar) {
        this.throwingFar = throwingFar;
        if (this.state == GrenadeState.SAFETY_PIN_OFF) {
            onReleaseStrikerLever();
            setState(GrenadeState.STRIKER_LEVER_RELEASED);
        }
    }

    public void update() {
        if (this.state == GrenadeState.STRIKER_LEVER_RELEASED) {
            if (System.currentTimeMillis() > this.lastSafetyPinAlertTimestamp + SAFETY_PIN_ALERT_TIMEOUT) {
                long remainingTimeUntilExplosion = (long) getWeapon().getExplosionTimeout()
                    - (System.currentTimeMillis() - this.activationTimestamp);
                if (remainingTimeUntilExplosion < 0L) {
                    remainingTimeUntilExplosion = 0L;
                }

                String message = StatCollector.translateToLocalFormatted(
                    "gui.grenadeExplodes",
                    Math.round((float) remainingTimeUntilExplosion / 1000.0F));
                StatusMessageManager.INSTANCE.addAlertMessage(message, 1, 1000L, 0L);
                this.lastSafetyPinAlertTimestamp = System.currentTimeMillis();
            }
        }

        boolean changed;
        do {
            changed = false;
            switch (this.state) {
                case STRIKER_LEVER_RELEASED: {
                    if (System.currentTimeMillis()
                        >= this.stateUpdateTimestamp + (long) getWeapon().getExplosionTimeout()) {
                        onExplode();
                        setState(GrenadeState.EXPLODED_IN_HANDS);
                        changed = true;
                    }
                    break;
                }
                case THROWING: {
                    if ((double) System.currentTimeMillis()
                        >= (double) this.stateUpdateTimestamp + getWeapon().getTotalThrowingDuration() * 1.1D) {
                        onThrow();
                        setState(GrenadeState.THROWN);
                        changed = true;
                    }
                    break;
                }
                case THROWN:
                case EXPLODED_IN_HANDS: {
                    if (System.currentTimeMillis() > this.stateUpdateTimestamp + getWeapon().getReequipTimeout()) {
                        setState(GrenadeState.IDLE);
                        changed = true;
                    }
                    break;
                }
                default:
                    break;
            }
        } while (changed);
    }

    private void onTakeSafetyPinOff() {
        if (getWeapon().getSafetyPinOffSound() != null) {
            getPlayer().playSound(getWeapon().getSafetyPinOffSound(), 1.0F, 1.0F);
        }
    }

    private void onReleaseStrikerLever() {
        this.activationTimestamp = System.currentTimeMillis();
    }

    private void onExplode() {
        NewGunrizonsMod.CHANNEL.sendToServer(new GrenadeMessage(this, 0L));
    }

    private void onThrow() {
        long activationTimestamp;
        if (getWeapon().getExplosionTimeout() > 0) {
            activationTimestamp = this.activationTimestamp;
        } else {
            activationTimestamp = -1L;
        }

        if (getWeapon().getThrowSound() != null) {
            getPlayer().playSound(getWeapon().getThrowSound(), 1.0F, 1.0F);
        }

        NewGunrizonsMod.CHANNEL.sendToServer(new GrenadeMessage(this, activationTimestamp));
    }

    // ---- Server-side throw ----

    public void serverThrow(EntityPlayer player, long activationTimestamp) {
        serverThrowGrenade(player, this, activationTimestamp);
        int slot = getItemInventoryIndex();
        if (player.inventory.mainInventory[slot] != null) {
            if (--player.inventory.mainInventory[slot].stackSize <= 0) {
                player.inventory.mainInventory[slot] = null;
            }
        }
    }

    public static void serverThrowGrenade(EntityPlayer player, ItemGrenadeInstance instance, long activationTimestamp) {
        if (activationTimestamp == 0L) {
            Explosion.createServerSideExplosion(
                player.worldObj,
                null,
                player.posX,
                player.posY,
                player.posZ,
                instance.getWeapon()
                    .getExplosionStrength(),
                false,
                true);
        } else {
            float velocity = instance.isThrowingFar() ? instance.getWeapon()
                .getFarVelocity()
                : instance.getWeapon()
                    .getVelocity();
            EntityGrenade entityGrenade = (new EntityGrenade.Builder()).withThrower(player)
                .withActivationTimestamp(activationTimestamp)
                .withGrenade(instance.getWeapon())
                .withExplosionStrength(
                    instance.getWeapon()
                        .getExplosionStrength())
                .withExplosionTimeout(
                    instance.getWeapon()
                        .getExplosionTimeout())
                .withVelocity(velocity)
                .withGravityVelocity(
                    instance.getWeapon()
                        .getGravityVelocity())
                .withRotationSlowdownFactor(
                    instance.getWeapon()
                        .getRotationSlowdownFactor())
                .build();
            player.worldObj.spawnEntityInWorld(entityGrenade);
        }
    }
}
