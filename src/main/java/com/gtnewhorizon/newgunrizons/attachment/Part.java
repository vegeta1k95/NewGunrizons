package com.gtnewhorizon.newgunrizons.attachment;

/**
 * Represents a renderable part of a weapon or attachment.
 * <p>
 * {@link StandardPart Standard} parts define built-in rendering positions (main item, hands).
 * {@link NamedPart Named} parts represent custom attachment-specific render slots.
 * <p>
 * Renderers use {@code instanceof StandardPart} to distinguish built-in positions from
 * custom attachment parts.
 */
public interface Part {

    Part MAIN_ITEM = new StandardPart("MAIN_ITEM");
    Part RIGHT_HAND = new StandardPart("RIGHT_HAND");
    Part LEFT_HAND = new StandardPart("LEFT_HAND");
    Part NONE = new StandardPart("NONE");
}
