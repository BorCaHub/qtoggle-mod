package com.example.qtoggle;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
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

        // MC 26.1: use fromNamespaceAndPath (constructor is private, .of() tidak ada)
        KeyMapping.Category toggleCategory = KeyMapping.Category.register(
                Identifier.fromNamespaceAndPath("qtoggle", "main")
        );

        // PENTING: harus didaftarin lewat KeyBindingHelper, kalau cuma
        // "new KeyMapping(...)" doang, keybind-nya gak akan pernah ke-trigger
        // dan gak akan muncul di Options > Controls.
        toggleKey = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.qtoggle.toggle",       // translation key (en_us.json)
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_G,            // default key: G
                toggleCategory              // custom category in Controls screen
        ));

        ClientTickEvents.START_CLIENT_TICK.register(client -> {
            if (client.player == null) return;

            // Check toggle first in START tick (before the game processes input)
            while (toggleKey.consumeClick()) {
                dropEnabled = !dropEnabled;
                showStatus(client, dropEnabled);
            }

            // Lock Q in START tick before the game can process the drop
            if (!dropEnabled) {
                Options options = client.options;
                KeyMapping dropKey = options.keyDrop;

                // Force the key to be treated as not pressed at all
                dropKey.setDown(false);

                // Drain all pending clicks
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
