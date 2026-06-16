# Q Toggle (Drop Lock) — Mod Fabric untuk Minecraft 26.1

## Apa fungsinya
- Tombol **Q** secara default **TIDAK** akan drop item (aman dari drop tidak sengaja saat PvP).
- Ada **keybind toggle** (default: **G**) untuk menyalakan/mematikan mode drop:
  - **OFF (default)** → Q dikunci, tidak drop apapun.
  - **ON** → Q berfungsi normal seperti vanilla (drop item).
- Status ON/OFF ditampilkan sebagai teks singkat di action bar (atas hotbar) setiap kali toggle ditekan.
- Keybind toggle bisa diganti bebas di **Options > Controls > Q Toggle**.

## Kenapa saya tidak bisa kirim file .jar langsung
Lingkungan saya tidak punya Java 25, Gradle, maupun akses ke Maven/Fabric
repository (dibatasi jaringan), jadi saya tidak bisa benar-benar
mengompilasi project ini. Yang saya berikan adalah **source code lengkap**
yang sudah disusun sesuai struktur Fabric untuk Minecraft 26.1 (Mojang
mappings, Loom 1.15, tanpa remap). Kamu tinggal compile sendiri di
komputermu.

## Yang kamu butuhkan untuk compile
1. **JDK 25** terinstall (karena 26.1 butuh Java 25).
2. **Git** (opsional, untuk gradlew wrapper) — atau download Gradle wrapper jar manual.
3. Koneksi internet ke `maven.fabricmc.net` dan Maven Central (untuk dependency).

## Langkah compile
1. Cek versi **Fabric API** yang resmi mendukung Minecraft 26.1 di
   https://modrinth.com/mod/fabric-api/versions — catat nomor versinya.
2. Buka `gradle.properties`, ganti baris:
   ```
   fabric_version=CEK_VERSI_TERBARU_DI_MODRINTH
   ```
   dengan nomor versi yang kamu temukan (contoh format: `0.110.0+1.26.1`).
3. Di folder project, jalankan:
   ```
   gradle wrapper --gradle-version 9.4
   ```
   (kalau belum punya gradlew). Atau langsung pakai Gradle yang sudah
   terinstall versi 9.4+.
4. Build mod:
   ```
   ./gradlew build
   ```
   (Windows: `gradlew.bat build`)
5. Hasil jar ada di `build/libs/qtoggle-1.0.0.jar`.

## Instalasi
1. Pastikan sudah pasang **Fabric Loader 0.18.4+** dan **Fabric API** yang
   sesuai versi 26.1 di game-mu.
2. Copy `qtoggle-1.0.0.jar` ke folder `mods` instalasi Minecraft-mu.
3. Jalankan game, masuk ke **Options > Controls > Q Toggle** untuk
   memastikan/mengganti keybind toggle (default G).

## Perubahan terbaru (sudah diperbaiki)
Setelah dicek ulang lewat dokumentasi resmi Fabric, ada perubahan API
penting di Fabric API 26.1 yang sudah disesuaikan di source code ini:
- `KeyBindingHelper` (package `keybinding.v1`) di versi lama berganti
  nama jadi **`KeyMappingHelper`** (package `keymapping.v1`) di 26.1.
- Sistem kategori keybind vanilla berubah total: tidak lagi pakai
  `String` biasa, tapi object **`KeyMapping.Category`** yang dibuat lewat
  `KeyMapping.Category.create(ResourceLocation)`.
- Versi Fabric API yang dikonfirmasi mendukung 26.1–26.1.2 sudah diisi
  otomatis di `gradle.properties`: `0.149.1+26.1.2`. Cek lagi di
  Modrinth kalau saat kamu build sudah ada versi lebih baru untuk
  26.1.3 dst.

## Kalau ada error compile
Minecraft 26.1 masih baru, jadi nama-nama method internal (Mojang
mappings) di beberapa hal kadang sedikit berubah antar patch. Kalau
`QToggleClient.java` error pada baris yang menyentuh `Options`,
`KeyMapping`, atau `keyDrop`, buka file itu pakai IDE (IntelliJ IDEA +
plugin Minecraft Development sangat membantu), lalu pakai autocomplete
untuk menemukan nama field/method yang benar di versi 26.1 final —
struktur logikanya (cek tiap tick, matikan status key drop saat mode
OFF) tidak perlu diubah, hanya nama API-nya saja kalau berbeda.

## Catatan kompatibilitas server
Mod ini **client-side only** (lihat `"environment": "client"` di
fabric.mod.json) — aman dipakai di server manapun karena hanya mengubah
input di sisi klien kamu sendiri.
