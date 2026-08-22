package com.example.weatherforecasts.alertscreen.view

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.ViewModelProvider
import com.example.weatherforecasts.R
import com.example.weatherforecasts.alertscreen.viewmodel.AlertViewModel
import com.example.weatherforecasts.alertscreen.viewmodel.AlertViewModelFactory
import com.example.weatherforecasts.model.ConcreteLocalSource
import com.example.weatherforecasts.model.Repository
import com.example.weatherforecasts.network.WeatherClient

class MyReceiver : BroadcastReceiver() {


    override fun onReceive(context: Context, intent: Intent) {
        val data = intent.getBooleanExtra("extra_data", false)
        val massage = intent.getStringExtra("massage")
        val channelId = "alarm_id"


        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val message = if (data) {
            massage
        } else {
            context.getString(R.string.no_alert_weather)

            //"No Alert Weather is Fine "
        }

        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Alarm Demo")
            .setContentText(message)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(massage)
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)


        notificationManager.notify(1, builder.build())


        val channelName = "alarm_name"

        val channel = NotificationChannel(
            channelId,
            channelName,
            NotificationManager.IMPORTANCE_HIGH
        )
        notificationManager.createNotificationChannel(channel)
    }

}