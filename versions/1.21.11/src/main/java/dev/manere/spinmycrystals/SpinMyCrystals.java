package dev.manere.spinmycrystals;

import dev.manere.spinmycrystals.command.SpinMyCrystalsCommand;
import dev.manere.spinmycrystals.config.SpinMyCrystalsConfig;
import dev.manere.spinmycrystals.model.SpinningCrystalModel;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SpinMyCrystals implements ClientModInitializer {
    public static final String MOD_ID = "spinmycrystals";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static final ModelLayerLocation MODEL_LAYER = new ModelLayerLocation(id("item/end_crystal"), "main");

    public static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(MOD_ID, path);
    }

    @Override
    public void onInitializeClient() {
        SpinMyCrystalsConfig.load();

        EntityModelLayerRegistry.registerModelLayer(MODEL_LAYER, SpinningCrystalModel::createBodyLayer);

        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            SpinMyCrystalsCommand.register(dispatcher);
        });
    }
}
