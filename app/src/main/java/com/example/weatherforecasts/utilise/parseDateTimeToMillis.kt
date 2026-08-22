package com.example.weatherforecasts.utilise



import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

object DateTimeUtils {

    fun parseDateTimeToMillis(dateString: String, timeString: String): Long {
        if (dateString.isEmpty() || timeString.isEmpty()) {
            return System.currentTimeMillis()
        }

        val dateFormat = SimpleDateFormat("dd-M-yyyy", Locale.getDefault())
        val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())

        val date = dateFormat.parse(dateString)
        val time = timeFormat.parse(timeString)

        val calendar = Calendar.getInstance()
        if (date != null && time != null) {
            calendar.time = date
            val timeCalendar = Calendar.getInstance().apply { this.time = time }

            calendar.set(Calendar.HOUR_OF_DAY, timeCalendar.get(Calendar.HOUR_OF_DAY))
            calendar.set(Calendar.MINUTE, timeCalendar.get(Calendar.MINUTE))
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)
        }

        return calendar.timeInMillis
    }
}