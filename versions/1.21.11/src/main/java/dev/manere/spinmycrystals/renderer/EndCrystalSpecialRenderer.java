package dev.manere.spinmycrystals.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.MapCodec;
import dev.manere.spinmycrystals.SpinMyCrystals;
import dev.manere.spinmycrystals.config.SpinMyCrystalsConfig;
import dev.manere.spinmycrystals.math.CrystalRotation;
import dev.manere.spinmycrystals.model.SpinningCrystalModel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.state.EndCrystalRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.joml.Quaternionf;
import org.joml.Vector3fc;
import org.jspecify.annotations.NonNull;

import java.util.function.Consumer;

public class EndCrystalSpecialRenderer implements SpecialModelRenderer<EndCrystalRenderState> {
    public static final Identifier TEXTURE = Identifier.withDefaultNamespace("textures/entity/end_crystal/end_crystal.png");

    private final SpinningCrystalModel model;

    public EndCrystalSpecialRenderer(@NonNull SpinningCrystalModel model) {
        this.model = model;
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
        @NonNull ItemDisplayContext displayContext,
        @NonNull PoseStack poseStack,
        @NonNull SubmitNodeCollector collector,
        int light,
        int overlay,
        boolean hasFoil,
        int seed
    ) {
        SpinMyCrystalsConfig config = SpinMyCrystalsConfig.get();
        if (!config.enabled) return;

        poseStack.pushPose();
        poseStack.translate(0.5 + config.offsetX, 0.5 + config.offsetY, 0.5 + config.offsetZ);

        RenderType renderType = config.culled
            ? RenderTypes.itemEntityTranslucentCull(TEXTURE)
            : RenderTypes.entityTranslucent(TEXTURE);

        int effectiveLight = config.noShade ? 15728880 : light;

        float baseAnim = state.ageInTicks * 3.0F;

        Quaternionf outerRot = CrystalRotation.computeOuterRotation(baseAnim, config.outerGlassSpeed);
        poseStack.pushPose();
        poseStack.scale(config.outerGlass, config.outerGlass, config.outerGlass);
        poseStack.mulPose(outerRot);
        collector.submitModelPart(this.model.outerGlass, poseStack, renderType, effectiveLight, overlay, null);
        poseStack.popPose();

        Quaternionf innerRot = CrystalRotation.computeInnerRotation(outerRot, baseAnim, config.innerGlassSpeed);
        poseStack.pushPose();
        float innerScale = 0.875F * config.innerGlass;
        poseStack.scale(innerScale, innerScale, innerScale);
        poseStack.mulPose(innerRot);
        collector.submitModelPart(this.model.innerGlass, poseStack, renderType, effectiveLight, overlay, null);
        poseStack.popPose();

        Quaternionf coreRot = CrystalRotation.computeCoreRotation(innerRot, baseAnim, config.cubeSpeed);
        poseStack.pushPose();
        float cubeScale = 0.765625F * config.cube;
        poseStack.scale(cubeScale, cubeScale, cubeScale);
        poseStack.mulPose(coreRot);
        collector.submitModelPart(this.model.cube, poseStack, renderType, effectiveLight, overlay, null);
        poseStack.popPose();

        poseStack.popPose();
    }

    @Override
    public void getExtents(@NonNull Consumer<Vector3fc> output) {
        PoseStack poseStack = new PoseStack();
        poseStack.translate(0.5F, 0.5F, 0.5F);
        this.model.outerGlass.getExtentsForGui(poseStack, output);
    }

    public record Unbaked() implements SpecialModelRenderer.Unbaked {
        public static final MapCodec<Unbaked> MAP_CODEC = MapCodec.unit(new Unbaked());

        @Override
        @NonNull
        public SpecialModelRenderer<?> bake(SpecialModelRenderer.@NonNull BakingContext context) {
            SpinningCrystalModel crystalModel = new SpinningCrystalModel(context.entityModelSet().bakeLayer(SpinMyCrystals.MODEL_LAYER));
            return new EndCrystalSpecialRenderer(crystalModel);
        }

        @Override
        @NonNull
        public MapCodec<Unbaked> type() {
            return MAP_CODEC;
        }
    }
}
