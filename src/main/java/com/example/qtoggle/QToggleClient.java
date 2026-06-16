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
 * Q Toggle (Drop Lock)
 *
 * Perilaku:
 * - Default: tombol drop (Q) TIDAK melakukan apa-apa (dikunci / "off").
 * - Tekan keybind toggle (default: G) untuk menyalakan mode drop ("on").
 *   Saat "on", Q akan drop item seperti normal.
 * - Tekan lagi keybind toggle untuk mematikan kembali ("off").
 * - Status ditampilkan sebagai notifikasi singkat di action bar (judul di atas hotbar).
 *
 * PENTING — perubahan API di Fabric 26.1:
 * Fabric API 26.1 merename banyak class, termasuk:
 *   KeyBindingHelper (package keybinding.v1) -> KeyMappingHelper (package keymapping.v1)
 * Sistem kategori keybind vanilla JUGA berubah total di 26.1: tidak lagi
 * pakai String biasa, tapi object KeyBinding.Category yang dibuat lewat
 * KeyBinding.Category.create(ResourceLocation). Kode di bawah sudah
 * disesuaikan dengan ini per dokumentasi resmi Fabric per April 2026.
 *
 * Kalau nanti ternyata nama class KeyMapping vanilla berbeda lagi (Mojang
 * sempat menyebutnya bisa juga "KeyBinding" tergantung build), cek lewat
 * IDE autocomplete -> struktur logikanya (cek tiap tick, matikan status
 * key drop saat mode OFF) tidak perlu diubah, hanya nama class/method-nya.
 */
public class QToggleClient implements ClientModInitializer {

	// Status mode drop: false = terkunci (Q tidak drop), true = aktif (Q drop normal)
	public static boolean dropEnabled = false;

	// Keybind untuk toggle mode drop. Default key: G
	private static KeyMapping toggleKey;

	@Override
	public void onInitializeClient() {
		// Kategori keybind kustom kita, dibuat lewat sistem Category baru di 26.1
		KeyMapping.Category category = KeyMapping.Category.create(
				ResourceLocation.fromNamespaceAndPath("qtoggle", "main")
		);

		toggleKey = KeyMappingHelper.registerKeyBinding(new KeyMapping(
				"key.qtoggle.toggle",            // nama key (untuk file lang)
				InputConstants.Type.KEYSYM,
				InputConstants.KEY_G.getValue(), // default key: G, ganti sesuai keinginan di Options > Controls
				category
		));

		// Cek setiap tick: apakah keybind toggle baru saja ditekan?
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			while (toggleKey.consumeClick()) {
				dropEnabled = !dropEnabled;
				sendStatusMessage(client, dropEnabled);
			}

			// Inti logika: kalau mode OFF, paksa key.drop dianggap TIDAK ditekan
			// sebelum game sempat memprosesnya untuk drop item.
			if (!dropEnabled && client.player != null) {
				Options options = client.options;
				KeyMapping dropKey = options.keyDrop;

				if (dropKey.isDown()) {
					// "Matikan" status tombol drop secara paksa di level input,
					// supaya game tidak menganggapnya sebagai penekanan valid.
					dropKey.setDown(false);
					// Bersihkan juga antrian klik supaya tidak ada drop yang nyangkut.
					dropKey.consumeClick();
				}
			}
		});
	}

	private void sendStatusMessage(Minecraft client, boolean enabled) {
		if (client.player == null) return;
		String text = enabled
				? "§a[Q Toggle] Mode DROP: AKTIF (Q akan drop item)"
				: "§c[Q Toggle] Mode DROP: TERKUNCI (Q tidak drop)";
		client.player.displayClientMessage(Component.literal(text), true); // true = tampil di action bar
	}
}
