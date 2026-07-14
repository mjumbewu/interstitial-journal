## 1. Project Setup

- [x] 1.1 Initialize a new Android Studio project (Kotlin/Compose) in an `android/` directory.
- [x] 1.2 Configure Gradle build scripts, application ID, and basic manifest.

## 2. Firebase Integration

- [x] 2.1 Add the Firebase Android SDK dependencies (Auth, Firestore).
- [x] 2.2 Register the Android app in the Firebase console and download `google-services.json`.
- [x] 2.3 Implement basic Firebase Authentication login flow in the Android app.
- [x] 2.4 Implement Firestore data access repository for reading and writing journal entries.
- [x] 2.5 Add Google Sign-In (`play-services-auth`) dependency to build configuration.
- [x] 2.6 Implement "Continue with Google" button and sign-in flow in `MainActivity`.

## 3. Quick Entry Dialog

- [x] 3.1 Create a new Activity (e.g., `QuickEntryActivity`) configured with a translucent/dialog theme in the manifest.
- [x] 3.2 Build the Compose UI for the bottom sheet layout (text input field, save button).
- [x] 3.3 Implement the auto-focus logic to immediately show the keyboard when the activity starts.
- [x] 3.4 Wire the "Save" button to the Firestore repository to create a new entry and close the activity.
- [x] 3.5 Configure `QuickEntryActivity` with edge-to-edge layout and apply `imePadding` so it shifts up with the keyboard.

## 4. Home Screen Widget

- [x] 4.1 Add the Jetpack Glance dependencies to the project.
- [x] 4.2 Create a `GlanceAppWidget` class and its receiver.
- [x] 4.3 Build the widget UI to display a list of recent entries and an "Add" button.
- [x] 4.4 Wire the "Add" button to launch the `QuickEntryActivity` via a PendingIntent.
- [x] 4.5 Implement the background data fetching logic to populate the widget with data from Firestore upon update.
