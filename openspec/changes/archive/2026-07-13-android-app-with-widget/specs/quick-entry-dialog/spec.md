## ADDED Requirements

### Requirement: Transparent Input UI
The quick entry dialog SHALL appear as a translucent overlay (or bottom sheet) over the home screen, avoiding a full context switch into a standard app screen.

#### Scenario: Dialog opens over home screen
- **WHEN** the dialog is launched from the widget
- **THEN** the background of the dialog is transparent or blurred, keeping the home screen visible behind it.

### Requirement: Immediate Keyboard Focus
The quick entry dialog SHALL automatically focus its text input field and summon the on-screen keyboard upon launching.

#### Scenario: Ready to type
- **WHEN** the dialog is launched
- **THEN** the user can immediately begin typing their entry without needing an extra tap to focus the input.

### Requirement: Save and Close
The quick entry dialog SHALL save the entered text to Firestore as a new journal entry and close itself automatically upon successful save.

#### Scenario: Saving an entry
- **WHEN** the user types an entry and taps the "Save" or submit button
- **THEN** the entry is written to Firestore, the dialog dismisses, and the user is returned seamlessly to their home screen.

### Requirement: Keyboard Inset Handling
The quick entry dialog SHALL adjust its layout to shift above the software keyboard when the keyboard is displayed, ensuring that the text input field and actions remain visible.

#### Scenario: Keyboard shifts dialog layout
- **WHEN** the dialog is focused and the software keyboard is displayed
- **THEN** the dialog shifts up so that its bottom edge rests on top of the keyboard.
