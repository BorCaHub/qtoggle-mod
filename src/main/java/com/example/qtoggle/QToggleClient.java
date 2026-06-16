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

        // Buat category dulu — MC 26.1 pakai KeyMapping.Category.create(String)
        KeyMapping.Category category = KeyMapping.Category.create("key.categories.qtoggle.main");

        // Constructor KeyMapping dengan Category object
        toggleKey = new KeyMapping(
                "key.qtoggle.toggle",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_G,
                category
        );

        ClientTickEvents.END_CLIENT_TICK.register(client -> {

            while (toggleKey.consumeClick()) {
                dropEnabled = !dropEnabled;
                tampilkanStatus(client, dropEnabled);
            }

            if (!dropEnabled && client.player != null) {
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
