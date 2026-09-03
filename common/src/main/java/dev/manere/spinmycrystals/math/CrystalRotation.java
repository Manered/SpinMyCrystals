package dev.manere.spinmycrystals.math;

import org.joml.Quaternionf;
import org.jspecify.annotations.NonNull;

public final class CrystalRotation {
    private static final float RAD_35 = (float) Math.toRadians(35.0);
    private static final float RAD_45 = (float) Math.toRadians(45.0);

    private CrystalRotation() {
    }

    @NonNull
    public static Quaternionf createLayerRotation(float angleDeg) {
        float radY = (float) Math.toRadians(angleDeg);

        Quaternionf qY = new Quaternionf().rotationY(radY);
        Quaternionf qZ = new Quaternionf().rotationZ(RAD_35);
        Quaternionf qX = new Quaternionf().rotationX(RAD_45);

        return qY.mul(qZ).mul(qX);
    }

    @NonNull
    public static Quaternionf computeOuterRotation(float baseAnim, float speed) {
        return createLayerRotation(baseAnim * speed);
    }

    @NonNull
    public static Quaternionf computeInnerRotation(@NonNull Quaternionf outerRotation, float baseAnim, float speed) {
        return new Quaternionf(outerRotation).mul(createLayerRotation(baseAnim * speed));
    }

    @NonNull
    public static Quaternionf computeCoreRotation(@NonNull Quaternionf innerRotation, float baseAnim, float speed) {
        return new Quaternionf(innerRotation).mul(createLayerRotation(baseAnim * speed));
    }
}
