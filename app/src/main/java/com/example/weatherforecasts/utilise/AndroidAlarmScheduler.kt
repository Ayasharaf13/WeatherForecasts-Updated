package com.example.weatherforecasts.utilise


import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.example.weatherforecasts.alertscreen.view.MyReceiver

import kotlin.jvm.java

data class AlarmSchedulerItem(
    val triggerTimeMillis: Long,
    val message: String
)



class AndroidAlarmScheduler(private val context: Context) {

    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    /**
     * تجدولة المنبه - تأخذ معاملاً واحداً فقط يعبر عن بيانات المنبه (Clean Code)
     */
    fun schedule(item: AlarmSchedulerItem) {
        val intent = createAlarmIntent(item.message)
        val requestCode = generateRequestCode()

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        setExactAlarm(item.triggerTimeMillis, pendingIntent)
    }

    private fun createAlarmIntent(message: String): Intent {
        return Intent(context, MyReceiver::class.java).apply {
            putExtra("extra_data", true)
            putExtra("massage", message)
        }
    }

    private fun generateRequestCode(): Int {
        val prefs = context.getSharedPreferences("alarm_prefs", Context.MODE_PRIVATE)
        val nextCode = prefs.getInt("counter", 0) + 1
        prefs.edit().putInt("counter", nextCode).apply()
        return nextCode
    }

    @SuppressLint("ScheduleExactAlarm")
    private fun setExactAlarm(triggerTimeMillis: Long, pendingIntent: PendingIntent) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerTimeMillis,
                pendingIntent
            )
        } else {
            alarmManager.setExact(
                AlarmManager.RTC_WAKEUP,
                triggerTimeMillis,
                pendingIntent
            )
        }
    }
}