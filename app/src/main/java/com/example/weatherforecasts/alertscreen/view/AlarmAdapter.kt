package com.example.weatherforecasts.alertscreen.view

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherforecasts.R
import com.example.weatherforecasts.model.Alarm
import java.text.SimpleDateFormat
import java.util.*

class AlarmAdapter(private val alarm: SendData, val cxt: Context) :
    ListAdapter<Alarm, AlarmAdapter.ViewHolder>(AlarmDiffUtil()) {

    fun convertEnglishToArabicTime(input: String): String {
        val englishToArabicDigits = mapOf(
            '0' to '٠', '1' to '١', '2' to '٢', '3' to '٣', '4' to '٤',
            '5' to '٥', '6' to '٦', '7' to '٧', '8' to '٨', '9' to '٩'
        )

        val amPmMap = mapOf("AM" to "ص", "PM" to "م")

        var output = input.map { char -> englishToArabicDigits[char] ?: char }.joinToString("")

        // Convert AM/PM to Arabic
        amPmMap.forEach { (english, arabic) ->
            output = output.replace(english, arabic)
        }

        return output
    }

    inner class ViewHolder(itemview: View) : RecyclerView.ViewHolder(itemview) {

        var textDateTo: TextView = itemview.findViewById(R.id.displayItemDateAlarmTo)
        var textDateFrom: TextView = itemview.findViewById(R.id.displayItemDateAlarmFrom)
        var textTimeTo: TextView = itemview.findViewById(R.id.displayItemAlarmTimeTo)
        var textTimeForm: TextView = itemview.findViewById(R.id.displayItemTimeAlarmFrom)
        var imgRemove: ImageView = itemview.findViewById(R.id.imgRemove)


    }

    val prefrence =
        cxt.getSharedPreferences("PREFERENCE_NAME", Context.MODE_PRIVATE)

    val editor = prefrence.edit()

    val language = prefrence.getString("lang", "en")
    val locale = if (language == "ar") Locale("ar", "EG") else Locale("en", "US")


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.row_alarm, parent, false)
        return ViewHolder(view)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentObj = getItem(position)

        val locale = Locale(language) // or based on user's language
        val sdf = SimpleDateFormat("hh:mm a", locale)
        val dateFormat = SimpleDateFormat("dd - MM - yyyy", locale)//("dd-mm-yyyy", locale)
        var timeFromStr = ""
        var timeToStr = ""
        var dateFromStr = ""
        var dateToStr = ""
        try {
            // Convert stored Arabic time string to English-compatible format
            if (language == "ar") {
                timeFromStr = convertEnglishToArabicTime(currentObj.textFromTime)
                timeToStr = convertEnglishToArabicTime(currentObj.textToTime)

                dateFromStr = convertEnglishToArabicTime(currentObj.textDateFrom)
                dateToStr = convertEnglishToArabicTime(currentObj.textDateTo)
            }


            val parsedTimeFrom = sdf.parse(timeFromStr)
            val parsedTimeTo = sdf.parse(timeToStr)

            val formattedTimeFrom = sdf.format(parsedTimeFrom!!)
            val formattedTimeTo = sdf.format(parsedTimeTo!!)


            val parsedDateFrom = dateFormat.parse(dateFromStr)
            val parsedDateTo = dateFormat.parse(dateToStr)

            val formatteDateFrom = dateFormat.format(parsedDateFrom!!)
            val formatteDateTo = dateFormat.format(parsedDateTo!!)

            holder.textTimeForm.text = formattedTimeFrom
            holder.textTimeTo.text = formattedTimeTo
            // holder.title.text = currentObj.title
            holder.textDateFrom.text = formatteDateFrom//currentObj.textDateFrom
            holder.textDateTo.text = formatteDateTo //currentObj.textDateTo
        } catch (e: Exception) {
            Log.e("AlarmAdapter", "Time parse error: ${e.message}")
            holder.textTimeForm.text = currentObj.textFromTime
            holder.textTimeTo.text = currentObj.textToTime
            holder.textDateFrom.text = currentObj.textDateFrom
            holder.textDateTo.text = currentObj.textDateTo

        }

        holder.imgRemove.setOnClickListener {

            val builder = AlertDialog.Builder(cxt)
                .setTitle(cxt.getString(R.string.delete_alarm_title))
                .setMessage(cxt.getString(R.string.delete_alarm_message))
                .setPositiveButton(cxt.getString(R.string.yes)) { _, _ ->
                    alarm.sendAlarm(currentObj)
                }
                .setNegativeButton(cxt.getString(R.string.cancel), null)
            builder.show()
        }
    }

}

class AlarmDiffUtil() : DiffUtil.ItemCallback<Alarm>() {
    override fun areItemsTheSame(oldItem: Alarm, newItem: Alarm): Boolean {
        return oldItem === newItem
    }

    override fun areContentsTheSame(oldItem: Alarm, newItem: Alarm): Boolean {
        return oldItem == newItem


    }


}

