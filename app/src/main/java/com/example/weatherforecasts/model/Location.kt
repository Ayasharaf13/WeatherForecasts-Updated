package com.example.weatherforecasts.model


import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "Locations",
    indices = [Index(value = ["lat", "lon"], unique = true)]
)// Prevent duplicate locations

data class Location(
    @PrimaryKey(autoGenerate = true)
    val idLoc: Int = 0, // Auto-generated primary key
    val timezone: String = "", // Location name (e.g., city or area)

    var lat: Double = 0.0, // Latitude coordinate

    var lon: Double = 0.0, // Longitude coordinate
)
