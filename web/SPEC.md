# Simple Interstitial Journal Specification

## Overview

This is a simple web application that allows users to write journal entries and view them in a list.

- The latest journal entry is displayed at the bottom of the page.
- The new-entry form is displayed at the bottom of the page.
- Each journal entry should have some text and a timestamp; the text should be interpreted as Markdown.
- Journal entries should be stored in a firestore database, though it should be possible to switch to another database in the future with minimal changes to the application code.
- The UI should be built with [vue 3](https://vuejs.org/).
- The web application should be deployed as a static website on GitHub pages via a GitHub action.
- The style should be simple and minimal, so as to stay out of the way of the user, and reduce distraction.
- The first screen should allow for logging in.
- After login, users should be able to see their journal entries and add new ones.
- Journal entries should be displayed in chronological order from the top down.
- The latest journal entry should be visible after logging in.
- Only the 100 most recent journal entries should be loaded at first, and older entries should be loaded on demand as the user scrolls up.

## Authentication

- Users should be able to log in with their email and password, or with a single-sign on provider, such as Google.

## Authorization

- Users should only be able to view and edit their own journal entries.