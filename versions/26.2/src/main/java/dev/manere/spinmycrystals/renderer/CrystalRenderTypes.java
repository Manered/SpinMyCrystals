package dev.manere.spinmycrystals.renderer;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.ColorTargetState;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.minecraft.client.renderer.BindGroupLayouts;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CrystalRenderTypes {
    public static final RenderPipeline NO_SHADE_CULL_PIPELINE = RenderPipeline.builder(RenderPipelines.ENTITY_SNIPPET)
        .withLocation("pipeline/crystal_no_shade_cull")
        .withShaderDefine("ALPHA_CUTOUT", 0.1F)
        .withShaderDefine("NO_CARDINAL_LIGHTING")
        .withBindGroupLayout(BindGroupLayouts.SAMPLER1)
        .withColorTargetState(new ColorTargetState(BlendFunction.TRANSLUCENT))
        .withCull(true)
        .build();

    public static final RenderPipeline NO_SHADE_NO_CULL_PIPELINE = RenderPipeline.builder(RenderPipelines.ENTITY_SNIPPET)
        .withLocation("pipeline/crystal_no_shade_no_cull")
        .withShaderDefine("ALPHA_CUTOUT", 0.1F)
        .withShaderDefine("NO_CARDINAL_LIGHTING")
        .withBindGroupLayout(BindGroupLayouts.SAMPLER1)
        .withColorTargetState(new ColorTargetState(BlendFunction.TRANSLUCENT))
        .withCull(false)
        .build();

    private static final Map<Identifier, RenderType> NO_SHADE_CULL_CACHE = new ConcurrentHashMap<>();
    private static final Map<Identifier, RenderType> NO_SHADE_NO_CULL_CACHE = new ConcurrentHashMap<>();
    private static final Map<Identifier, RenderType> SHADED_CULL_CACHE = new ConcurrentHashMap<>();

    public static RenderType get(Identifier texture, boolean noShade, boolean culled) {
        if (!noShade && !culled) {
            return RenderTypes.entityTranslucent(texture);
        }
        if (!noShade) {
            return SHADED_CULL_CACHE.computeIfAbsent(texture, id -> RenderType.create(
                "crystal_shaded_cull",
                RenderSetup.builder(RenderPipelines.ENTITY_TRANSLUCENT_CULL)
                    .withTexture("Sampler0", id)
                    .useLightmap()
                    .useOverlay()
                    .affectsCrumbling()
                    .sortOnUpload()
                    .setOutline(RenderSetup.OutlineProperty.AFFECTS_OUTLINE)
                    .createRenderSetup()
            ));
        }
        if (culled) {
            return NO_SHADE_CULL_CACHE.computeIfAbsent(texture, id -> RenderType.create(
                "crystal_no_shade_cull",
                RenderSetup.builder(NO_SHADE_CULL_PIPELINE)
                    .withTexture("Sampler0", id)
                    .useLightmap()
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
                .useLightmap()
                .useOverlay()
                .affectsCrumbling()
                .sortOnUpload()
                .setOutline(RenderSetup.OutlineProperty.AFFECTS_OUTLINE)
                .createRenderSetup()
        ));
    }
}
