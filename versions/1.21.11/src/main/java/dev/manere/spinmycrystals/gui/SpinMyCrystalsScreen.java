package dev.manere.spinmycrystals.gui;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public class SpinMyCrystalsScreen extends AbstractSpinMyCrystalsScreen {
    public SpinMyCrystalsScreen(@Nullable Screen parent) {
        super(parent);
    }

    @Override
    public void render(@NonNull GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        if (this.minecraft != null && this.minecraft.level != null) {
            graphics.fill(0, 0, this.width, this.height, 0x88000000);
        } else {
            this.renderBackground(graphics, mouseX, mouseY, delta);
        }

        int menuWidth = 470;
        int menuHeight = 220;
        int menuX = (this.width - menuWidth) / 2;
        int menuY = (this.height - menuHeight) / 2;

        int sideX = menuX + 14;
        int sideWidth = 100;

        graphics.fill(menuX, menuY, menuX + menuWidth, menuY + menuHeight, 0xD0101014);
        graphics.renderOutline(menuX, menuY, menuWidth, menuHeight, 0x44FFFFFF);

        int dividerX = sideX + sideWidth + 8;
        graphics.fill(dividerX, menuY + 34, dividerX + 1, menuY + menuHeight - 12, 0x33FFFFFF);

        super.render(graphics, mouseX, mouseY, delta);
    }

    @Override
    protected void navigateBack() {
        if (this.minecraft != null) {
            this.minecraft.setScreen(this.parent);
        }
    }
}
