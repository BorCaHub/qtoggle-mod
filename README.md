# Q Toggle (Drop Lock) — Mod Fabric untuk Minecraft 26.1

## Fungsi Mod
- Tombol **Q** secara default **TIDAK** akan drop item (aman dari drop tidak sengaja saat PvP).
- Ada **keybind toggle** (default: **G**) untuk menyalakan/mematikan mode drop:
  - **OFF (default)** → Q dikunci, tidak drop apapun.
  - **ON** → Q berfungsi normal seperti vanilla (drop item).
- Status ON/OFF ditampilkan sebagai teks singkat di **action bar** (atas hotbar) setiap kali toggle ditekan.
- Keybind toggle bisa diganti di **Options > Controls > Q Toggle**.

---

## Yang Dibutuhkan untuk Compile

1. **JDK 25** terinstall
2. Koneksi internet ke `maven.fabricmc.net` dan Maven Central

---

## Langkah Compile

1. Cek versi **Fabric API** terbaru yang mendukung Minecraft 26.1 di:
   ```
   https://modrinth.com/mod/fabric-api/versions
   ```
2. Buka `gradle.properties`, ganti nilai `fabric_version` jika ada versi lebih baru:
   ```properties
   fabric_version=0.149.1+26.1.2
   ```
3. Build mod:
   ```bash
   # Linux / macOS
   ./gradlew build

   # Windows
   gradlew.bat build
   ```
4. Hasil jar ada di `build/libs/qtoggle-1.0.0.jar`.

---

## Instalasi di Minecraft

1. Pastikan sudah pasang **Fabric Loader 0.19.2+** dan **Fabric API** versi 26.1.
2. Copy `qtoggle-1.0.0.jar` ke folder `mods` instalasi Minecraft-mu.
3. Jalankan game, masuk ke **Options > Controls > Q Toggle** untuk memastikan/mengganti keybind toggle (default: **G**).

---

## Catatan Teknis

### Kenapa error `modImplementation` bisa muncul
Fabric Loom mensyaratkan baris `mappings` di blok `dependencies` — tanpanya,
konfigurasi `modImplementation` tidak pernah dibuat dan build langsung gagal.
File `build.gradle` di project ini sudah menyertakan:
```groovy
mappings loom.officialMojangMappings()
```

### Kenapa `settings.gradle` perlu `dependencyResolutionManagement`
Gradle 9.x memisahkan deklarasi repository plugin (di `pluginManagement`) dengan
repository dependency runtime. Fabric maven dan Mojang maven harus ada di
`dependencyResolutionManagement` agar Gradle bisa resolve Minecraft dan Fabric API.

### Tentang GitHub Actions
Workflow **tidak** menjalankan `gradle wrapper` manual — wrapper sudah ada di repo
dan siap dipakai langsung. Cukup `chmod +x ./gradlew` lalu `./gradlew build`.

---

## Kompatibilitas Server
Mod ini **client-side only** (`"environment": "client"` di `fabric.mod.json`) —
aman dipakai di server manapun karena hanya mengubah input di sisi klien.

---

## Kalau Ada Error Compile

Minecraft 26.1 masih tergolong baru. Jika `QToggleClient.java` error pada baris
yang menyentuh `Options`, `KeyMapping`, atau `keyDrop`:
1. Buka project dengan **IntelliJ IDEA** + plugin **Minecraft Development**.
2. Gunakan autocomplete untuk menemukan nama field/method yang benar di versi 26.1.
3. Struktur logika (cek tiap tick, matikan drop key saat mode OFF) tidak perlu diubah.
