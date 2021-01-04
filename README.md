# HearTask

A small sample application based on open https://hearthis.at/ API. It allows the user to display the list of artists, list their songs and play them. The player controls are available in foreground notification or in-app player bottom sheet.

The application uses ExoPlayer and some of the newer experimental technologies like Jetpack Compose, Dagger Hilt and others.

The application architecture is based on MVVM pattern with repositories and use cases. All the UI is contained in a single activity and the navigation is provided via Navigation Component/Jetpack Compose integration.

// TODO improve README
