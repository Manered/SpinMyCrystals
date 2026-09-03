package dev.manere.spinmycrystals.renderer;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.entity.state.EndCrystalRenderState;

public class SpinningCrystalModel extends EntityModel<EndCrystalRenderState> {
    public final ModelPart base;
    public final ModelPart outerGlass;
    public final ModelPart innerGlass;
    public final ModelPart cube;

    public SpinningCrystalModel(ModelPart root) {
        super(root);
        this.base = root.getChild("base");
        this.outerGlass = root.getChild("outer_glass");
        this.innerGlass = root.getChild("inner_glass");
        this.cube = root.getChild("cube");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        CubeListBuilder glassCube = CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -4.0F, -4.0F, 8.0F, 8.0F, 8.0F);

        root.addOrReplaceChild("outer_glass", glassCube, PartPose.ZERO);
        root.addOrReplaceChild("inner_glass", glassCube, PartPose.ZERO);
        root.addOrReplaceChild(
            "cube",
            CubeListBuilder.create().texOffs(32, 0).addBox(-4.0F, -4.0F, -4.0F, 8.0F, 8.0F, 8.0F),
            PartPose.ZERO
        );
        root.addOrReplaceChild("base", CubeListBuilder.create().texOffs(0, 16).addBox(-6.0F, 0.0F, -6.0F, 12.0F, 4.0F, 12.0F), PartPose.ZERO);

        return LayerDefinition.create(mesh, 64, 32);
    }
}
