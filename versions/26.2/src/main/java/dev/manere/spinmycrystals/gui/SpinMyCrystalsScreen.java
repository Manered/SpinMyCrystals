package dev.manere.spinmycrystals.gui;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public class SpinMyCrystalsScreen extends AbstractSpinMyCrystalsScreen {
    public SpinMyCrystalsScreen(@Nullable Screen parent) {
        super(parent);
    }

    @Override
    public void extractRenderState(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        if (this.minecraft != null && this.minecraft.level != null) {
            graphics.fill(0, 0, this.width, this.height, 0x88000000);
        } else {
            this.extractMenuBackground(graphics);
        }

        int menuWidth = 470;
        int menuHeight = 220;
        int menuX = (this.width - menuWidth) / 2;
        int menuY = (this.height - menuHeight) / 2;

        int sideX = menuX + 14;
        int sideWidth = 100;

        graphics.fill(menuX, menuY, menuX + menuWidth, menuY + menuHeight, 0xD0101014);
        graphics.outline(menuX, menuY, menuWidth, menuHeight, 0x44FFFFFF);

        int dividerX = sideX + sideWidth + 8;
        graphics.fill(dividerX, menuY + 34, dividerX + 1, menuY + menuHeight - 12, 0x33FFFFFF);

        super.extractRenderState(graphics, mouseX, mouseY, a);
    }

    @Override
    protected void navigateBack() {
        if (this.minecraft != null) {
            this.minecraft.gui.setScreen(this.parent);
        }
    }
}
