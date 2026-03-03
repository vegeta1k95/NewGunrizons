package com.vicmatskiv.weaponlib;

import net.minecraft.client.model.ModelBase;

public class ItemBullet extends ItemAttachment {

    public ItemBullet(String modId, AttachmentCategory category, ModelBase model, String textureName, String crosshair,
        ItemAttachment.ApplyHandler apply, ItemAttachment.ApplyHandler remove) {
        super(modId, category, model, textureName, crosshair, apply, remove);
    }

    public static final class Builder extends AttachmentBuilder {

        public Builder() {
            withMaxStackSize(64);
        }

        protected ItemAttachment createAttachment(ModContext modContext) {
            return new ItemBullet(
                getModId(),
                AttachmentCategory.BULLET,
                getModel(),
                getTextureName(),
                null,
                null,
                null);
        }
    }
}
