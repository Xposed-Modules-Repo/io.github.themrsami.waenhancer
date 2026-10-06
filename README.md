<div align="center">

# WaEnhancer (Enhanced Edition)

An advanced, privacy-first Xposed module for WhatsApp and WhatsApp Business.  
Built for performance, resilience, and complete local privacy.

[![Android](https://img.shields.io/badge/Platform-Android%2016%20(API%2036)-3DDC84?logo=android&logoColor=white)](#)
[![Kotlin](https://img.shields.io/badge/Language-Kotlin-7F52FF?logo=kotlin&logoColor=white)](#)
[![Framework](https://img.shields.io/badge/Framework-LSPosed%20%7C%20Xposed-purple)](#)
[![License](https://img.shields.io/badge/License-GPL%20v3-blue.svg)](LICENSE)
[![Privacy](https://img.shields.io/badge/Telemetry-Zero%20%2F%20100%25%20Offline-brightgreen)](#)

</div>

---

## Overview

**WaEnhancer (Enhanced Edition)** is a high-performance Xposed framework module designed to extend the capabilities of WhatsApp and WhatsApp Business on rooted Android devices. 

This edition is engineered with a **zero-telemetry, offline-first** philosophy, featuring a modernized Material 3 user interface, real-time hook failure diagnostics, robust contact resolution, and comprehensive Android 16 (API 36) support.

---

## Key Highlights

### 🛡️ Zero Telemetry & Pure Local Privacy
- Operates strictly on-device with no background network pings, tracking beacons, or analytics.
- All update-checking logic, nag banners, and external connection loops have been completely removed.
- Secure, encrypted local data handling with zero external telemetry.

### 🔍 Real-Time Hook Failure Diagnostics
- Integrated diagnostic logger that monitors hooks, reflection lookups, and DexKit queries in real-time.
- Automatically logs method signature changes and stack traces when WhatsApp updates.
- One-tap access via the toolbar bug icon and the Root Diagnostics panel, featuring instant clipboard export and log clearing.

### 👤 Robust Contact & LID Resolution
- Advanced SQLite resolver that queries contacts across both phone JIDs and WhatsApp Linked IDs (LID).
- Automatic fallback to WhatsApp push names (`wa_name`), eliminating raw 15-digit numeric IDs from toast notifications, online status alerts, and anti-revoke messages.

### 🎨 Modern Material 3 Electric Indigo Theme
- Refined UI palette powered by Electric Indigo (`#4F46E5` / `#6366F1`) and clean emerald accents.
- Responsive Light and Dark themes with full support for Android Monet dynamic colors and custom presets.
- Snappy, zero-delay UI transitions with artificial animation delays removed.

### ⚡ Android 16 (API 36) Resilience
- Hardened preference and collection lifecycles preventing crashes during preference inflation on newer Android runtimes.
- Device-protected storage compatibility with modern LSPosed and Zygisk environments.

---

## Features

<details>
<summary><b>🔒 Privacy & Security</b></summary>

- **Anti-Revoke Messages & Statuses**: Read recalled messages and view deleted status updates with sender identification.
- **Anti-Disappearing Messages**: Prevent messages from disappearing in timed chats.
- **Freeze Last Seen**: Maintain your last seen timestamp while actively browsing.
- **Incognito Status Viewing**: View status updates without appearing on the viewer list.
- **Ghost Mode**: Complete stealth browsing mode.
- **Send Blue Ticks on Reply**: Blue ticks are only dispatched when you reply to a message.
- **Hide Typing & Audio Recording**: Conceal chat activity indicators.
- **Disable View Once**: Open and save View Once media without restrictions.
- **Call Privacy & Blocking**: Whitelist/blacklist call handling with custom rejection responses (busy, unavailable, rejected).
- **Hide Forwarded Tag**: Forward messages without the "Forwarded" label.
- **Remove Forward & Pinned Limits**: Bypass chat pinning and forwarding caps.

</details>

<details>
<summary><b>📸 Media Enhancements</b></summary>

- **Uncapped Upload Quality**: Send images and videos in original full resolution without aggressive compression.
- **60 FPS Video Support**: Maintain maximum framerates during video transfers.
- **View Once Media Downloader**: Save View Once photos and videos directly to local storage.
- **Media Preview**: In-app media preview without leaving conversation contexts.
- **Audio Transcription**: Integrated transcription provider support for voice notes.

</details>

<details>
<summary><b>🎨 UI & Customization</b></summary>

- **Global Themes**: Electric Indigo (default), Material You (Monet), Blue, Cyan, Purple, Orange, Red, and Pink.
- **Home Screen Customization**: Custom titles, bio display, DND switch, and tab reordering.
- **Separated Chats & Groups**: Distinct navigation tabs for individual chats and group conversations.
- **Online Indicators**: Real-time status dots in conversation lists.
- **Clean About Screen**: Pure system diagnostics (Package, Version, Device, OS API, ABI) without external advertisements or sponsor clutter.

</details>

<details>
<summary><b>🛠️ Diagnostics & Root Tools</b></summary>

- **Hook Failure Logs**: Inspect detailed stack traces for any broken method hooks following WhatsApp client updates.
- **Root Diagnostics**: Verify SELinux policy compatibility and detect Hide My App List restrictions.
- **Backup & Restore**: Export and import complete module configurations in portable JSON format.

</details>

---

## Requirements

- **Device**: Android 8.0 to Android 16 (API 36).
- **Root Solution**: Magisk (v24+), KernelSU, or APatch.
- **Xposed Framework**: [LSPosed](https://github.com/JingMatrix/LSPosed) (Zygisk release) or equivalent.
- **Target Application**: Supported WhatsApp or WhatsApp Business versions (e.g., `2.26.34.xx` - `2.26.38.xx`+).

---

## Building from Source

### Prerequisites
- JDK 17 (Temurin or OpenJDK)
- Android SDK with Build Tools 36+
- CMake 3.22+ and NDK 27+

### Build Steps

1. Clone the repository:
   ```bash
   git clone <repository-url>
   cd WaEnhancer
   ```

2. Build for WhatsApp:
   ```bash
   ./gradlew assembleWhatsappDebug
   ```
   The output APK will be generated at:
   `app/build/outputs/apk/whatsapp/debug/`

3. Build for WhatsApp Business:
   ```bash
   ./gradlew assembleBusinessDebug
   ```
   The output APK will be generated at:
   `app/build/outputs/apk/business/debug/`

---

## Installation & Setup

1. Install the generated APK on your rooted device.
2. Open your **LSPosed Manager**.
3. Enable the **WaEnhancer** module and ensure the scope includes:
   - **WhatsApp** (`com.whatsapp`) or **WhatsApp Business** (`com.whatsapp.w4b`)
4. Force-stop WhatsApp or perform a soft reboot.
5. Open WaEnhancer, customize your preferred settings, and restart WhatsApp.

---

## Architecture Overview

```
WaEnhancer/
├── app/
│   ├── src/main/java/com/wmods/wppenhacer/
│   │   ├── activities/           # Main, About, and Search activities
│   │   ├── ui/fragments/         # Home, General, Privacy, Media, Customization
│   │   ├── xposed/
│   │   │   ├── core/             # Feature loader, lifecycle hooks, WppCore
│   │   │   ├── features/         # Modular feature implementations
│   │   │   │   ├── general/      # Anti-Revoke, call privacy, expiration bypass
│   │   │   │   ├── media/        # High quality media, view-once unlock
│   │   │   │   ├── customization/# Floating bars, custom layouts, colors
│   │   │   │   └── others/       # Toast viewers, online tracking
│   │   │   └── utils/            # HookFailureLogger, DexKit integration, RootDiagnostics
│   │   └── preference/           # SafePreferenceDataStore, custom preferences
│   └── src/main/res/             # Material 3 Electric Indigo layouts, drawables, themes
```

---

## Legal & Disclaimer

- **No Proprietary Code**: This project does not contain, distribute, or unpack any proprietary binary code belonging to WhatsApp Inc. or Meta Platforms, Inc.
- **Dynamic Memory Modification**: All functionality is executed dynamically at runtime in device memory via the Xposed framework.
- **Educational & Research Purpose**: This module is provided for educational and research purposes. Use responsibly and in accordance with applicable laws.

---

## License

This project is licensed under the [GNU General Public License v3.0](LICENSE).
