package com.example.qtoggle;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.network.chat.Component;

/**
 * Q Toggle (Drop Lock) — Client Mod Initializer
 *
 * Perilaku:
 * - Default: tombol drop (Q) TIDAK melakukan apa-apa (terkunci / mode OFF).
 * - Tekan keybind toggle (default: G) untuk menyalakan mode drop (ON).
 *   Saat ON, Q akan drop item seperti normal.
 * - Tekan lagi keybind toggle untuk mematikan kembali (OFF).
 * - Status ditampilkan sebagai notifikasi singkat di action bar (atas hotbar).
 */
public class QToggleClient implements ClientModInitializer {

    // Status mode drop: false = terkunci (Q tidak drop), true = aktif (Q drop normal)
    public static boolean dropEnabled = false;

    // Keybind untuk toggle mode drop (default: G)
    private static KeyMapping toggleKey;

    @Override
    public void onInitializeClient() {

        // MC 26.1 / Fabric API 26.1:
        // - KeyBindingHelper renamed to KeyMappingHelper (keymapping.v1)
        // - registerKeyBinding renamed to registerKeyMapping
        // - Category is now a plain String (no ResourceLocation needed)
        toggleKey = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "key.qtoggle.toggle",              // ID (dipakai oleh file lang)
                InputConstants.Type.KEYSYM,
                InputConstants.GLFW_KEY_G,          // int constant langsung, bukan .KEY_G.getValue()
                "key.categories.qtoggle.main"       // Kategori sebagai String
        ));

        // Hook ke event tick client — cek input setiap tick
        ClientTickEvents.END_CLIENT_TICK.register(client -> {

            // --- 1. Cek apakah keybind toggle baru saja ditekan ---
            while (toggleKey.consumeClick()) {
                dropEnabled = !dropEnabled;
                tampilkanStatus(client, dropEnabled);
            }

            // --- 2. Inti logika drop lock ---
            // Kalau mode OFF dan ada pemain aktif, paksa key.drop dianggap tidak ditekan
            if (!dropEnabled && client.player != null) {
                Options options = client.options;
                KeyMapping dropKey = options.keyDrop;

                if (dropKey.isDown()) {
                    // Matikan status tombol drop di level input
                    KeyMapping.set(dropKey.getKey(), false);
                    // Bersihkan antrian klik agar tidak ada drop yang "nyangkut"
                    dropKey.consumeClick();
                }
            }
        });
    }

    /**
     * Tampilkan pesan status ON/OFF di action bar (baris di atas hotbar).
     * true = tampil di action bar, bukan chat
     */
    private void tampilkanStatus(Minecraft client, boolean aktif) {
        if (client.player == null) return;

        String teks = aktif
                ? "§a[Q Toggle] Mode DROP: AKTIF — Q akan drop item"
                : "§c[Q Toggle] Mode DROP: TERKUNCI — Q tidak drop";

        client.player.sendSystemMessage(Component.literal(teks), true);
    }
}
