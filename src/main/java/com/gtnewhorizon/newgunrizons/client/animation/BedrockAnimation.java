package com.gtnewhorizon.newgunrizons.client.animation;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.gtnewhorizon.newgunrizons.NewGunrizonsMod;

/**
 * Parses and stores Bedrock animation data from a {@code .animation.json} file.
 * Each file can contain multiple named animation clips.
 */
public class BedrockAnimation {

    private final Map<String, AnimationClip> clips = new LinkedHashMap<>();

    public BedrockAnimation(String animationPath) {
        this(NewGunrizonsMod.MODID, animationPath);
    }

    public BedrockAnimation(String domain, String animationPath) {
        String resourcePath = "/assets/" + domain + "/animations/" + animationPath + ".animation.json";
        InputStream is = BedrockAnimation.class.getResourceAsStream(resourcePath);
        if (is == null) {
            throw new RuntimeException("Animation not found on classpath: " + resourcePath);
        }
        try {
            JsonObject root = new JsonParser().parse(new InputStreamReader(is, StandardCharsets.UTF_8))
                .getAsJsonObject();
            parse(root);
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse animation: " + resourcePath, e);
        } finally {
            try {
                is.close();
            } catch (Exception ignored) {}
        }
    }

    private void parse(JsonObject root) {
        JsonObject animations = root.getAsJsonObject("animations");
        if (animations == null) return;

        for (Map.Entry<String, JsonElement> entry : animations.entrySet()) {
            String name = entry.getKey();
            JsonObject animObj = entry.getValue()
                .getAsJsonObject();
            clips.put(name, parseClip(animObj));
        }
    }

    private static AnimationClip parseClip(JsonObject animObj) {
        float length = animObj.has("animation_length") ? animObj.get("animation_length")
            .getAsFloat() : 0f;
        boolean loop = animObj.has("loop") && animObj.get("loop")
            .getAsBoolean();

        Map<String, BoneAnimation> bones = new LinkedHashMap<>();
        if (animObj.has("bones")) {
            JsonObject bonesObj = animObj.getAsJsonObject("bones");
            for (Map.Entry<String, JsonElement> boneEntry : bonesObj.entrySet()) {
                String boneName = boneEntry.getKey();
                JsonObject boneObj = boneEntry.getValue()
                    .getAsJsonObject();
                bones.put(boneName, parseBoneAnimation(boneObj));
            }
        }
        return new AnimationClip(length, loop, bones);
    }

    private static BoneAnimation parseBoneAnimation(JsonObject boneObj) {
        List<Keyframe> rotation = boneObj.has("rotation") ? parseKeyframes(boneObj.get("rotation")) : null;
        List<Keyframe> position = boneObj.has("position") ? parseKeyframes(boneObj.get("position")) : null;
        List<Keyframe> scale = boneObj.has("scale") ? parseKeyframes(boneObj.get("scale")) : null;
        return new BoneAnimation(rotation, position, scale);
    }

    private static List<Keyframe> parseKeyframes(JsonElement element) {
        if (element.isJsonArray()) {
            // Static value: single keyframe at t=0
            float[] value = parseVec3(element.getAsJsonArray());
            return Collections.singletonList(new Keyframe(0f, value));
        }

        if (!element.isJsonObject()) {
            return Collections.emptyList();
        }

        JsonObject obj = element.getAsJsonObject();
        List<Keyframe> keyframes = new ArrayList<>();
        for (Map.Entry<String, JsonElement> entry : obj.entrySet()) {
            float time = Float.parseFloat(entry.getKey());
            JsonElement val = entry.getValue();
            float[] value;
            if (val.isJsonArray()) {
                value = parseVec3(val.getAsJsonArray());
            } else if (val.isJsonObject()) {
                // Molang or complex keyframe — try "vector" or fall back to [0,0,0]
                JsonObject kfObj = val.getAsJsonObject();
                if (kfObj.has("vector")) {
                    value = parseVec3(kfObj.getAsJsonArray("vector"));
                } else {
                    value = new float[] { 0, 0, 0 };
                }
            } else {
                // Single numeric value — apply to all axes
                float v = val.getAsFloat();
                value = new float[] { v, v, v };
            }
            keyframes.add(new Keyframe(time, value));
        }
        keyframes.sort((a, b) -> Float.compare(a.time, b.time));
        return keyframes;
    }

    private static float[] parseVec3(JsonArray arr) {
        return new float[] {
            arr.get(0)
                .getAsFloat(),
            arr.get(1)
                .getAsFloat(),
            arr.get(2)
                .getAsFloat()
        };
    }

    public AnimationClip getClip(String name) {
        return clips.get(name);
    }

    public Map<String, AnimationClip> getClips() {
        return Collections.unmodifiableMap(clips);
    }

    // ---- Inner data classes ----

    public static class AnimationClip {

        public final float length;
        public final boolean loop;
        public final Map<String, BoneAnimation> bones;

        AnimationClip(float length, boolean loop, Map<String, BoneAnimation> bones) {
            this.length = length;
            this.loop = loop;
            this.bones = Collections.unmodifiableMap(bones);
        }
    }

    public static class BoneAnimation {

        public final List<Keyframe> rotation;
        public final List<Keyframe> position;
        public final List<Keyframe> scale;

        BoneAnimation(List<Keyframe> rotation, List<Keyframe> position, List<Keyframe> scale) {
            this.rotation = rotation;
            this.position = position;
            this.scale = scale;
        }
    }

    public static class Keyframe {

        public final float time;
        public final float[] value;

        Keyframe(float time, float[] value) {
            this.time = time;
            this.value = value;
        }
    }
}
