# JavaFX Music Player

A simple and elegant music player built with JavaFX.

## Features

- Play, pause, and skip through music tracks
- Playlist management
- Volume control with slider
- Progress bar for track duration
- Rotating album art animation
- Random track selection on startup
- Tooltip hints for buttons
- Clean and modern UI design

## Requirements

- Java 11 or higher
- Maven
- JavaFX 19.0.2.1
- Music files in the `music` directory at the root of the project

## Building and Running

1. Clone the repository
2. Create a `music` directory at the root of the project
```
music_player/
├── src/
│   ├── main/
│   │   ├── java/com/music_player/
│   │   │   ├── HelloApplication.java
│   │   │   └── HelloController.java
│   │   └── resources/com/music_player/
│   │       ├── css/
│   │       ├── icons/
│   │       └── views/
└── music/
    └── (your music files)
```
3. Add your music files to the `music` directory
4. Build and run the project:
   
```bash
   mvn clean install
   mvn javafx:run