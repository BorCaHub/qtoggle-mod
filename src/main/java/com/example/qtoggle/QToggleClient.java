package com.example.qtoggle;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

/**
 * Q Toggle (Drop Lock) — Client Mod Initializer
 *
 * Perilaku:
 * - Default: tombol drop (Q) TIDAK melakukan apa-apa (terkunci / mode OFF).
 * - Tekan keybind toggle (default: G) untuk menyalakan mode drop (ON).
 *   Saat ON, Q akan drop item seperti normal.
 * - Tekan lagi keybind toggle untuk mematikan kembali (OFF).
 * - Status ditampilkan sebagai notifikasi singkat di action bar (atas hotbar).
 *
 * Catatan API Fabric 26.1:
 * - KeyBindingHelper (keybinding.v1) sudah direname jadi KeyMappingHelper (keymapping.v1)
 * - Kategori keybind vanilla tidak lagi pakai String biasa, tapi KeyMapping.Category
 *   yang dibuat lewat KeyMapping.Category.create(ResourceLocation)
 *
 * Kalau ada error nama method/field di versi 26.1 yang lebih baru, buka dengan
 * IntelliJ IDEA + plugin Minecraft Development dan pakai autocomplete untuk
 * menemukan nama yang benar. Struktur logikanya tidak perlu diubah.
 */
public class QToggleClient implements ClientModInitializer {

    // Status mode drop: false = terkunci (Q tidak drop), true = aktif (Q drop normal)
    public static boolean dropEnabled = false;

    // Keybind untuk toggle mode drop (default: G)
    private static KeyMapping toggleKey;

    @Override
    public void onInitializeClient() {

        // Buat kategori keybind kustom lewat sistem Category baru di Fabric 26.1
        KeyMapping.Category category = KeyMapping.Category.create(
                ResourceLocation.fromNamespaceAndPath("qtoggle", "main")
        );

        // Daftarkan keybind toggle ke sistem Fabric
        toggleKey = KeyMappingHelper.registerKeyBinding(new KeyMapping(
                "key.qtoggle.toggle",              // ID (dipakai oleh file lang)
                InputConstants.Type.KEYSYM,
                InputConstants.KEY_G.getValue(),   // Default key: G
                category
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
            // sebelum game sempat memprosesnya untuk drop item.
            if (!dropEnabled && client.player != null) {
                Options options = client.options;
                KeyMapping dropKey = options.keyDrop;

                if (dropKey.isDown()) {
                    // Matikan status tombol drop di level input
                    dropKey.setDown(false);
                    // Bersihkan antrian klik agar tidak ada drop yang "nyangkut"
                    dropKey.consumeClick();
                }
            }
        });
    }

    /**
     * Tampilkan pesan status ON/OFF di action bar (baris di atas hotbar).
     * Gunakan §a (hijau) untuk ON dan §c (merah) untuk OFF agar mudah dibedakan.
     */
    private void tampilkanStatus(Minecraft client, boolean aktif) {
        if (client.player == null) return;

        String teks = aktif
                ? "§a[Q Toggle] Mode DROP: AKTIF — Q akan drop item"
                : "§c[Q Toggle] Mode DROP: TERKUNCI — Q tidak drop";

        // Parameter kedua 'true' = tampil di action bar, bukan chat
        client.player.displayClientMessage(Component.literal(teks), true);
    }
}
