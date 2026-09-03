package dev.manere.spinmycrystals.gui;

import dev.manere.spinmycrystals.config.SpinMyCrystalsConfig;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Locale;
import java.util.function.Consumer;

public abstract class AbstractSpinMyCrystalsScreen extends Screen {
    private static final String[] CATEGORIES = {"Scales", "Speeds", "Position", "Shading"};

    protected final @Nullable Screen parent;
    private int currentCategory = 0;
    private Button resetAllBtn;

    protected AbstractSpinMyCrystalsScreen(@Nullable Screen parent) {
        super(Component.literal("SpinMyCrystals"));
        this.parent = parent;
    }

    private static @NonNull Component createHeaderComponent() {
        String mainText = "Spin My Crystals";
        MutableComponent root = Component.empty();

        int startRgb = 0xD494FF;
        int endRgb = 0xFFB3DE;

        int r1 = (startRgb >> 16) & 0xFF;
        int g1 = (startRgb >> 8) & 0xFF;
        int b1 = startRgb & 0xFF;

        int r2 = (endRgb >> 16) & 0xFF;
        int g2 = (endRgb >> 8) & 0xFF;
        int b2 = endRgb & 0xFF;

        int len = mainText.length();
        for (int i = 0; i < len; i++) {
            float t = (float) i / (float) (len - 1);
            int r = (int) (r1 + t * (r2 - r1));
            int g = (int) (g1 + t * (g2 - g1));
            int b = (int) (b1 + t * (b2 - b1));
            int rgb = (r << 16) | (g << 8) | b;
            root.append(Component.literal(String.valueOf(mainText.charAt(i)))
                .withStyle(s -> s.withColor(rgb).withBold(true)));
        }

        root.append(Component.literal(" by ").withStyle(s -> s.withColor(0xADADAD).withBold(false)));
        root.append(Component.literal("manere").withStyle(s -> s.withColor(0xCFCFCF).withBold(false)));
        return root;
    }

    private static <T extends AbstractWidget> @NonNull T withTooltip(@NonNull T widget, @NonNull String tooltip) {
        widget.setTooltip(Tooltip.create(Component.literal(tooltip)));
        return widget;
    }

    @Override
    protected void init() {
        this.clearWidgets();
        SpinMyCrystalsConfig config = SpinMyCrystalsConfig.get();

        int menuWidth = 470;
        int menuHeight = 220;
        int menuX = (this.width - menuWidth) / 2;
        int menuY = (this.height - menuHeight) / 2;

        int sideX = menuX + 14;
        int sideY = menuY + 36;
        int sideWidth = 100;

        Component headerText = createHeaderComponent();
        int headerWidth = this.font.width(headerText);
        int headerX = menuX + (menuWidth - headerWidth) / 2;
        StringWidget titleWidget = new StringWidget(headerX, menuY + 13, headerWidth + 4, 16, headerText, this.font);
        this.addRenderableWidget(titleWidget);

        for (int i = 0; i < CATEGORIES.length; i++) {
            final int catIndex = i;
            boolean active = (this.currentCategory == catIndex);
            Component btnText = active
                ? Component.literal("▶ " + CATEGORIES[i]).withStyle(s -> s.withColor(0xFFFFFF).withBold(true))
                : Component.literal(CATEGORIES[i]).withStyle(s -> s.withColor(0xCFCFCF));

            this.addRenderableWidget(
                Button.builder(btnText, btn -> {
                    this.currentCategory = catIndex;
                    this.init();
                }).bounds(sideX, sideY + i * 26, sideWidth, 20).build()
            );
        }

        this.addRenderableWidget(withTooltip(
            Button.builder(
                Component.literal(config.enabled ? "Disable Mod" : "Enable Mod")
                    .withStyle(s -> s.withColor(config.enabled ? 0xFF8888 : 0x88FF88)),
                btn -> {
                    config.enabled = !config.enabled;
                    this.init();
                }
            ).bounds(sideX, menuY + menuHeight - 52, sideWidth, 20).build(),
            config.enabled ? "Disables 3D crystal rendering and reverts to vanilla 2D items." : "Enables 3D crystal rendering."
        ));

        this.resetAllBtn = Button.builder(Component.literal("Reset All"), btn -> {
            config.resetAll();
            this.init();
        }).bounds(sideX, menuY + menuHeight - 28, sideWidth, 20).build();
        this.resetAllBtn.active = config.isModifiedFromDefaults();

        this.addRenderableWidget(withTooltip(
            this.resetAllBtn,
            "Reset all settings back to default."
        ));

        int contentX = sideX + sideWidth + 16;
        int contentWidth = menuWidth - (sideWidth + 40);
        int contentY = sideY + 4;
        int rowSpacing = 32;

        switch (this.currentCategory) {
            case 0 -> {
                int y = contentY;
                addNumericRow("Outer Glass Scale", contentX, y, config.outerGlass, 0.8F, 0.0F, 3.0F, 0.05F,
                    v -> config.outerGlass = v, "Changes the size of the outer glass frame.");

                y += rowSpacing;
                addNumericRow("Inner Glass Scale", contentX, y, config.innerGlass, 0.8F, 0.0F, 3.0F, 0.05F,
                    v -> config.innerGlass = v, "Changes the size of the inner glass frame.");

                y += rowSpacing;
                addNumericRow("Core Scale", contentX, y, config.cube, 0.8F, 0.0F, 3.0F, 0.05F,
                    v -> config.cube = v, "Changes the size of the crystal core inside.");
            }
            case 1 -> {
                int y = contentY;
                addNumericRow("Outer Glass Speed", contentX, y, config.outerGlassSpeed, 0.8F, -5.0F, 5.0F, 0.01F,
                    v -> config.outerGlassSpeed = v, "How fast the outer glass frame spins. Negative values spin backwards.");

                y += rowSpacing;
                addNumericRow("Inner Glass Speed", contentX, y, config.innerGlassSpeed, 0.8F, -5.0F, 5.0F, 0.01F,
                    v -> config.innerGlassSpeed = v, "How fast the inner glass frame spins. Negative values spin backwards.");

                y += rowSpacing;
                addNumericRow("Core Speed", contentX, y, config.cubeSpeed, 0.8F, -5.0F, 5.0F, 0.01F,
                    v -> config.cubeSpeed = v, "How fast the crystal core spins. Negative values spin backwards.");
            }
            case 2 -> {
                int y = contentY;
                addNumericRow("Position Offset X", contentX, y, config.offsetX, 0.02F, -1.0F, 1.0F, 0.01F,
                    v -> config.offsetX = v, "Moves the crystal left or right in your hand.");

                y += rowSpacing;
                addNumericRow("Position Offset Y", contentX, y, config.offsetY, -0.02F, -1.0F, 1.0F, 0.01F,
                    v -> config.offsetY = v, "Moves the crystal up or down in your hand.");

                y += rowSpacing;
                addNumericRow("Position Offset Z", contentX, y, config.offsetZ, 0.0F, -1.0F, 1.0F, 0.01F,
                    v -> config.offsetZ = v, "Moves the crystal closer or further away in your hand.");
            }
            case 3 -> {
                int y = contentY;
                int btnW = contentWidth;

                boolean shadingOn = !config.noShade;
                Component shadingText = Component.literal("Shading: ").withStyle(s -> s.withColor(0xFFFFFF).withBold(false))
                    .append(Component.literal(shadingOn ? "On" : "Off").withStyle(s -> s.withColor(shadingOn ? 0x88FF88 : 0xFF8888).withBold(false)));

                this.addRenderableWidget(withTooltip(
                    Button.builder(
                        shadingText,
                        btn -> {
                            config.noShade = !config.noShade;
                            this.init();
                        }
                    ).bounds(contentX, y, btnW, 20).build(),
                    "Removes dark face shadows on the crystal without making it fullbright."
                ));

                y += rowSpacing;
                boolean cullingOn = config.culled;
                Component cullingText = Component.literal("Culling: ").withStyle(s -> s.withColor(0xFFFFFF).withBold(false))
                    .append(Component.literal(cullingOn ? "On" : "Off").withStyle(s -> s.withColor(cullingOn ? 0x88FF88 : 0xFF8888).withBold(false)));

                this.addRenderableWidget(withTooltip(
                    Button.builder(
                        cullingText,
                        btn -> {
                            config.culled = !config.culled;
                            this.init();
                        }
                    ).bounds(contentX, y, btnW, 20).build(),
                    "Culls inner hidden faces of the crystal to look cleaner."
                ));
            }
        }

        int doneWidth = 96;
        this.addRenderableWidget(withTooltip(
            Button.builder(Component.literal("Save & Close"), btn -> this.onClose())
                .bounds(menuX + menuWidth - doneWidth - 14, menuY + menuHeight - 28, doneWidth, 20)
                .build(),
            "Save changes and close."
        ));
    }

    private void addNumericRow(
        @NonNull String label,
        int x,
        int y,
        float currentVal,
        float defaultVal,
        float min,
        float max,
        float step,
        @NonNull Consumer<Float> onApply,
        @NonNull String tooltip
    ) {
        int labelWidth = 115;
        int sliderWidth = 135;
        int inputWidth = 46;
        int resetWidth = 20;

        StringWidget labelWidget = new StringWidget(x, y, labelWidth, 20, Component.literal(label), this.font);
        this.addRenderableWidget(labelWidget);

        int controlsStartX = x + labelWidth + 4;
        boolean[] updating = new boolean[]{false};

        FloatSliderWidget slider = new FloatSliderWidget(
            controlsStartX,
            y,
            sliderWidth,
            20,
            "",
            currentVal,
            min,
            max,
            step,
            null
        );

        EditBox box = new EditBox(this.font, controlsStartX + sliderWidth + 4, y, inputWidth, 20, Component.literal(label));
        box.setValue(String.format(Locale.ROOT, "%.2f", currentVal));

        Button[] resetBtnHolder = new Button[1];

        Runnable updateResetState = () -> {
            if (resetBtnHolder[0] != null) {
                float current = slider.getFloatValue();
                resetBtnHolder[0].active = Math.abs(current - defaultVal) > 0.001F;
            }
            if (this.resetAllBtn != null) {
                this.resetAllBtn.active = SpinMyCrystalsConfig.get().isModifiedFromDefaults();
            }
        };

        slider.setOnApply(val -> {
            if (!updating[0]) {
                updating[0] = true;
                box.setValue(String.format(Locale.ROOT, "%.2f", val));
                onApply.accept(val);
                updating[0] = false;
                updateResetState.run();
            }
        });

        box.setResponder(text -> {
            if (!updating[0]) {
                try {
                    float val = Float.parseFloat(text.trim());
                    updating[0] = true;
                    slider.setFloatValue(val, false);
                    onApply.accept(val);
                    updating[0] = false;
                    updateResetState.run();
                } catch (NumberFormatException ignored) {
                }
            }
        });

        Button resetBtn = Button.builder(Component.literal("↺"), btn -> {
            updating[0] = true;
            slider.setFloatValue(defaultVal, false);
            box.setValue(String.format(Locale.ROOT, "%.2f", defaultVal));
            onApply.accept(defaultVal);
            updating[0] = false;
            updateResetState.run();
        }).bounds(controlsStartX + sliderWidth + 4 + inputWidth + 4, y, resetWidth, 20).build();

        resetBtnHolder[0] = resetBtn;
        resetBtn.active = Math.abs(currentVal - defaultVal) > 0.001F;

        withTooltip(slider, tooltip);
        withTooltip(box, "Click to type an exact number.");
        withTooltip(resetBtn, "Reset to default (" + String.format(Locale.ROOT, "%.2f", defaultVal) + ")");

        this.addRenderableWidget(slider);
        this.addRenderableWidget(box);
        this.addRenderableWidget(resetBtn);
    }

    @Override
    public void onClose() {
        SpinMyCrystalsConfig.save();
        if (this.minecraft != null) {
            navigateBack();
        }
    }

    protected abstract void navigateBack();
}
