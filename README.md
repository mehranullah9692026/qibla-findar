# Qibla Noor | قبلہ نور

Android app (Kotlin + Jetpack Compose): prayer times, Qibla compass, Hijri date, Tasbeeh, Azkar, 99 Names. Urdu / English.

## Build the APK and AAB

### Option A: GitHub Actions (no computer needed)
1. Upload this whole project to a GitHub repository (keep the folder structure, including `.github/workflows/build.yml`).
2. Open the **Actions** tab -> **Build Android** -> **Run workflow**.
3. When it finishes, download `QiblaNoor-APK` (install on a phone) and `QiblaNoor-AAB` from the run page.

### Option B: Android Studio
1. Open this folder in Android Studio (Ladybug or newer, JDK 17). If asked for Gradle, use version 8.9.
2. Install: Run > Run 'app'. Play Store: Build > Generate Signed App Bundle / APK.

## Play Store notes
- The AAB from Actions is **unsigned**. Sign it with your own upload key (Android Studio > Generate Signed Bundle) before uploading.
- `targetSdk` is 35. If Play Console asks for a newer API level, change `compileSdk` and `targetSdk` in `app/build.gradle.kts`.
- Add a 512x512 app icon, screenshots, and a privacy policy URL (see PRIVACY_POLICY.md) in Play Console.
- Permissions used: location (prayer times and Qibla only, processed on device).
- Not included yet: Azan alarms/notifications, Sehri/Iftar reminders.
