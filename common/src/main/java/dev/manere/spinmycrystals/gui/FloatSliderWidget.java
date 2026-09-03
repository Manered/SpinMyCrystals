package dev.manere.spinmycrystals.gui;

import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Locale;
import java.util.function.Consumer;

public class FloatSliderWidget extends AbstractSliderButton {
    private final @NonNull String prefix;
    private final float min;
    private final float max;
    private final float step;
    private @Nullable Consumer<Float> onApply;

    public void setOnApply(@Nullable Consumer<Float> onApply) {
        this.onApply = onApply;
    }

    public FloatSliderWidget(
        int x,
        int y,
        int width,
        int height,
        @NonNull String prefix,
        float current,
        float min,
        float max,
        float step,
        @Nullable Consumer<Float> onApply
    ) {
        super(x, y, width, height, Component.empty(), toNormalized(current, min, max));
        this.prefix = prefix;
        this.min = min;
        this.max = max;
        this.step = step;
        this.onApply = onApply;
        this.updateMessage();
    }

    private static double toNormalized(float value, float min, float max) {
        if (max == min) return 0.0;
        return Mth.clamp((value - min) / (max - min), 0.0, 1.0);
    }

    public float getFloatValue() {
        float raw = (float) (this.min + this.value * (this.max - this.min));
        if (this.step > 0.0F) {
            raw = Math.round(raw / this.step) * this.step;
        }
        return Math.round(raw * 100.0F) / 100.0F;
    }

    public void setFloatValue(float val) {
        setFloatValue(val, true);
    }

    public void setFloatValue(float val, boolean triggerCallback) {
        this.value = toNormalized(val, this.min, this.max);
        this.updateMessage();
        if (triggerCallback) {
            applyValue();
        }
    }

    @Override
    protected void updateMessage() {
        if (this.prefix.isEmpty()) {
            this.setMessage(Component.literal(String.format(Locale.ROOT, "%.2f", this.getFloatValue())));
        } else {
            this.setMessage(Component.literal(this.prefix + ": " + String.format(Locale.ROOT, "%.2f", this.getFloatValue())));
        }
    }

    @Override
    protected void applyValue() {
        if (this.onApply != null) {
            this.onApply.accept(this.getFloatValue());
        }
    }
}
