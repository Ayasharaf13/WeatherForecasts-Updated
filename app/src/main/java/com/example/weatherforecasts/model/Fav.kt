package com.example.weatherforecasts.model


import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Fav_table")
data class Fav(
    @PrimaryKey(autoGenerate = true)
    val idCityName: Int = 0,
    var locationName: String = "Albania",
    var latFav: Double = 0.0,
    var lonFav: Double = 0.0

)



