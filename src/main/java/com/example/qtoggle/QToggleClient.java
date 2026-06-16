package com.example.qtoggle;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.input.v1.FabricKeyBinding;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.KeyMapping;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

public class QToggleClient implements ClientModInitializer {

    public static boolean dropEnabled = false;

    @Override
    public void onInitializeClient() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null) return;

            if (!dropEnabled) {
                Options options = client.options;
                KeyMapping dropKey = options.keyDrop;
                if (dropKey.isDown()) {
                    while (dropKey.consumeClick()) { }
                }
            }
        });
    }
}
