package com.example.weatherforecasts.model

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import org.jetbrains.annotations.NotNull

@Dao
interface AlarmDao {


    @Query("SELECT * FROM Alarm_table_API WHERE location_id = :locationId")
    fun getAlertsAPIByLocation(locationId: Int): Flow<List<Alert>>

    @Query("SELECT * FROM Alarm_table WHERE location_id = :locationId")
    fun getAlarmsUserByLocation(locationId: Int): Flow<List<Alarm>>

    @Query("SELECT idLoc FROM Locations")
    fun getAllLocationIds(): Flow<List<Int>>

    @Query("SELECT * FROM Fav_table")
    fun getAllCityName(): Flow<List<Fav>>


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    @NotNull
    suspend fun insertLocation(loc: Location): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    @NotNull
    suspend fun insertCityName(cityNameFav: Fav): Long


    @Delete
    @NotNull
    suspend fun deleteCityName(cityNameFav: Fav)
    @Delete
    @NotNull
    suspend fun deleteLocation(loc: Location)
    @Query("SELECT * FROM Locations WHERE idLoc = :locationId")
    suspend fun getLocationById(locationId: Int): Location


    // Checks if a location already exists nearby (within ~300 meters) to avoid duplicate entries
    // // Prevents the app from treating minor GPS coordinate fluctuations
    // as a new location and creating duplicate records.
    @Query(
        """
    SELECT idLoc FROM Locations 
    WHERE lat BETWEEN :lat - 0.003 AND :lat + 0.003 
      AND lon BETWEEN :lon - 0.003 AND :lon + 0.003 
    LIMIT 1
"""
    )
    suspend fun getLocationById(lat: Double, lon: Double): Int?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    @NotNull
    suspend fun insertAlarm(alarm: Alarm): Long

    @Delete
    suspend fun deleteAlarm(alarm: Alarm)
    @Delete
    suspend fun deleteAlarmApi(alert: Alert)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    @NotNull
    suspend fun insertAlarmAApi(alert: List<Alert>)


}