package dev.manere.spinmycrystals.mixin;

import dev.manere.spinmycrystals.config.SpinMyCrystalsConfig;
import dev.manere.spinmycrystals.renderer.EndCrystalSpecialRenderer;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.item.ModelRenderProperties;
import net.minecraft.client.renderer.item.SpecialModelWrapper;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.client.resources.model.geometry.QuadCollection;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.joml.Matrix4fc;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SpecialModelWrapper.class)
public class SpecialModelWrapperMixin {
    @Shadow @Final private SpecialModelRenderer<?> specialRenderer;
    @Shadow @Final private ModelRenderProperties properties;
    @Shadow @Final private Matrix4fc transformation;

    @Inject(method = "update", at = @At("HEAD"), cancellable = true)
    private void spinmycrystals$onUpdate(
        ItemStackRenderState output,
        ItemStack item,
        ItemModelResolver resolver,
        ItemDisplayContext displayContext,
        @Nullable ClientLevel level,
        @Nullable ItemOwner owner,
        int seed,
        CallbackInfo ci
    ) {
        if (this.specialRenderer instanceof EndCrystalSpecialRenderer crystalRenderer && !SpinMyCrystalsConfig.get().enabled) {
            QuadCollection baseQuads = crystalRenderer.getFallbackQuads();
            if (baseQuads != null) {
                output.appendModelIdentityElement(this);
                ItemStackRenderState.LayerRenderState layer = output.newLayer();
                if (item.hasFoil()) {
                    ItemStackRenderState.FoilType foilType = ItemStackRenderState.FoilType.STANDARD;
                    layer.setFoilType(foilType);
                    output.setAnimated();
                    output.appendModelIdentityElement(foilType);
                }

                if (crystalRenderer.getFallbackExtents() != null) {
                    layer.setExtents(crystalRenderer.getFallbackExtents());
                }
                layer.setLocalTransform(this.transformation);
                if (crystalRenderer.getFallbackProperties() != null) {
                    crystalRenderer.getFallbackProperties().applyToLayer(layer, displayContext);
                } else {
                    this.properties.applyToLayer(layer, displayContext);
                }
                layer.prepareQuadList().addAll(baseQuads.getAll());
                if (baseQuads.hasMaterialFlag(2)) {
                    output.setAnimated();
                }
                ci.cancel();
            }
        }
    }
}

