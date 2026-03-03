package com.vicmatskiv.weaponlib.network;

import com.vicmatskiv.weaponlib.state.Permit;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import io.netty.buffer.ByteBuf;

public class PermitMessage implements IMessage {

    private Permit<?> permit;
    private Object context;

    public PermitMessage() {}

    public PermitMessage(Permit<?> permit, Object context) {
        this.permit = permit;
        this.context = context;
    }

    public Permit<?> getPermit() {
        return this.permit;
    }

    public Object getContext() {
        return this.context;
    }

    public void fromBytes(ByteBuf buf) {
        TypeRegistry typeRegistry = TypeRegistry.getInstance();
        this.context = typeRegistry.fromBytes(buf);
        this.permit = typeRegistry.fromBytes(buf);
    }

    public void toBytes(ByteBuf buf) {
        TypeRegistry typeRegistry = TypeRegistry.getInstance();
        typeRegistry.toBytes((UniversallySerializable) this.context, buf);
        typeRegistry.toBytes(this.permit, buf);
    }
}
