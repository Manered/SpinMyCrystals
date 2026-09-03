package dev.manere.spinmycrystals.renderer;

import com.google.common.base.Suppliers;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.MapCodec;
import dev.manere.spinmycrystals.SpinMyCrystals;
import dev.manere.spinmycrystals.config.SpinMyCrystalsConfig;
import dev.manere.spinmycrystals.math.CrystalRotation;
import dev.manere.spinmycrystals.model.SpinningCrystalModel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.dispatch.BlockModelRotation;
import net.minecraft.client.renderer.entity.state.EndCrystalRenderState;
import net.minecraft.client.renderer.item.CuboidItemModelWrapper;
import net.minecraft.client.renderer.item.ModelRenderProperties;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ResolvedModel;
import net.minecraft.client.resources.model.geometry.QuadCollection;
import net.minecraft.client.resources.model.sprite.TextureSlots;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.joml.Quaternionf;
import org.joml.Vector3fc;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class EndCrystalSpecialRenderer implements SpecialModelRenderer<EndCrystalRenderState> {
    private static final Identifier TEXTURE = Identifier.withDefaultNamespace("textures/entity/end_crystal/end_crystal.png");

    private final SpinningCrystalModel model;
    private final @Nullable QuadCollection fallbackQuads;
    private final @Nullable Supplier<Vector3fc[]> fallbackExtents;
    private final @Nullable ModelRenderProperties fallbackProperties;

    public EndCrystalSpecialRenderer(
        @NonNull SpinningCrystalModel model,
        @Nullable QuadCollection fallbackQuads,
        @Nullable Supplier<Vector3fc[]> fallbackExtents,
        @Nullable ModelRenderProperties fallbackProperties
    ) {
        this.model = model;
        this.fallbackQuads = fallbackQuads;
        this.fallbackExtents = fallbackExtents;
        this.fallbackProperties = fallbackProperties;
    }

    public @Nullable QuadCollection getFallbackQuads() {
        return this.fallbackQuads;
    }

    public @Nullable Supplier<Vector3fc[]> getFallbackExtents() {
        return this.fallbackExtents;
    }

    public @Nullable ModelRenderProperties getFallbackProperties() {
        return this.fallbackProperties;
    }

    @Override
    @NonNull
    public EndCrystalRenderState extractArgument(@NonNull ItemStack stack) {
        EndCrystalRenderState state = new EndCrystalRenderState();

        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player != null) {
            state.ageInTicks = player.tickCount + mc.getDeltaTracker().getGameTimeDeltaPartialTick(false);
        }

        return state;
    }

    @Override
    public void submit(
        @NonNull EndCrystalRenderState state,
        @NonNull PoseStack poseStack,
        @NonNull SubmitNodeCollector collector,
        int light,
        int overlay,
        boolean hasFoil,
        int outlineColor
    ) {
        poseStack.pushPose();
        SpinMyCrystalsConfig config = SpinMyCrystalsConfig.get();
        poseStack.translate(0.5 + config.offsetX, 0.5 + config.offsetY, 0.5 + config.offsetZ);

        RenderType renderType = CrystalRenderTypes.get(TEXTURE, config.noShade, config.culled);

        float baseAnim = state.ageInTicks * 3.0F;

        Quaternionf outerRot = CrystalRotation.computeOuterRotation(baseAnim, config.outerGlassSpeed);
        poseStack.pushPose();
        poseStack.scale(config.outerGlass, config.outerGlass, config.outerGlass);
        poseStack.mulPose(outerRot);
        collector.submitModelPart(this.model.outerGlass, poseStack, renderType, light, overlay, null);
        poseStack.popPose();

        Quaternionf innerRot = CrystalRotation.computeInnerRotation(outerRot, baseAnim, config.innerGlassSpeed);
        poseStack.pushPose();
        float innerScale = 0.875F * config.innerGlass;
        poseStack.scale(innerScale, innerScale, innerScale);
        poseStack.mulPose(innerRot);
        collector.submitModelPart(this.model.innerGlass, poseStack, renderType, light, overlay, null);
        poseStack.popPose();

        Quaternionf coreRot = CrystalRotation.computeCoreRotation(innerRot, baseAnim, config.cubeSpeed);
        poseStack.pushPose();
        float cubeScale = 0.765625F * config.cube;
        poseStack.scale(cubeScale, cubeScale, cubeScale);
        poseStack.mulPose(coreRot);
        collector.submitModelPart(this.model.cube, poseStack, renderType, light, overlay, null);
        poseStack.popPose();

        poseStack.popPose();
    }

    @Override
    public void getExtents(@NonNull Consumer<Vector3fc> output) {
        PoseStack poseStack = new PoseStack();
        poseStack.translate(0.5F, 0.5F, 0.5F);
        this.model.outerGlass.getExtentsForGui(poseStack, output);
    }

    public record Unbaked() implements SpecialModelRenderer.Unbaked<EndCrystalRenderState> {
        public static final MapCodec<Unbaked> MAP_CODEC = MapCodec.unit(new Unbaked());

        @Override
        @NonNull
        public EndCrystalSpecialRenderer bake(SpecialModelRenderer.@NonNull BakingContext context) {
            QuadCollection fallbackQuads = null;
            Supplier<Vector3fc[]> fallbackExtents = null;
            ModelRenderProperties fallbackProperties = null;

            if (context instanceof net.minecraft.client.renderer.item.ItemModel.BakingContext itemContext) {
                try {
                    ModelBaker baker = itemContext.blockModelBaker();
                    ResolvedModel model = baker.getModel(Identifier.withDefaultNamespace("item/end_crystal"));
                    TextureSlots textureSlots = model.getTopTextureSlots();
                    fallbackQuads = model.bakeTopGeometry(textureSlots, baker, BlockModelRotation.IDENTITY);
                    fallbackProperties = ModelRenderProperties.fromResolvedModel(baker, model, textureSlots);
                    if (fallbackQuads != null) {
                        QuadCollection finalQuads = fallbackQuads;
                        fallbackExtents = Suppliers.memoize(() -> CuboidItemModelWrapper.computeExtents(finalQuads.getAll()));
                    }
                } catch (Exception ignored) {
                }
            }

            SpinningCrystalModel crystalModel = new SpinningCrystalModel(context.entityModelSet().bakeLayer(SpinMyCrystals.MODEL_LAYER));
            return new EndCrystalSpecialRenderer(crystalModel, fallbackQuads, fallbackExtents, fallbackProperties);
        }

        @Override
        @NonNull
        public MapCodec<Unbaked> type() {
            return MAP_CODEC;
        }
    }
}
