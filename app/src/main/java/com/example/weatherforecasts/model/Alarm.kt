package com.example.weatherforecasts.model

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.*

@Entity(
    tableName = "Alarm_table", foreignKeys = [
        ForeignKey(
            entity = Location::class,
            parentColumns = ["idLoc"], // Primary key in Location table
            childColumns = ["location_id"], // Foreign key in Alarm_table
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )

    ],
    indices = [Index(value = ["location_id", "textFromTime", "textToTime"], unique = true)]

)


data class Alarm @RequiresApi(Build.VERSION_CODES.O) constructor(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    var textDateFrom: String = "",

    var location_id: Int = 0,//foreign key

    var textDateTo: String = "",
    var textFromTime: String = "",
    var textToTime: String = "",
    var localDateFrom: LocalDate = LocalDate.now(),
    var localDateTo: LocalDate = LocalDate.now(),
    var localTimeFrom: LocalTime = LocalTime.now(),
    var localTimeTo: LocalTime = LocalTime.now()


)
