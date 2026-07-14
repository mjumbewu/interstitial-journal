## Context

The interstitial journal currently exists as a Vue 3 web application backed by Firebase Authentication and Firestore. To reduce friction and increase usefulness, we want a home screen widget that displays recent entries and allows quick addition of new entries without fully opening an app. Because web apps (PWAs) cannot provide native Android home screen widgets with this level of interactivity, we need a native Android solution.

## Goals / Non-Goals

**Goals:**
- Provide a native Android app that connects to the existing Firebase project.
- Implement an Android home screen widget that reads the 100 most recent journal entries from Firestore.
- Implement a "Quick Entry" flow from the widget using a transparent activity or bottom sheet (the "Google Tasks" pattern) to allow fast text input.
- Keep the existing Vue 3 web app functioning normally as the desktop client.

**Non-Goals:**
- Creating an iOS app or widget at this time.
- Creating a full-featured UI in the Android app (the main app UI can be a very basic list, as the focus is the widget).
- Wrapping the Vue app in Capacitor or Cordova; we are opting for a pure native Android path for a clean separation of concerns and robust widget support.

## Decisions

- **Architecture:** Pure Native Android App (Kotlin/Jetpack Compose).
  - *Rationale:* Avoids the complexity of bridging web auth state (Capacitor) to native widget code. Provides the most robust and performant widget experience.
- **Widget UI Framework:** Jetpack Glance.
  - *Rationale:* Glance allows building AppWidgets using Jetpack Compose style syntax, which is modern and easier to maintain than XML-based RemoteViews.
- **Quick Entry Pattern:** Transparent Activity themed as a Bottom Sheet Dialog.
  - *Rationale:* Android AppWidgets do not support direct text input (`EditText`). To simulate inline typing, tapping the "add" button on the widget will launch an activity with a translucent background that immediately requests focus and shows the keyboard, mimicking the Google Tasks widget behavior. To prevent the layout from being obscured by the keyboard, we will enable edge-to-edge window rendering and apply Compose's `Modifier.imePadding()` to the layout.
- **Data Sync:** Firebase Android SDK.
  - *Rationale:* The widget and the native app will authenticate and read/write directly to the existing Firestore database, matching the web app's data structure.
- **Authentication:** Support Email/Password and Google Sign-In.
  - *Rationale:* To match the web app authentication features, we will integrate `play-services-auth` (Google Sign-In SDK) to fetch Google ID tokens and authenticate with Firebase.

## Risks / Trade-offs

- **Risk:** Maintaining two codebases (Vue and Kotlin) for UI.
  - *Mitigation:* The Android app UI will be kept extremely minimal. Its primary purpose is to host the widget and the quick-entry dialog. The web app remains the primary "full" client.
- **Risk:** Background sync for the widget draining battery or hitting Firestore quotas.
  - *Mitigation:* The widget should only update when actively viewed or when a new entry is added locally, rather than aggressive background polling. We'll use Firestore's offline persistence and listeners appropriately.
