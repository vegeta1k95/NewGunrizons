package com.gtnewhorizon.newgunrizons.items.instances;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.Getter;
import lombok.Setter;

public abstract class ItemInstance {

    private static final String INSTANCE_TAG = "gunrizons_instance";

    private static final byte TYPE_WEAPON = 1;
    private static final byte TYPE_GRENADE = 2;

    @Setter
    @Getter
    protected EntityLivingBase player;

    @Getter
    protected Item item;

    @Setter
    @Getter
    protected int itemInventoryIndex;

    public ItemInstance() {}

    public ItemInstance(int itemInventoryIndex, EntityLivingBase player) {
        this.itemInventoryIndex = itemInventoryIndex;
        this.player = player;
        ItemStack itemStack = player.getHeldItem();
        if (itemStack != null) {
            this.item = itemStack.getItem();
        }
    }

    public ItemInstance(int itemInventoryIndex, EntityLivingBase player, ItemStack itemStack) {
        this.itemInventoryIndex = itemInventoryIndex;
        this.player = player;
        if (itemStack != null) {
            this.item = itemStack.getItem();
        }
    }

    public ItemStack getItemStack() {
        return this.player instanceof EntityPlayer
            ? ((EntityPlayer) this.player).inventory.getStackInSlot(this.itemInventoryIndex)
            : null;
    }

    public void readFromBuf(ByteBuf buf) {
        this.item = Item.getItemById(buf.readInt());
        this.itemInventoryIndex = buf.readInt();
    }

    public void writeToBuf(ByteBuf buf) {
        buf.writeInt(Item.getIdFromItem(this.item));
        buf.writeInt(this.itemInventoryIndex);
    }

    public abstract void writeByteType(ByteBuf buf);

    @SuppressWarnings("unchecked")
    public static <T> T fromStack(ItemStack itemStack) {
        if (itemStack == null || itemStack.stackTagCompound == null) {
            return null;
        }
        byte[] bytes = itemStack.stackTagCompound.getByteArray(INSTANCE_TAG);
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        ByteBuf buf = Unpooled.copiedBuffer(bytes);
        try {
            byte typeId = buf.readByte();
            ItemInstance instance;
            switch (typeId) {
                case TYPE_WEAPON:
                    instance = new ItemWeaponInstance();
                    break;
                case TYPE_GRENADE:
                    instance = new ItemGrenadeInstance();
                    break;
                default:
                    return null;
            }
            instance.readFromBuf(buf);
            return (T) instance;
        } finally {
            buf.release();
        }
    }

    public static void toStack(ItemStack itemStack, ItemInstance instance) {
        if (itemStack == null) return;
        if (itemStack.stackTagCompound == null) {
            itemStack.stackTagCompound = new NBTTagCompound();
        }
        if (instance == null) {
            itemStack.stackTagCompound.removeTag(INSTANCE_TAG);
            return;
        }
        ByteBuf buf = Unpooled.buffer();
        try {
            instance.writeByteType(buf);
            instance.writeToBuf(buf);
            byte[] bytes = new byte[buf.readableBytes()];
            buf.readBytes(bytes);
            itemStack.stackTagCompound.setByteArray(INSTANCE_TAG, bytes);
        } finally {
            buf.release();
        }
    }
}
