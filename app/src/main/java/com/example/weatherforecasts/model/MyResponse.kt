package com.example.weatherforecasts.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName


@Entity(
    tableName = "Alarm_table_API",
    primaryKeys = ["sender_name", "event", "start", "end"], foreignKeys = [
        ForeignKey(
            entity = Location::class,
            parentColumns = ["idLoc"], // Primary key in Location table
            childColumns = ["location_id"], // Foreign key in Alarm_table
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ]
)
data class Alert(
    // var idApiAlarm :Int =1, // Remove or handle this separately if needed
    val sender_name: String = "",
    val event: String = "",
    val start: Int = 0,
    val end: Int = 0,
    var location_id: Int = 1,//foreign key
    val description: String = ""
)


data class Current(
    val dt: Int,
    val sunrise: Int,
    val sunset: Int,
    val temp: Double,
    val feels_like: Double,
    val pressure: Int,
    val humidity: Int,
    val dew_point: Double,
    val uvi: Float,
    val clouds: Int,
    val visibility: Int,
    val wind_speed: Double,
    val wind_deg: Int,
    val wind_gust: Double,
    val weather: List<Weather>
)


data class FeelsLike(
    val day: Double,
    val night: Double,
    val eve: Double,
    val morn: Double
)

data class Hourly(
    val dt: Int,
    val temp: Double,
    val feels_like: Double,
    val pressure: Int,
    val humidity: Int,
    val dew_point: Double,
    val uvi: Double,
    val clouds: Int,
    val visibility: Int,
    val wind_speed: Double,
    val wind_deg: Int,
    val wind_gust: Double,
    val weather: List<Weather>,
    val pop: Double,
    val rain: Rain
)

data class Minutely(

    val dt: Int,
    val precipitation: Int
)

data class Rain(
    @SerializedName("1h")
    val _1h: Double
)


data class Temp(
    val day: Double,
    val min: Double,
    val max: Double,
    val night: Double,
    val eve: Double,
    val morn: Double
)

data class Weather(
    val id: Int,
    val main: String,
    val description: String,
    val icon: String
)
