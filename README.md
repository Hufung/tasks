# Task Manager Android App

A beautiful and user-friendly Android task management application built with Kotlin and Jetpack Compose.

## Features

- ✅ **Task & Subtask Management** - Create tasks with multiple subtasks and track progress
- 🏷️ **Tags** - Organize tasks with customizable tags
- 📅 **Due Dates & Reminders** - Set due dates and receive notifications
- 🎯 **Priority Levels** - Four priority levels (Low, Medium, High, Critical) with visual indicators
- 📝 **Task Descriptions** - Add detailed descriptions to tasks
- 🎨 **Beautiful UI** - Modern Material Design 3 interface with light/dark mode support
- 💾 **Offline Support** - All data stored locally using Room Database

## Tech Stack

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose with Material Design 3
- **Architecture**: MVVM with Repository pattern
- **Database**: Room Database
- **Navigation**: Jetpack Navigation Compose
- **Notifications**: Android AlarmManager + NotificationManager

## Building the APK

### Option 1: GitHub Actions (Recommended)

The project is configured to automatically build the APK using GitHub Actions:

1. Push your code to GitHub
2. Go to the "Actions" tab in your repository
3. The workflow will automatically build the APK
4. Download the APK from the "Artifacts" section

### Option 2: Local Build

If you have Android SDK installed:

```bash
./gradlew assembleRelease
```

The APK will be generated at: `app/build/outputs/apk/release/app-release.apk`

## Installation

1. Download the APK file
2. Enable "Install from Unknown Sources" in your Android device settings
3. Open the APK file and install

## Screens

- **Home Screen** - View all tasks with filtering and sorting options
- **Add/Edit Task** - Create or modify tasks with all details
- **Task Detail** - View complete task information and manage subtasks

## Permissions

- `POST_NOTIFICATIONS` - For task reminders
- `SCHEDULE_EXACT_ALARM` - For precise reminder timing

## Requirements

- Android 8.0 (API 26) or higher
- Minimum SDK: 26
- Target SDK: 34

## License

This project is open source and available under the MIT License.