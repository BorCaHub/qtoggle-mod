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

        toggleKey = new KeyMapping(
                "key.qtoggle.toggle",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_G,
                KeyMapping.Category.MISC
        );

        ClientTickEvents.START_CLIENT_TICK.register(client -> {
            if (client.player == null) return;

            // Cek toggle dulu di START tick (sebelum game proses input)
            while (toggleKey.consumeClick()) {
                dropEnabled = !dropEnabled;
                tampilkanStatus(client, dropEnabled);
            }

            // Lock Q di START tick sebelum game sempat proses drop
            if (!dropEnabled) {
                Options options = client.options;
                KeyMapping dropKey = options.keyDrop;

                // Paksa key dianggap tidak ditekan sama sekali
                dropKey.setDown(false);

                // Drain semua click yang pending
                while (dropKey.consumeClick()) { }
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
