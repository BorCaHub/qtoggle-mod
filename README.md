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

## Update penting (loom_version diperbaiki)
Versi Loom `1.15` di percobaan sebelumnya **tidak pernah ada sebagai
release** — yang ada hanya alpha (`1.15.0-alpha.x`) lalu langsung lompat
ke versi stabil `1.15.1` sampai `1.15.5`. Sudah diperbaiki ke
**`1.15.5`** (versi stabil terbaru per Maret 2026) di `gradle.properties`.
`minecraft_version` dan `loader_version` juga sudah disamakan dengan
instalasi gamemu yang terbukti jalan (26.1.2 dan loader 0.19.2).

Kalau pakai GitHub Actions, di workflow `.github/workflows/build.yml`,
baris `gradle wrapper --gradle-version 9.4` aman dibiarkan — Gradle
otomatis pakai versi compatible terbaru kalau 9.4 sudah tidak ada.

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
