package com.gtnewhorizon.newgunrizons.client.debug;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.EnumMap;
import java.util.Map;
import java.util.function.Consumer;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.Minecraft;

import org.lwjgl.opengl.GL11;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class PositionDebugger {

    public enum Mode {

        FIRST_PERSON("First Person"),
        FIRST_PERSON_RECOILED("Recoiled"),
        FIRST_PERSON_ZOOMING("Zooming"),
        FIRST_PERSON_RUNNING("Running"),
        FIRST_PERSON_ZOOMING_RECOILED("Zoom Recoiled"),
        FIRST_PERSON_MODIFYING("Modifying"),
        LEFT_HAND("Left Hand"),
        RIGHT_HAND("Right Hand"),
        LEFT_HAND_ZOOMING("Left Zoom"),
        RIGHT_HAND_ZOOMING("Right Zoom"),
        THIRD_PERSON("Third Person"),
        ENTITY("Entity"),
        INVENTORY("Inventory"),
        MUZZLE_FLASH("Muzzle Flash");

        public final String displayName;

        Mode(String displayName) {
            this.displayName = displayName;
        }
    }

    public static class TransformState {

        public float translateX = 0f;
        public float translateY = 0f;
        public float translateZ = 0f;
        public float rotateX = 0f;
        public float rotateY = 0f;
        public float rotateZ = 0f;
        public float scale = 1.0f;

        public TransformState() {}

        public TransformState(float tx, float ty, float tz, float rx, float ry, float rz, float scale) {
            this.translateX = tx;
            this.translateY = ty;
            this.translateZ = tz;
            this.rotateX = rx;
            this.rotateY = ry;
            this.rotateZ = rz;
            this.scale = scale;
        }

        public void copyFrom(TransformState other) {
            this.translateX = other.translateX;
            this.translateY = other.translateY;
            this.translateZ = other.translateZ;
            this.rotateX = other.rotateX;
            this.rotateY = other.rotateY;
            this.rotateZ = other.rotateZ;
            this.scale = other.scale;
        }
    }

    @Setter
    @Getter
    private static boolean active;
    /** True only while the debug screen is open — forces all lambdas to use currentMode. */
    @Setter
    @Getter
    private static boolean screenOpen;
    @Setter
    @Getter
    private static Mode currentMode = Mode.FIRST_PERSON;

    /** Override for recoil animation duration (ms). -1 means use weapon's default. */
    @Setter
    @Getter
    private static int recoilDurationOverride = -1;

    /** Override for shooting (post-recoil) animation duration (ms). -1 means use weapon's default. */
    @Setter
    @Getter
    private static int shootingDurationOverride = -1;

    /** Override for camera recoil duration (ms). -1 means use weapon's default. */
    @Setter
    @Getter
    private static int cameraRecoilDurationOverride = -1;

    private static final Map<Mode, TransformState> states = new EnumMap<>(Mode.class);
    private static final Map<Mode, TransformState> initialStates = new EnumMap<>(Mode.class);
    private static boolean initialized;

    static {
        for (Mode mode : Mode.values()) {
            states.put(mode, new TransformState());
        }
    }

    public static TransformState getState(Mode mode) {
        return states.get(mode);
    }

    public static void applyTransforms(TransformState s) {
        if (s == null) return;
        if (s.rotateY != 0) GL11.glRotatef(s.rotateY, 0.0F, 1.0F, 0.0F);
        if (s.rotateX != 0) GL11.glRotatef(s.rotateX, 1.0F, 0.0F, 0.0F);
        if (s.rotateZ != 0) GL11.glRotatef(s.rotateZ, 0.0F, 0.0F, 1.0F);
        GL11.glTranslatef(s.translateX, s.translateY, s.translateZ);
        GL11.glScaled(s.scale, s.scale, s.scale);
    }

    public static void applyTransforms(Mode mode) {
        applyTransforms(states.get(mode));
    }

    /**
     * Registers a debug-capable positioning for the given mode.
     * The initial TransformState defines both the normal (non-debug) positioning
     * and the starting slider values when the debugger opens.
     */
    public static <T> Consumer<T> debuggable(Mode mode, TransformState initial) {
        initialStates.put(mode, initial);
        return (t) -> {
            if (!active) {
                applyTransforms(initial);
            } else if (!screenOpen) {
                // Screen closed: each lambda uses its own mode's debug state
                applyTransforms(mode);
            } else if (currentMode == Mode.MUZZLE_FLASH) {
                // Muzzle flash debug: render weapon normally
                applyTransforms(initial);
            } else if (isHandMode(mode)) {
                // Hand lambda: resolve to the correct hand state for the current selection
                applyTransforms(resolveHandMode(mode, currentMode));
            } else {
                // Gun lambda: resolve to the correct gun state for the current selection
                applyTransforms(resolveGunMode(currentMode));
            }
        };
    }

    private static boolean isHandMode(Mode m) {
        return m == Mode.LEFT_HAND || m == Mode.RIGHT_HAND
            || m == Mode.LEFT_HAND_ZOOMING
            || m == Mode.RIGHT_HAND_ZOOMING;
    }

    private static boolean isZoomRelated(Mode m) {
        return m == Mode.FIRST_PERSON_ZOOMING || m == Mode.FIRST_PERSON_ZOOMING_RECOILED
            || m == Mode.LEFT_HAND_ZOOMING
            || m == Mode.RIGHT_HAND_ZOOMING;
    }

    /** Maps any mode (including hand modes) to the associated gun mode. */
    private static Mode resolveGunMode(Mode m) {
        switch (m) {
            case LEFT_HAND:
            case RIGHT_HAND:
                return Mode.FIRST_PERSON;
            case LEFT_HAND_ZOOMING:
            case RIGHT_HAND_ZOOMING:
                return Mode.FIRST_PERSON_ZOOMING;
            default:
                return m;
        }
    }

    /** For a hand lambda, pick the correct hand variant based on whether the current selection is zoom-related. */
    private static Mode resolveHandMode(Mode handMode, Mode selected) {
        boolean zoom = isZoomRelated(selected);
        if (handMode == Mode.LEFT_HAND || handMode == Mode.LEFT_HAND_ZOOMING) {
            return zoom ? Mode.LEFT_HAND_ZOOMING : Mode.LEFT_HAND;
        }
        if (handMode == Mode.RIGHT_HAND || handMode == Mode.RIGHT_HAND_ZOOMING) {
            return zoom ? Mode.RIGHT_HAND_ZOOMING : Mode.RIGHT_HAND;
        }
        return handMode;
    }

    /**
     * Returns the recoil duration to use: the debug override if active and set, otherwise the weapon's default.
     */
    public static int getEffectiveRecoilDuration(int weaponDefault) {
        if (active && recoilDurationOverride >= 0) {
            return recoilDurationOverride;
        }
        return weaponDefault;
    }

    public static int getEffectiveShootingDuration(int weaponDefault) {
        if (active && shootingDurationOverride >= 0) {
            return shootingDurationOverride;
        }
        return weaponDefault;
    }

    public static String generateCode(Mode mode) {
        TransformState s = states.get(mode);
        if (s == null) return "";
        StringBuilder sb = new StringBuilder();
        if (mode == Mode.MUZZLE_FLASH) {
            sb.append(String.format(".withFlashOffsetX(() -> %.3fF)%n", s.translateX));
            sb.append(String.format(".withFlashOffsetY(() -> %.3fF)%n", s.translateY));
            sb.append(String.format(".withFlashOffsetZ(() -> %.3fF)%n", s.translateZ));
            sb.append(String.format(".withFlashScale(() -> %.1fF)", s.scale));
            return sb.toString();
        }
        sb.append(
            String.format(
                "new TransformState(%.3ff, %.3ff, %.3ff, %.1ff, %.1ff, %.1ff, %.1ff)",
                s.translateX,
                s.translateY,
                s.translateZ,
                s.rotateX,
                s.rotateY,
                s.rotateZ,
                s.scale));
        if ((mode == Mode.FIRST_PERSON_RECOILED || mode == Mode.FIRST_PERSON_ZOOMING_RECOILED)) {
            if (recoilDurationOverride >= 0) {
                sb.append(String.format("%n.withRecoilAnimationDuration(%d)", recoilDurationOverride));
            }
            if (shootingDurationOverride >= 0) {
                sb.append(String.format("%n.withShootingAnimationDuration(%d)", shootingDurationOverride));
            }
            if (cameraRecoilDurationOverride >= 0) {
                sb.append(String.format("%n.withCameraRecoilDuration(%d)", cameraRecoilDurationOverride));
            }
        }
        return sb.toString();
    }

    public static void save() {
        Gson gson = new GsonBuilder().setPrettyPrinting()
            .create();
        JsonObject root = new JsonObject();
        for (Mode mode : Mode.values()) {
            root.add(mode.name(), gson.toJsonTree(states.get(mode)));
        }
        if (recoilDurationOverride >= 0) {
            root.addProperty("recoilDuration", recoilDurationOverride);
        }
        if (shootingDurationOverride >= 0) {
            root.addProperty("shootingDuration", shootingDurationOverride);
        }
        if (cameraRecoilDurationOverride >= 0) {
            root.addProperty("cameraRecoilDuration", cameraRecoilDurationOverride);
        }
        File file = getSaveFile();
        file.getParentFile()
            .mkdirs();
        try (FileWriter writer = new FileWriter(file)) {
            gson.toJson(root, writer);
        } catch (Exception ignored) {}
    }

    public static void load() {
        if (initialized) return;
        initialized = true;
        // Seed debug states from registered initial values (factory constants)
        for (Map.Entry<Mode, TransformState> entry : initialStates.entrySet()) {
            TransformState debugState = states.get(entry.getKey());
            if (debugState != null) {
                debugState.copyFrom(entry.getValue());
            }
        }
        // Overlay with saved values from file (user's tweaked values)
        File file = getSaveFile();
        if (!file.exists()) return;
        try (FileReader reader = new FileReader(file)) {
            Gson gson = new Gson();
            JsonObject root = new JsonParser().parse(reader)
                .getAsJsonObject();
            for (Mode mode : Mode.values()) {
                if (root.has(mode.name())) {
                    states.put(
                        mode,
                        gson.fromJson(root.getAsJsonObject(mode.name()), TransformState.class));
                }
            }
            if (root.has("recoilDuration")) {
                recoilDurationOverride = root.get("recoilDuration")
                    .getAsInt();
            }
            if (root.has("shootingDuration")) {
                shootingDurationOverride = root.get("shootingDuration")
                    .getAsInt();
            }
            if (root.has("cameraRecoilDuration")) {
                cameraRecoilDurationOverride = root.get("cameraRecoilDuration")
                    .getAsInt();
            }
        } catch (Exception ignored) {}
    }

    private static File getSaveFile() {
        return new File(
            Minecraft.getMinecraft().mcDataDir,
            "config/newgunrizons_debug_positions.json");
    }
}
