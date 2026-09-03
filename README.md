# 🌤️ WeatherForcasts App

> **A real-time weather tracking application that automatically detects user location to deliver precise local temperatures and atmospheric conditions.**
>
> ----------------
>
## 🛠️ Tech Stack & Architecture

* **Language:** Kotlin
* **UI Framework:** Android SDK (XML Layouts)
* **Architecture:** MVVM (Model-View-ViewModel)
* **Navigation:** Navigation Component 
* **Networking:** Retrofit
* **Location & Maps:** Fused Location Provider API & Google Maps SDK
* **Task Scheduling:** AlarmManager (Precise Background Timing)
* **Asynchronous Programming:** Kotlin Coroutines & Flow & Live data
* **Local Caching:** Room Database & SharedPreferences
* **State Management:** LiveData & StateFlow (Lifecycle-Aware Data Holders)

-------------------

## 💡 Technical Decisions & Challenges

* **Challenge:** Concurrent asynchronous operations (location persistence, weather API updates, and alarm scheduling) caused race conditions where the scheduler executed before Room database writes were finalized.
* **Solution:** Refactored operations into a sequential `suspend` pipeline using `viewModelScope` on `Dispatchers.IO`. Monitored state transitions via `LiveData` to guarantee database commits finish before triggering `AlarmManager` or updating UI components.
