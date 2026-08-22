package com.example.weatherforecasts.model

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import org.jetbrains.annotations.NotNull

interface LocalSource {

    suspend fun delete(alarm: Alarm)
    suspend fun deleteAlertApi(alert: Alert)
    suspend fun insert(alarm: Alarm): Long
    suspend fun insertAlertApiLocal(alert: List<Alert>)

    fun getStoredAlarmUserByLoc(idLoc: Int): Flow<List<Alarm>>
    fun getStoredAlertAPIByLoc(idLoc: Int): Flow<List<Alert>>

    fun getAllLocationIds(): Flow<List<Int>>
    suspend fun insertLocation(loc: Location): Long
    suspend fun deleteLocation(loc: Location)
    suspend fun getLocationById(locationId: Int): Location
    suspend fun getLocationById(lat: Double, lon: Double): Int?

    suspend fun getAllCityName(): Flow<List<Fav>>

    suspend fun insertCityName(cityName: Fav)
    suspend fun deleteCityName(cityName: Fav)


}