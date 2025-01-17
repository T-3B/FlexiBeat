# FlexiBeat

FlexiBeat is a lightweight local music player for Android, written in Kotlin and Jetpack Compose.

## Features

- Supports light and dark theme (depending on device settings), and also dynamic ones (based on the home wallpaper, for Android 12+).
- MVVM Architecutre
- DataStore and Room for persitent data storage
- Uses Exoplayer, with usual music controls: play/pause, next/previous track, seek back/forward, shuffle, repeat
- Search with filters, e.g. `year=1999 title="Love song"`, `!genre=jazz global_search_multiWords` and `global_search_multiWords` (case insensitive, negative filters with `!` prefix, can mix a global search with filters, optional quotation marks for filter values)
- MediaStore to fetch songs

This application was heavily tested on a Android 11 smartphone, and "poor" testing on Android 12 and 15.\
But it should work without any issue for any Android 7+ devices, for any screen size.

## Context

FlexiBeat is in fact a project made for the course `[PROJ0011-1] Personal Student Project` at University of Liège.

My submission can be found in the latest release (code archive, universal `.apk` file, and report).
