package com.example.qtoggle;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.lwjgl.glfw.GLFW;

public class QToggleClient implements ClientModInitializer {

    public static boolean dropEnabled = false;
    private static KeyMapping toggleKey;

    @Override
    public void onInitializeClient() {

        KeyMapping.Category toggleCategory = KeyMapping.Category.register(
                Identifier.fromNamespaceAndPath("qtoggle", "main")
        );

        // MC 26.1: KeyBindingHelper sudah di-rename jadi KeyMappingHelper
        // (lihat docs.fabricmc.net/develop/porting/fabric-api).
        // Tanpa ini, keybind tidak akan muncul di Options > Controls.
        toggleKey = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "key.qtoggle.toggle",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_G,
                toggleCategory
        ));

        ClientTickEvents.START_CLIENT_TICK.register(client -> {
            if (client.player == null) return;

            while (toggleKey.consumeClick()) {
                dropEnabled = !dropEnabled;
                showStatus(client, dropEnabled);
            }

            if (!dropEnabled) {
                Options options = client.options;
                KeyMapping dropKey = options.keyDrop;
                dropKey.setDown(false);
                while (dropKey.consumeClick()) { }
            }
        });
    }

    private void showStatus(Minecraft client, boolean active) {
        if (client.player == null) return;
        String message = active
                ? "§a[Q Toggle] DROP: ENABLED — Q drops items"
                : "§c[Q Toggle] DROP: LOCKED — Q will not drop";
        client.gui.setOverlayMessage(Component.literal(message), false);
    }
}
