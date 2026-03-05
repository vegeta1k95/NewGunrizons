package com.gtnewhorizon.newgunrizons.items;

import com.gtnewhorizon.newgunrizons.attachment.AttachmentBuilder;
import com.gtnewhorizon.newgunrizons.attachment.AttachmentCategory;
import com.gtnewhorizon.newgunrizons.config.ModContext;

/**
 * A bullet attachment type.
 * <p>
 * Exists primarily as a type marker so that {@code instanceof ItemBullet} checks
 * can distinguish bullets from other attachments. Bullets are stackable (default 64).
 */
public class ItemBullet extends ItemAttachment {

    public ItemBullet(AttachmentCategory category, String crosshair) {
        super(category, crosshair);
    }

    public static final class Builder extends AttachmentBuilder {

        public Builder() {
            withMaxStackSize(64);
        }

        public ItemAttachment createAttachment(ModContext modContext) {
            return new ItemBullet(AttachmentCategory.BULLET, null);
        }
    }
}
