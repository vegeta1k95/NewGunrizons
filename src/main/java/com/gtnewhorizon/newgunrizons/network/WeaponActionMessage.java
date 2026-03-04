package com.gtnewhorizon.newgunrizons.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import io.netty.buffer.ByteBuf;

/**
 * Client-to-server message requesting a weapon/magazine action that involves
 * server-side inventory operations (consuming ammo, swapping attachments, etc.).
 */
public class WeaponActionMessage implements IMessage {

    public static final byte WEAPON_LOAD = 0;
    public static final byte WEAPON_UNLOAD = 1;
    public static final byte MAGAZINE_LOAD = 2;
    public static final byte CHANGE_ATTACHMENT = 3;

    private byte actionType;
    private int slotIndex;
    private byte attachmentCategory;

    public WeaponActionMessage() {}

    public WeaponActionMessage(byte actionType, int slotIndex) {
        this(actionType, slotIndex, (byte) -1);
    }

    public WeaponActionMessage(byte actionType, int slotIndex, byte attachmentCategory) {
        this.actionType = actionType;
        this.slotIndex = slotIndex;
        this.attachmentCategory = attachmentCategory;
    }

    public byte getActionType() {
        return this.actionType;
    }

    public int getSlotIndex() {
        return this.slotIndex;
    }

    public byte getAttachmentCategory() {
        return this.attachmentCategory;
    }

    public void fromBytes(ByteBuf buf) {
        this.actionType = buf.readByte();
        this.slotIndex = buf.readInt();
        this.attachmentCategory = buf.readByte();
    }

    public void toBytes(ByteBuf buf) {
        buf.writeByte(this.actionType);
        buf.writeInt(this.slotIndex);
        buf.writeByte(this.attachmentCategory);
    }
}
