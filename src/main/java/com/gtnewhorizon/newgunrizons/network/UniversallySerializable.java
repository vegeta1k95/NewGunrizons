package com.gtnewhorizon.newgunrizons.network;

import io.netty.buffer.ByteBuf;

public interface UniversallySerializable {

    void init(ByteBuf buf);

    void serialize(ByteBuf buf);
}
