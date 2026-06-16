package com.example.qtoggle;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

public class QToggleClient implements ClientModInitializer {

    public static boolean dropEnabled = false;
    private static KeyMapping toggleKey;

    @Override
    public void onInitializeClient() {

        // Pakai KeyMapping.Category.MISC yang sudah ada — tidak perlu bikin category baru
        toggleKey = new KeyMapping(
                "key.qtoggle.toggle",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_G,
                KeyMapping.Category.MISC
        );

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null) return;

            while (toggleKey.consumeClick()) {
                dropEnabled = !dropEnabled;
                tampilkanStatus(client, dropEnabled);
            }

            if (!dropEnabled) {
                Options options = client.options;
                KeyMapping dropKey = options.keyDrop;
                if (dropKey.isDown()) {
                    while (dropKey.consumeClick()) { }
                }
            }
        });
    }

    private void tampilkanStatus(Minecraft client, boolean aktif) {
        if (client.player == null) return;
        String teks = aktif
                ? "§a[Q Toggle] DROP: AKTIF — Q drop item"
                : "§c[Q Toggle] DROP: TERKUNCI — Q tidak drop";
        client.gui.setOverlayMessage(Component.literal(teks), false);
    }
}
