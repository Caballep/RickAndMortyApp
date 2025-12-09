# Rick and Morty Character App!

![Rick and Morty GIF](./assets/rickandmorty.gif)

Wubba Lubba Dub Dub!

This is a simple, modern Android application that lets you jump into the **Rick and Morty universe** to find any character you want. Clean architecture and a bunch of cool design patterns and strategies were applied.

* **Find Anyone:** Search for characters easily by name.
* **See the Details:** Get all the essential stats: are they **Alive** or **Dead**? What species are they? Where did they originate?
* **Offline Mode:** If you lose Wi-Fi or your phone's signal drops in a weird dimension, no problem! The app saves character data locally so you can keep browsing without an internet connection.
* **Smart Data Handling:** The app transforms the raw data from the API into something clean and usable for the screen, ensuring everything looks right.
* **Crash Protection / Error Handling:** App gives you a clear error message instead of crashing, keeping the experience smooth.

### Key Tools Used:
| What | Why |
|:---|:---|
| **Jetpack Compose** | Used this to build the entire screen experience. It makes the UI fast and reactive. |
| **Hilt** | This handles dependency injection, making it easy to wire up all the different pieces of the app. |
| **Ktor Client** | Used this library to fetch the character data from the Rick and Morty API over the internet. |
| **Jetpack Room** | This is the local database used to save character data on your device, enabling the handy offline feature. |
| **Kotlin Coroutines** | Used for all the background work—like fetching data or saving to the database—ensuring the app stays fast and responsive. |
| **Coil** | Loads and displays character images quickly and efficiently. |