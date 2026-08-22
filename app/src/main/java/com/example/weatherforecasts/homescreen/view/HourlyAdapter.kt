package com.example.weatherforecasts.homescreen.view

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.weatherforecasts.R
import com.example.weatherforecasts.model.Hourly
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*


class HourlyAdapter(tempWithUnit: String, cxt: Context) :
    ListAdapter<Hourly, HourlyAdapter.ViewHolder>(HourlyDiffUtil()) {

    var tempwithUn = tempWithUnit
    val prefrence =
        cxt.getSharedPreferences("PREFERENCE_NAME", Context.MODE_PRIVATE)
    val editor = prefrence.edit()
    val language = prefrence.getString("lang", "en")

    inner class ViewHolder(itemview: View) : RecyclerView.ViewHolder(itemview) {

        var tvTemHour: TextView = itemview.findViewById(R.id.tv_tem_hour)
        var imgeHour: ImageView = itemview.findViewById(R.id.img_hour)
        var tvHour: TextView = itemview.findViewById(R.id.tv_hour)

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.row_hourly, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("SimpleDateFormat", "SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val currentObj = getItem(position)
        val icon = currentObj.weather.getOrNull(0)?.icon
        val iconurl = "https://openweathermap.org/img/wn/$icon@2x.png"
        val locale = Locale(language, "EG")
        val originalUrl = "https://openweathermap.org/img/wn/$icon@2x.png"
        val unblockedIconUrl = "https://wsrv.nl/?url=$originalUrl"


// Uses wsrv.nl image proxy to bypass regional ISP/DNS blocking of openweathermap.org domains.
// The proxy fetches the icon server-side, ensuring weather icons render reliably on all networks.

        holder.imgeHour.load(unblockedIconUrl) {
            addHeader("User-Agent", "Mozilla/5.0 (Linux; Android 10; Mobile)")
            crossfade(true)
            placeholder(R.drawable.ic_launcher_background)
            error(R.drawable.ic_launcher_background)
            listener(
                onSuccess = { _, _ ->
                    Log.d("COIL_SUCCESS", "Done! Image loaded via Image Proxy")
                },
                onError = { _, result ->
                    Log.e("COIL_ERROR", "Error: ${result.throwable.message}", result.throwable)
                }
            )
        }


        val time_data = currentObj.dt
        val dayOfWeek = Instant.ofEpochSecond(time_data.toLong())
            .atZone(ZoneId.systemDefault()) // Convert to the local time zone
            .format(DateTimeFormatter.ofPattern("EEEE")) // "EEEE" returns full day name


        val date: Date = Date(time_data * 1000L); // *1000 is to convert seconds to milliseconds
        val sdf: SimpleDateFormat =
            SimpleDateFormat(
                "hh:mm a",
                locale
            ); // the format of your date h for 12hourseformate ,H for 24hourseFormate
        sdf.setTimeZone(TimeZone.getTimeZone("GMT-4"))
        holder.tvHour.text = sdf.format(date)

        var temp = currentObj.temp
        //   holder.tvTemHour.text = temp + tempwithUn
        // holder.unitTemp.text = "K"
        val nf = NumberFormat.getInstance(locale)
        val tempDaychangelang = nf.format(temp)
        holder.tvTemHour.text = tempDaychangelang.toString() + tempwithUn


    }

    class HourlyDiffUtil() : DiffUtil.ItemCallback<Hourly>() {
        override fun areItemsTheSame(oldItem: Hourly, newItem: Hourly): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Hourly, newItem: Hourly): Boolean {
            return oldItem == newItem
        }


    }
}

