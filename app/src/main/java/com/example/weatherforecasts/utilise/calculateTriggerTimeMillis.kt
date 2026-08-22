package com.example.weatherforecasts.utilise


import com.example.weatherforecasts.alertscreen.viewmodel.AlertViewModel
import com.example.weatherforecasts.model.Alarm
import com.example.weatherforecasts.model.Alert


data class AlarmTimeRequest(
    val alertApi: List<Alert>? = null,
    val alarmUser: Alarm? = null,
    val alarmStatus: AlertViewModel.AlarmStatusResult
)



object AlarmTimeCalculator {

    fun calculateTriggerTimeMillis(request: AlarmTimeRequest): Long {
        return when {
            request.alarmStatus.from -> {
                // 👈 1. هنا تم استدعاؤها لحساب تاريخ ووقت البداية
                DateTimeUtils.parseDateTimeToMillis(
                    request.alarmUser?.textDateFrom.orEmpty(),
                    request.alarmUser?.textFromTime.orEmpty()
                )
            }
            request.alarmStatus.to -> {
                // 👈 2. هنا تم استدعاؤها لحساب تاريخ ووقت النهاية
                DateTimeUtils.parseDateTimeToMillis(
                    request.alarmUser?.textDateTo.orEmpty(),
                    request.alarmUser?.textToTime.orEmpty()
                )
            }
            request.alarmStatus.firstAlarmApi && !request.alertApi.isNullOrEmpty() -> {
                // هنا لا نحتاج لاستدعائها لأن الوقت قادم جاهزاً بالثواني (Epoch Seconds) من الـ API
                val firstAlert = request.alertApi[0]
                val startSeconds = firstAlert.start?.toLong() ?: 0L
                startSeconds * 1000L
            }
            else -> System.currentTimeMillis()
        }
    }
}