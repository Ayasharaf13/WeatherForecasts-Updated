package com.example.weatherforecasts.alertscreen.viewmodel


import android.content.SharedPreferences
import androidx.lifecycle.*
import com.example.weatherforecasts.model.Alarm
import com.example.weatherforecasts.model.Alert
import com.example.weatherforecasts.model.Location
import com.example.weatherforecasts.model.RepositoryInterface
import com.example.weatherforecasts.utilise.AlarmSchedulerItem
import com.example.weatherforecasts.utilise.AlarmTimeCalculator
import com.example.weatherforecasts.utilise.AlarmTimeRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.util.Locale


class AlertViewModel(private val repo: RepositoryInterface) : ViewModel() {

    // 1. المكان الحالي المحدد (StateFlow يحتفظ دائماً بأحدث ID)
    private val _selectedLocationId = MutableStateFlow<Int?>(null)

    // SingleLiveEvent لتمرير المنبهات المجهزة للـ UI لجدولتها مرة واحدة فقط دون تكرار
    val scheduleAlarmEvent = SingleLiveEvent<AlarmSchedulerItem>()

    // 2. دمج سرياني البيانات (Alarms + Alerts) تلقائياً بناءً على Location ID الحالي
    @OptIn(ExperimentalCoroutinesApi::class)
    private val combinedFlow: Flow<Pair<List<Alarm>, List<Alert>>> = _selectedLocationId
        .filterNotNull() // لن ينطلق إلا إذا كان هناك ID محدد
        .flatMapLatest { id ->

            // جلب البيانات بشكل تفاعلي من Room لكل تغيير
            combine(
                repo.getStoredAlarmUserByLoc(id),
                repo.getStoredAlertApiByLoc(id)

            ) { alarms, alerts ->
                alarms to alerts
            }
        }

    // 3. تحويل الـ Combined Flow إلى LiveData للـ Fragment (يضمن إرسال القيمة فور العودة للشاشة)
    val combinedData: LiveData<Pair<List<Alarm>, List<Alert>>> =
        combinedFlow.asLiveData(Dispatchers.IO)

    private val _currentLocation = MutableLiveData<List<Int>>()
    val currentLocation: LiveData<List<Int>> get() = _currentLocation

    init {
        getAllLocation()

    }

    // دالة تحديد المكان (تُطلق التحديث فوراً)
    fun setLocation(locationId: Int) {
        _selectedLocationId.value = locationId
    }

    suspend fun addToAlarm(alarm: Alarm, locationId: Int) {
        withContext(Dispatchers.IO) {
            alarm.location_id = locationId
            repo.insert(alarm)
        }
        withContext(Dispatchers.Main) {
            setLocation(locationId)
        }
    }

    fun delete(alarm: Alarm) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.delete(alarm)
        }
    }

    fun deletealertapi(alert: Alert) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.deleteAlertApi_repo(alert)
        }
    }

    fun saveAlarmApi(alert: List<Alert>, locId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            alert[0].location_id = locId
            repo.insertAlertApi(alert)
        }
    }

    suspend fun saveLoc(loc: Location): Int {
        return withContext(Dispatchers.IO) {
            val locToInsert = loc.copy(idLoc = 0)
            val newGeneratedId = repo.insertLocation(locToInsert)
            newGeneratedId.toInt()
        }
    }

    fun getAllLocation() {
        viewModelScope.launch(Dispatchers.IO) {
            repo.getAllLocationIds().collect {
                _currentLocation.postValue(it)
            }
        }
    }

    suspend fun getLocById(locId: Int): Location {
        return repo.getLocationById(locId)
    }

    fun deleteLocation(loc: Location) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.deleteLocation(loc)
        }
    }

    fun deleteAllLocations(locationIds: List<Int>) {
        viewModelScope.launch(Dispatchers.IO) {
            locationIds.forEach { id ->
                val location = repo.getLocationById(id)
                if (location != null) {
                    repo.deleteLocation(location)
                }
            }
        }
    }

    suspend fun getLocById(lat: Double, lon: Double): Int? {
        return withContext(Dispatchers.IO) {
            repo.getLocationById(lat, lon)
        }
    }


    data class TimeRange(
        val start: Instant,
        val end: Instant
    )

    data class AlarmStatusResult(
        var from: Boolean = false,
        var firstAlarmApi: Boolean = false,
        var to: Boolean = false
    )

    data class AlarmEvaluationState(
        val statusResult: AlarmStatusResult,
        val storeStatusFrom: Boolean,
        val storeStatusTo: Boolean
    )


    // 1. Main Entry Function
    fun processAlarmsAndCheckAlerts(alarms: List<Alarm>, alerts: List<Alert>) {
        viewModelScope.launch(Dispatchers.Default) {
            for (alarmUser in alarms) {
                val alarmItem = evaluateAlarmForUser(alarmUser, alerts)
                // 👈 إرسال الحدث فقط إذا كان يحتوي على رسالة/تنبيه حقيقي
                if (alarmItem.message.isNotEmpty()) {
                    scheduleAlarmEvent.postValue(alarmItem)
                }

            }
        }
    }

    // 2. Evaluates Single Alarm
    private fun evaluateAlarmForUser(alarmUser: Alarm, alerts: List<Alert>): AlarmSchedulerItem {
        val currentAlarmStatus = AlarmStatusResult()
        val alertsToDelete = mutableListOf<Alert>()
        val messages = mutableSetOf<String>()
        var storeStatusFrom = false
        var storeStatusTo = false
        var firstMatchStored = 0

        val pickerRange = TimeRange(
            start = convertToInstant(alarmUser.localDateFrom, alarmUser.localTimeFrom),
            end = convertToInstant(alarmUser.localDateTo, alarmUser.localTimeTo)
        )

        for (alertApi in alerts) {
            val startEvent = alertApi.start?.toLong()?.times(1000)
            val endEvent = alertApi.end?.toLong()?.times(1000)

            if (startEvent != null && endEvent != null) {
                val apiAlertRange = TimeRange(
                    start = Instant.ofEpochMilli(startEvent),
                    end = Instant.ofEpochMilli(endEvent)
                )

                val statusResult = checkTimeDateInRange(pickerRange, apiAlertRange)

                if (statusResult.to || statusResult.from) {
                    firstMatchStored += 1
                    if (firstMatchStored == 1) {
                        storeStatusTo = statusResult.to
                        storeStatusFrom = statusResult.from
                    }
                    messages.add(alertApi.event.toString())
                    alertsToDelete.add(alertApi)
                }
            }
        }

        // Bundle context into 1 object
        val evaluationContext = AlarmEvaluationContext(
            alarmUser = alarmUser,
            state = AlarmEvaluationState(
                statusResult = currentAlarmStatus,
                storeStatusFrom = storeStatusFrom,
                storeStatusTo = storeStatusTo
            )
        )

        return createSchedulerItem(messages, alertsToDelete, evaluationContext)
    }

    // 3. Clean Check Function (Only 2 Parameters)
    private fun checkTimeDateInRange(
        picker: TimeRange,
        apiAlert: TimeRange
    ): AlarmStatusResult {
        val result = AlarmStatusResult()
        val zoneId = ZoneId.systemDefault()

        // Conversions (Exact same logic)
        val instantStartTime = LocalTime.from(apiAlert.start.atZone(zoneId))
        val instantEndTime = LocalTime.from(apiAlert.end.atZone(zoneId))
        val instantFromPickerTime = LocalTime.from(picker.start.atZone(zoneId))
        val instantToPickerTime = LocalTime.from(picker.end.atZone(zoneId))

        val instantStartDate = LocalDate.from(apiAlert.start.atZone(zoneId))
        val instantEndDate = LocalDate.from(apiAlert.end.atZone(zoneId))
        val instantFromPickerDate = LocalDate.from(picker.start.atZone(zoneId))
        val instantToPickerDate = LocalDate.from(picker.end.atZone(zoneId))

        // Helper for time checking (Exact same logic)
        fun checkTimeAlarm() {
            if (instantStartTime <= instantEndTime) {
                if (instantFromPickerTime in instantStartTime..instantEndTime) {
                    result.from = true
                } else if (instantToPickerTime in instantStartTime..instantEndTime) {
                    result.to = true
                }
            } else {
                // Case 2: Alert crosses midnight
                if (instantFromPickerTime >= instantStartTime || instantFromPickerTime <= instantEndTime) {
                    result.from = true
                } else if (instantToPickerTime >= instantStartTime || instantToPickerTime <= instantEndTime) {
                    result.to = true
                }
            }
        }

        // Date range verification (Exact same logic)
        if (instantFromPickerDate in instantStartDate..instantEndDate) {
            checkTimeAlarm()
        } else if (instantToPickerDate in instantStartDate..instantEndDate) {
            checkTimeAlarm()
        }

        return result
    }

    // 4. Helper method to construct result item
    // 1. Updated Function with 3 Parameters
    private fun createSchedulerItem(
        messages: Set<String>,
        alertsToDelete: List<Alert>,
        context: AlarmEvaluationContext
    ): AlarmSchedulerItem {
        val (alarmUser, state) = context

        return if (messages.isNotEmpty()) {
            val finalMess = messages.joinToString(" | ")

            // Exact same logic preserved
            if (state.storeStatusFrom) {
                state.statusResult.from = true
            } else if (state.storeStatusTo) {
                state.statusResult.to = true
            }

            val timeRequest = AlarmTimeRequest(
                alertApi = alertsToDelete,
                alarmUser = alarmUser,
                alarmStatus = state.statusResult
            )

            val calculatedMillis = AlarmTimeCalculator.calculateTriggerTimeMillis(timeRequest)
            AlarmSchedulerItem(triggerTimeMillis = calculatedMillis, message = finalMess)
        } else {
            AlarmSchedulerItem(triggerTimeMillis = System.currentTimeMillis(), message = "")
        }
    }

    // Data class bundling alarm user and evaluation state
    private data class AlarmEvaluationContext(
        val alarmUser: Alarm,
        val state: AlarmEvaluationState
    )

    // 5. Utility conversion function
    private fun convertToInstant(date: LocalDate, time: LocalTime): Instant {
        return LocalDateTime.of(date, time)
            .atZone(ZoneId.systemDefault())
            .toInstant()
    }



    // نموذج ينقل بيانات الحفظ للـ ViewModel
    data class SaveAlarmRequest(
        val alarm: Alarm,
        val latitude: String,
        val longitude: String,
        val sharedPreferences : SharedPreferences,
        val weatherAlerts: List<Alert>?
    )

    // نموذج يُرجع النتيجة للـ UI
    data class SaveAlarmResult(
        val messageResId: Int? = null,
        val isSuccess: Boolean,
        val alarmMessage: String = ""
    )


    fun Double.roundTo4Decimals(): Double {
        return String.format(Locale.US, "%.4f", this).toDouble()
    }

    fun saveAlarmAndLocation(request: SaveAlarmRequest): LiveData<SaveAlarmResult> {
        val resultLiveData = MutableLiveData<SaveAlarmResult>()

        viewModelScope.launch(Dispatchers.IO) {
            val (alarm, latStr, lonStr,sharedPreferences ,listOfAlerts) = request

            val safeLat = latStr.toDouble().roundTo4Decimals()
            val safeLon = lonStr.toDouble().roundTo4Decimals()

            val location = Location(idLoc = 0, lat = safeLat, lon = safeLon)

            var locId = repo.getLocationById(location.lat, location.lon) ?: 0

            if (locId <= 0 || locId == null) {
                locId = saveLoc(location)//repo.insertLocation(location).toInt()
                saveLocationToPreferences(sharedPreferences,locId)
            }

            if (locId > 0) {
                if (!listOfAlerts.isNullOrEmpty()) {
                    saveAlarmApi (listOfAlerts, locId)
                }
                addToAlarm(alarm, locId)
            }

            val alertMessage = if (listOfAlerts.isNullOrEmpty()) {
                "" // علامة للـ Fragment لاستخدام النص الافتراضي R.string.no_alert_weather_good
            } else {
                listOfAlerts[0].event ?: "Weather Alert"
            }

            resultLiveData.postValue(
                SaveAlarmResult(
                    isSuccess = true,
                    alarmMessage = alertMessage
                )
            )
        }

        return resultLiveData
    }

    private fun saveLocationToPreferences(sharedPreferences: SharedPreferences,locId: Int) {
        sharedPreferences.edit().putInt("idLocation", locId).apply()
    }

}

// TO DO  we need to delet alert when user remove Alarm saved local
// alertsToDelete.forEach { viewModelAlert.deletealertapi(it) }



