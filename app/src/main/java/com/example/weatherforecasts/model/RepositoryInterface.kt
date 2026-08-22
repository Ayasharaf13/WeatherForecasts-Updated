package com.example.weatherforecasts.model

import kotlinx.coroutines.flow.Flow


interface RepositoryInterface {

    suspend fun getFromNetwork(
        lat: Double,
        lon: Double,
        unit: String,
        lang: String
    ): Flow<WeatherData>

    fun getStoredAlarmUserByLoc(idLoc: Int): Flow<List<Alarm>>
    fun getStoredAlertApiByLoc(idLoc: Int): Flow<List<Alert>>
    suspend fun delete(alarm: Alarm)
    suspend fun deleteAlertApi_repo(alert: Alert)
    suspend fun insert(alarm: Alarm): Long
    suspend fun insertAlertApi(alert: List<Alert>)

    suspend fun insertLocation(loc: Location): Long
    suspend fun deleteLocation(loc: Location)
    suspend fun getLocationById(locationId: Int): Location

    fun getAllLocationIds(): Flow<List<Int>>
    suspend fun getLocationById(lat: Double, lon: Double): Int?

    suspend fun insertCityName(cityName: Fav)
    suspend fun deleteCityName(cityName: Fav)

    suspend fun getAllCityName(): Flow<List<Fav>>


}