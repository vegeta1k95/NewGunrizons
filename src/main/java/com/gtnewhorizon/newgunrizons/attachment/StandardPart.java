package com.gtnewhorizon.newgunrizons.attachment;

/**
 * A built-in rendering position shared by all weapons and attachments.
 * <p>
 * Renderers use {@code instanceof StandardPart} to distinguish these built-in positions
 * from custom attachment-specific parts ({@link NamedPart}).
 *
 * @see Part#MAIN_ITEM
 * @see Part#RIGHT_HAND
 * @see Part#LEFT_HAND
 * @see Part#NONE
 */
public final class StandardPart implements Part {

    private final String name;

    StandardPart(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }
}
