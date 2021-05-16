# DrawMe

DrawMe is an implementation of the famous "Pictionary" game, implemented in Android.

It can be played online with others players, or alone with an AI which tries to recognize your drawings (currently not implemented in the game).

## Build

1. Clone or download this repository

   ```sh
   git clone https://github.com/depp57/DrawMe.git
   cd DrawMe
   ```

2. Open the project in Android Studio and run it from there or build an APK directly through Gradle:

   ```sh
   ./gradlew assembleDebug
   ```
   
   Add the Android SDK to you PATH environment variable or create the ANDROID_SDK_ROOT variable for
   this to work.


### Deploy to device/emulator

   ```sh
   ./gradlew installDebug
   ```

*You can also replace the "Debug" with "Release" to get an optimized release binary.*
