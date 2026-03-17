package com.gtnewhorizon.newgunrizons.items.instances;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;

import com.gtnewhorizon.newgunrizons.grenade.GrenadeState;
import com.gtnewhorizon.newgunrizons.items.ItemGrenade;

import io.netty.buffer.ByteBuf;
import lombok.Getter;
import lombok.Setter;

public class ItemGrenadeInstance extends ItemInstance<GrenadeState> {

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

    public ItemGrenadeInstance(int itemInventoryIndex, EntityLivingBase player, ItemStack itemStack) {
        super(itemInventoryIndex, player, itemStack);
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

    protected void setAmmo(int ammo) {
        if (ammo != this.ammo) {
            this.ammo = ammo;
        }
    }

    protected void updateWith(ItemInstance<GrenadeState> otherItemInstance, boolean updateManagedState) {
        super.updateWith(otherItemInstance, updateManagedState);
        ItemGrenadeInstance other = (ItemGrenadeInstance) otherItemInstance;
        this.setAmmo(other.ammo);
    }

    public ItemGrenade getWeapon() {
        return (ItemGrenade) this.item;
    }

    public String toString() {
        return this.getWeapon().getName() + "[grenade]";
    }
}
