package dev.manere.spinmycrystals.renderer;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.shaders.UniformType;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CrystalRenderTypes {
    public static final RenderPipeline NO_SHADE_CULL_PIPELINE = RenderPipeline.builder()
        .withUniform("DynamicTransforms", UniformType.UNIFORM_BUFFER)
        .withUniform("Projection", UniformType.UNIFORM_BUFFER)
        .withUniform("Fog", UniformType.UNIFORM_BUFFER)
        .withUniform("Lighting", UniformType.UNIFORM_BUFFER)
        .withVertexShader("core/entity")
        .withFragmentShader("core/entity")
        .withSampler("Sampler0")
        .withSampler("Sampler1")
        .withVertexFormat(DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS)
        .withLocation("pipeline/crystal_no_shade_cull")
        .withShaderDefine("ALPHA_CUTOUT", 0.1F)
        .withShaderDefine("NO_CARDINAL_LIGHTING")
        .withShaderDefine("EMISSIVE")
        .withBlend(BlendFunction.TRANSLUCENT)
        .withCull(true)
        .build();

    public static final RenderPipeline NO_SHADE_NO_CULL_PIPELINE = RenderPipeline.builder()
        .withUniform("DynamicTransforms", UniformType.UNIFORM_BUFFER)
        .withUniform("Projection", UniformType.UNIFORM_BUFFER)
        .withUniform("Fog", UniformType.UNIFORM_BUFFER)
        .withUniform("Lighting", UniformType.UNIFORM_BUFFER)
        .withVertexShader("core/entity")
        .withFragmentShader("core/entity")
        .withSampler("Sampler0")
        .withSampler("Sampler1")
        .withVertexFormat(DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS)
        .withLocation("pipeline/crystal_no_shade_no_cull")
        .withShaderDefine("ALPHA_CUTOUT", 0.1F)
        .withShaderDefine("NO_CARDINAL_LIGHTING")
        .withShaderDefine("EMISSIVE")
        .withBlend(BlendFunction.TRANSLUCENT)
        .withCull(false)
        .build();

    private static final Map<Identifier, RenderType> NO_SHADE_CULL_CACHE = new ConcurrentHashMap<>();
    private static final Map<Identifier, RenderType> NO_SHADE_NO_CULL_CACHE = new ConcurrentHashMap<>();

    public static @NonNull RenderType get(@NonNull Identifier texture, boolean noShade, boolean culled) {
        if (!noShade && !culled) {
            return RenderTypes.entityTranslucent(texture);
        }
        if (!noShade) {
            return RenderTypes.itemEntityTranslucentCull(texture);
        }
        if (culled) {
            return NO_SHADE_CULL_CACHE.computeIfAbsent(texture, id -> RenderType.create(
                "crystal_no_shade_cull",
                RenderSetup.builder(NO_SHADE_CULL_PIPELINE)
                    .withTexture("Sampler0", id)
                    .useOverlay()
                    .affectsCrumbling()
                    .sortOnUpload()
                    .setOutline(RenderSetup.OutlineProperty.AFFECTS_OUTLINE)
                    .createRenderSetup()
            ));
        }
        return NO_SHADE_NO_CULL_CACHE.computeIfAbsent(texture, id -> RenderType.create(
            "crystal_no_shade_no_cull",
            RenderSetup.builder(NO_SHADE_NO_CULL_PIPELINE)
                .withTexture("Sampler0", id)
                .useOverlay()
                .affectsCrumbling()
                .sortOnUpload()
                .setOutline(RenderSetup.OutlineProperty.AFFECTS_OUTLINE)
                .createRenderSetup()
        ));
    }
}
