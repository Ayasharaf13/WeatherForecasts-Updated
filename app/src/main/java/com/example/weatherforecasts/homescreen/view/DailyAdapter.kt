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
import coil.ImageLoader
import coil.imageLoader
import coil.load
import coil.request.CachePolicy
import coil.request.ImageRequest
//import com.bumptech.glide.Glide
import com.example.weatherforecasts.R
import com.example.weatherforecasts.model.Daily
import com.example.weatherforecasts.model.LocalSource
import com.example.weatherforecasts.utilise.loadIconDirectly
import okhttp3.Dns
import okhttp3.OkHttpClient
import java.net.InetAddress
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*


class DailyAdapter(tempUnit: String, context: Context) :
    ListAdapter<Daily, DailyAdapter.ViewHolder>(DailyDiffUtil()) {

    var tempWithUnit = tempUnit
    val prefrence = context.getSharedPreferences("PREFERENCE_NAME", Context.MODE_PRIVATE)
    val editor = prefrence.edit()
    val language = prefrence.getString("lang", "en")

    inner class ViewHolder(itemview: View) : RecyclerView.ViewHolder(itemview) {

        var tvDayOfWeek: TextView = itemview.findViewById(R.id.tvDayOfWeek)
        var imgDay: ImageView = itemview.findViewById(R.id.imgDay)
        var weatherState: TextView = itemview.findViewById(R.id.weatherState)
        var tvTempDay: TextView = itemview.findViewById(R.id.tvTempDay)

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.row_daily, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("SimpleDateFormat")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentObj = getItem(position)

        val icon = currentObj.weather.getOrNull(0)?.icon ?: "01d"
        val iconurl = "https://openweathermap.org/img/wn/01d@2x.png"
        val originalUrl = "https://openweathermap.org/img/wn/$icon@2x.png"
        val unblockedIconUrl = "https://wsrv.nl/?url=$originalUrl"

// Uses wsrv.nl image proxy to bypass regional ISP/DNS blocking of openweathermap.org domains.
// The proxy fetches the icon server-side, ensuring weather icons render reliably on all networks.

        holder.imgDay.load(unblockedIconUrl) {

            addHeader("User-Agent", "Mozilla/5.0 (Linux; Android 10; Mobile)")
            crossfade(true)
            placeholder(R.drawable.ic_launcher_background)
            error(R.drawable.ic_launcher_background)

            listener(onSuccess = { _, _ ->
                Log.d("COIL_SUCCESS", "Done! Image loaded via Image Proxy")
            }, onError = { _, result ->
                Log.e("COIL_ERROR", "Error: ${result.throwable.message}", result.throwable)
            })
        }

        Log.d("WEATHER_DEBUG", "Icon value: '$icon' | Unblocked URL: '$unblockedIconUrl'")


        Log.d("WEATHER_DEBUGTry", "Icon value: '$icon' | Full URL: '$iconurl'")


        val time_data = currentObj.dt

        val date: Date = Date(time_data * 1000L); // *1000 is to convert seconds to milliseconds
        val sdf: SimpleDateFormat =
            SimpleDateFormat("EEEE", Locale(language, "EG")); // the format of your date
        sdf.setTimeZone(TimeZone.getTimeZone("GMT-4"))
        holder.tvDayOfWeek.text = sdf.format(date)

        val temDay = currentObj.temp.day
        val locale = Locale(language, "EG")
        val nf = NumberFormat.getInstance(locale)
        val tempDaychangelang = nf.format(temDay)
        holder.tvTempDay.text = tempDaychangelang.toString() + tempWithUnit


        holder.weatherState.text = currentObj.weather.get(0).description


    }


    class DailyDiffUtil() : DiffUtil.ItemCallback<Daily>() {
        override fun areItemsTheSame(oldItem: Daily, newItem: Daily): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Daily, newItem: Daily): Boolean {
            return oldItem == newItem
        }


    }
}