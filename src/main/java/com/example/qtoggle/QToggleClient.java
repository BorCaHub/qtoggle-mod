package com.example.qtoggle;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Map;

public class QToggleClient implements ClientModInitializer {

    public static boolean dropEnabled = false;
    private static KeyMapping toggleKey;

    @Override
    public void onInitializeClient() {
        try {
            // Ambil Category class
            Class<?> categoryClass = Class.forName("net.minecraft.client.KeyMapping$Category");

            // Ambil semua Category yang udah ada (MOVEMENT, GAMEPLAY, dll) lewat field
            // Kita pakai salah satu yang ada daripada bikin baru
            Field[] categoryFields = categoryClass.getDeclaredFields();
            Object miscCategory = null;
            for (Field f : categoryFields) {
                f.setAccessible(true);
                if (f.getType() == categoryClass) {
                    miscCategory = f.get(null);
                    System.out.println("[QToggle] Pakai category: " + f.getName());
                    break;
                }
            }

            if (miscCategory == null) {
                System.out.println("[QToggle] Tidak ada category, pakai constructor langsung");
                // Coba constructor Category(String, int)
                Constructor<?> catCtor = categoryClass.getDeclaredConstructors()[0];
                catCtor.setAccessible(true);
                // Cek berapa param constructor-nya
                int paramCount = catCtor.getParameterCount();
                System.out.println("[QToggle] Category constructor params: " + paramCount);
                if (paramCount == 2) {
                    miscCategory = catCtor.newInstance("key.categories.qtoggle.main", 9999);
                } else if (paramCount == 1) {
                    miscCategory = catCtor.newInstance("key.categories.qtoggle.main");
                }
            }

            // Buat KeyMapping pakai constructor (String, Type, int, Category)
            toggleKey = new KeyMapping(
                    "key.qtoggle.toggle",
                    InputConstants.Type.KEYSYM,
                    GLFW.GLFW_KEY_G,
                    (KeyMapping.Category) miscCategory
            );

            // Daftarkan ke ALL lewat reflection
            Field allField = KeyMapping.class.getDeclaredField("ALL");
            allField.setAccessible(true);
            @SuppressWarnings("unchecked")
            Map<String, KeyMapping> all = (Map<String, KeyMapping>) allField.get(null);
            all.put("key.qtoggle.toggle", toggleKey);

            System.out.println("[QToggle] KeyMapping berhasil didaftarkan!");

        } catch (Exception e) {
            System.err.println("[QToggle] Gagal daftarkan keybind: " + e);
            e.printStackTrace();
            toggleKey = null;
        }

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null) return;

            // Toggle via consumeClick kalau keybind berhasil didaftarkan
            if (toggleKey != null) {
                while (toggleKey.consumeClick()) {
                    dropEnabled = !dropEnabled;
                    tampilkanStatus(client, dropEnabled);
                }
            }

            // Lock Q kalau mode OFF
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
