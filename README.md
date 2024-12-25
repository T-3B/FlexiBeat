# FlexiBeat

FlexiBeat is a local music player for Android, written in Kotlin and Jetpack Compose.

## Features

- Supports light and dark theme (depending on device settings), and also dynamic ones (based on the home wallpaper, for Android 12+).
- MVVM Architecutre
- DataStore and Room for persitent data storage
- Uses Exoplayer, with usual music controls: play/pause, next/previous track, seek back/forward, shuffle, repeat
- Search with filters (e.g. `release_date=1999,title=Love`)
- MediaStore to fetch songs

This application was heavily tested on a Android 11 smartphone.

## Context

FlexiBeat is in fact a project made for the course `[PROJ0011-1] Personal Student Project` at University of Liège.

My submission can be found in the latest release (code archive and `.apk` file).
