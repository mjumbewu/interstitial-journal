## ADDED Requirements

### Requirement: Firebase Authentication
The Android application SHALL allow the user to authenticate using Firebase Authentication.

#### Scenario: User logs in successfully
- **WHEN** the user opens the app and enters valid credentials
- **THEN** they are authenticated with Firebase and can access their journal entries.

### Requirement: Firestore Connection
The Android application SHALL connect to the existing Firestore database used by the web application.

#### Scenario: App reads entries
- **WHEN** the user is authenticated
- **THEN** the app can fetch the latest journal entries from the "entries" collection.

### Requirement: Google Authentication
The Android application SHALL support Google Sign-In as an authentication mechanism, alongside email/password credentials.

#### Scenario: User logs in using Google Sign-In
- **WHEN** the user selects Google Sign-In and completes the OAuth flow
- **THEN** they are authenticated with Firebase and can access their journal entries.
