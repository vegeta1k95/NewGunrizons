package com.gtnewhorizon.newgunrizons.attachment;

import java.util.List;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;

/** An item that supports attachments (weapons, grenades). */
@FunctionalInterface
public interface AttachmentContainer {

    List<CompatibleAttachment> getActiveAttachments(EntityLivingBase entity, ItemStack itemStack);
}
