## Why

The goal is to ensure the user's phone is more helpful than not anytime it is unlocked by putting a journal entry widget directly on the home screen. This reduces friction for entering interstitial journal entries and keeps recent context visible, making the journaling habit effortless. We are opting for a native Android approach to ensure the best possible widget experience.

## What Changes

- A new pure Kotlin Android application will be created alongside the existing web app.
- The Android app will provide a home screen widget displaying recent journal entries.
- The widget will include an "Add Entry" button that opens a transparent activity (bottom sheet dialog), similar to Google Tasks, allowing for quick text input without fully opening the main app.
- The Android app will connect to the same Firebase backend (Auth and Firestore) as the existing Vue web app.
- The existing Vue web app will remain untouched and serve as the desktop/web client.

## Capabilities

### New Capabilities
- `android-app`: The base Android application structure, including Firebase initialization and user authentication using the Firebase Android SDK.
- `home-screen-widget`: The Android AppWidgetProvider, the UI layout (Glance/Compose or RemoteViews) for displaying recent entries, and the background data sync logic.
- `quick-entry-dialog`: A transparent activity or bottom sheet dialog triggered by the widget for typing and saving new entries.

### Modified Capabilities
- (None)

## Impact

- A new `android/` directory will be added to the repository containing a standard Android project structure.
- The existing `web/` directory and its deployment are unaffected.
- The Firebase project will need an Android app registered to provide the `google-services.json` file.
