package com.gtnewhorizon.newgunrizons.client.shaders;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

import net.minecraft.util.ResourceLocation;

import lombok.Getter;

public class ShaderEffect {

    @Getter
    private final ResourceLocation shaderLocation;
    private final List<UniformBinding> uniforms;
    @Getter
    private final UUID sourceId;

    public ShaderEffect(UUID sourceId, ResourceLocation location) {
        this.sourceId = sourceId;
        this.shaderLocation = location;
        this.uniforms = new ArrayList<>();
    }

    public ShaderEffect withUniform(String name, Function<ShaderContext, Object> supplier) {
        this.uniforms.add(new UniformBinding(name, supplier));
        return this;
    }

    public List<UniformBinding> getUniforms() {
        return Collections.unmodifiableList(this.uniforms);
    }

    @Getter
    public static final class UniformBinding {

        private final String name;
        private final Function<ShaderContext, Object> supplier;

        public UniformBinding(String name, Function<ShaderContext, Object> supplier) {
            this.name = name;
            this.supplier = supplier;
        }
    }
}
