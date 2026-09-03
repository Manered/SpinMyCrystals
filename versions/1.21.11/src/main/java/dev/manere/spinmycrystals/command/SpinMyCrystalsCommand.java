package dev.manere.spinmycrystals.command;

import dev.manere.spinmycrystals.gui.SpinMyCrystalsScreen;
import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.Minecraft;

public class SpinMyCrystalsCommand {
    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(ClientCommandManager.literal("spinmycrystals")
            .executes(ctx -> openMenu(ctx.getSource())));
    }

    private static int openMenu(FabricClientCommandSource source) {
        Minecraft mc = Minecraft.getInstance();
        mc.schedule(() -> mc.setScreen(new SpinMyCrystalsScreen(null)));
        return 1;
    }
}



