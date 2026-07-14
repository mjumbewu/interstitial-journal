## ADDED Requirements

### Requirement: Display Recent Entries
The home screen widget SHALL display a list of the most recent journal entries, ordered chronologically.

#### Scenario: Viewing the widget
- **WHEN** the user looks at the home screen widget
- **THEN** they see their recent journal entries formatted with timestamps.

### Requirement: Launch Quick Entry
The home screen widget SHALL provide a UI element (e.g., an "Add" button or text box mimic) that launches the quick entry dialog.

#### Scenario: Tapping to add an entry
- **WHEN** the user taps the "Add" area on the widget
- **THEN** the `quick-entry-dialog` is immediately launched without opening the full application UI.
