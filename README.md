# InstaMod (insta-)

> **Custom Floating Bottom Navigation Bar & Universal Media Downloader for Instagram on Android**

An open-source **LSPosed (Xposed)** module and companion settings application for Android that customizes the official Instagram app with a modern floating navigation bar and native media download buttons across Reels, Stories, and Feed posts.

---

## ✨ Features

### 1. 🏝️ Floating Island Bottom Navigation Bar
* Transforms Instagram's standard edge-to-edge docked bottom navigation bar into a modern, detached floating pill island.
* Adds customizable horizontal/bottom margins, card corner radius (24dp), and subtle elevation shadow.
* Seamlessly respects dark and light theme palettes.

### 2. 🎬 Reels HD Media Downloader
* Injects a clean, native-styled download button directly onto the vertical Reels action panel (beside Like, Comment, and Share).
* Resolves the active video streaming URL and enqueues high-speed downloads via Android's native `DownloadManager`.
* Automatically organizes saved media into `/storage/emulated/0/Download/Instagram/`.

### 3. 📸 Stories Media Downloader
* Injects a download icon directly in the Story viewer header overlay (next to the three-dots menu).
* Captures the active story segment (whether HD photo candidate or video stream).

### 4. 🖼️ Feed & Carousel Posts Downloader
* Adds a direct download icon to the post action bar adjacent to the bookmark button.
* Downloads full-resolution feed images and video posts.

### 5. ⚙️ Companion Material 3 Settings App
* Standalone launcher app with switches to toggle any hook on or off:
  - Toggle Floating Navigation Bar
  - Toggle Reels Downloader
  - Toggle Stories Downloader
  - Toggle Posts Downloader

---

## 🏗️ Project Architecture

```
insta-/
├── app/
│   ├── src/main/
│   │   ├── AndroidManifest.xml          # LSPosed module metadata & scopes
│   │   ├── assets/
│   │   │   └── xposed_init              # Main entry point declaration
│   │   ├── java/com/smitronix/instamod/
│   │   │   ├── MainHook.kt              # Root IXposedHookLoadPackage router
│   │   │   ├── hooks/
│   │   │   │   ├── FloatingNavHook.kt       # Modifies TabBar into a floating pill
│   │   │   │   ├── ReelsDownloaderHook.kt   # Injects download button on Reels
│   │   │   │   ├── StoriesDownloaderHook.kt # Injects download button on Stories
│   │   │   │   └── PostsDownloaderHook.kt   # Injects download button on Feed posts
│   │   │   ├── utils/
│   │   │   │   ├── DownloadHelper.kt    # Android DownloadManager integration
│   │   │   │   ├── ViewUtils.kt         # Layout styling & pill drawables
│   │   │   │   └── PrefsHelper.kt       # XSharedPreferences reader
│   │   │   └── ui/
│   │   │       └── MainActivity.kt      # Material 3 Companion Settings UI
│   │   └── res/                         # Vector drawables, themes & layouts
│   └── build.gradle.kts                 # Compile target: Android SDK 35
├── build.gradle.kts
├── settings.gradle.kts
└── gradlew
```

---

## 🚀 Installation & Usage

### Method A: Rooted Devices (LSPosed / Magisk / KernelSU / APatch)

1. Build or download the module APK (`app-debug.apk`).
2. Install the APK on your device:
   ```bash
   adb install app/build/outputs/apk/debug/app-debug.apk
   ```
3. Open **LSPosed Manager**:
   * Navigate to the **Modules** tab.
   * Enable **InstaMod**.
   * Ensure **Instagram** (`com.instagram.android`) is checked in the module scope.
4. Force stop Instagram (`Settings > Apps > Instagram > Force Stop`) and relaunch it.

---

### Method B: Non-Rooted Devices (LSPatch)

If your device is not rooted:
1. Download and install **[LSPatch](https://github.com/LSPosed/LSPatch)**.
2. In LSPatch, tap **Manage** and select your installed official Instagram APK (or an APK file).
3. Select **Portable Mode** or **Local Mode**.
4. Add **InstaMod** (`app-debug.apk`) as the embedded module.
5. Tap **Start Patching**. Once finished, install the generated patched Instagram APK.

---

## 🛠️ Building from Source

### Prerequisites
* Java JDK 17 or JDK 21
* Android SDK (API 34 or 35)

### Build Commands
```bash
# Clone the repository
git clone https://github.com/SmitroniX/insta-.git
cd insta-

# Build Debug APK
./gradlew assembleDebug

# Built APK output location:
# app/build/outputs/apk/debug/app-debug.apk
```

---

## 📄 License
This project is licensed under the [MIT License](LICENSE).