package com.example.qtoggle;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.KeyMapping;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

public class QToggleClient implements ClientModInitializer {

    public static boolean dropEnabled = false;
    private static final int TOGGLE_KEY = GLFW.GLFW_KEY_G;
    private static boolean wasPressed = false;

    @Override
    public void onInitializeClient() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null) return;

            // MC 26.1: getWindow() diganti jadi handle() atau windowHandle()
            long window = Minecraft.getInstance().getWindow().handle;

            boolean isPressed = GLFW.glfwGetKey(window, TOGGLE_KEY) == GLFW.GLFW_PRESS;

            if (isPressed && !wasPressed) {
                dropEnabled = !dropEnabled;
                tampilkanStatus(client, dropEnabled);
            }
            wasPressed = isPressed;

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
