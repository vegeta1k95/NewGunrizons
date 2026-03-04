package com.gtnewhorizon.newgunrizons.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import io.netty.buffer.ByteBuf;

/**
 * Network message for synchronizing {@code PlayerItemInstance} state to the server.
 * Replaces the former {@code PermitMessage} for state sync purposes.
 */
public class SyncMessage implements IMessage {

    private Object context;

    public SyncMessage() {}

    public SyncMessage(Object context) {
        this.context = context;
    }

    public Object getContext() {
        return this.context;
    }

    public void fromBytes(ByteBuf buf) {
        this.context = TypeRegistry.getInstance()
            .fromBytes(buf);
    }

    public void toBytes(ByteBuf buf) {
        TypeRegistry.getInstance()
            .toBytes((UniversallySerializable) this.context, buf);
    }
}
