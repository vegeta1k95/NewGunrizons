package com.gtnewhorizon.newgunrizons.attachment;

/**
 * A custom named render slot, typically used for auxiliary attachment sub-parts.
 * <p>
 * Unlike {@link StandardPart}, named parts represent attachment-specific rendering
 * positions and are not recognized by {@code instanceof StandardPart} checks in renderers.
 */
public final class NamedPart implements Part {

    private final String name;

    public NamedPart(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Part [" + name + "]";
    }
}
