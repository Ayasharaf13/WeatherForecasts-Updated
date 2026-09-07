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


------------------


## 🎬 App Flow Demo & Screenshots

<p align="center">
<img width="260"  alt="video-scoretask_resize" src="https://github.com/user-attachments/assets/7389a3ae-5001-4edc-963a-ae09df1e5982" />
</p>


|<img src="https://github.com/user-attachments/assets/e5bd5527-5d3c-4830-b161-14fd969f2913" width="200"/> | <img src="https://github.com/user-attachments/assets/2789f63b-4396-49f1-8bfa-c18d53d46a91" width="200"/> | <img src="https://github.com/user-attachments/assets/bf61f186-13ff-47d7-aa66-ab27c4ddfd30" width="200"/> | <img src="https://github.com/user-attachments/assets/4f633807-0468-424a-90cd-f993a7c86431" width="200"/> |
| :---: | :---: | :---: | :---: |
|<img src="https://github.com/user-attachments/assets/98ce3e4a-69f2-450b-b3bc-b3a0701abe59" width="200"/> | <img src="https://github.com/user-attachments/assets/19bf3d12-3619-4205-925e-e1805de6a236" width="200"/> | <img src="https://github.com/user-attachments/assets/492ae302-a347-4813-bb71-031fd5c01cec" width="200"/> | <img src="https://github.com/user-attachments/assets/1d6333d5-6093-41b1-9515-ff2c6565aaa3" width="200"/> |
