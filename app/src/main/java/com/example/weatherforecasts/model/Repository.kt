package com.example.weatherforecasts.model

import com.example.weatherforecasts.network.RemoteSource
import kotlinx.coroutines.flow.Flow


class Repository(remoteSource: RemoteSource, localSource: LocalSource) : RepositoryInterface {
    val remoteSource = remoteSource
    val localSource = localSource


    override suspend fun getFromNetwork(
        lat: Double,
        lon: Double,
        unit: String,
        lang: String
    ): Flow<WeatherData> {


        return remoteSource.getNetworkWeather(lat, lon, unit, lang)

    }

    override fun getStoredAlarmUserByLoc(idLoc: Int): Flow<List<Alarm>> {
        return localSource.getStoredAlarmUserByLoc(idLoc)

    }

    override fun getStoredAlertApiByLoc(idLoc: Int): Flow<List<Alert>> {

        return localSource.getStoredAlertAPIByLoc(idLoc)

    }


    override suspend fun delete(alarm: Alarm) {
        localSource.delete(alarm)
    }

    override suspend fun deleteAlertApi_repo(alert: Alert) {
        localSource.deleteAlertApi(alert)
    }

    override suspend fun insert(alarm: Alarm): Long {
        return localSource.insert(alarm)
    }

    override suspend fun insertAlertApi(alert: List<Alert>) {
        return localSource.insertAlertApiLocal(alert)
    }


    override suspend fun insertLocation(loc: Location): Long {
        return localSource.insertLocation(loc)
    }

    override suspend fun deleteLocation(loc: Location) {

        localSource.deleteLocation(loc)
    }

    override suspend fun getLocationById(locationId: Int): Location {
        return localSource.getLocationById(locationId)
    }

    override suspend fun getLocationById(lat: Double, lon: Double): Int? {

        return localSource.getLocationById(lat, lon)
    }

    override fun getAllLocationIds(): Flow<List<Int>> {

        return localSource.getAllLocationIds()
    }

    override suspend fun insertCityName(cityName: Fav) {
        localSource.insertCityName(cityName)
    }

    override suspend fun deleteCityName(cityName: Fav) {
        localSource.deleteCityName(cityName)
    }

    override suspend fun getAllCityName(): Flow<List<Fav>> {
        return localSource.getAllCityName()
    }


    companion object {

        private var instance: Repository? = null
        fun getInstance(remoteSource: RemoteSource, localSource: LocalSource): Repository {
            return instance ?: synchronized(this) {

                val temp = Repository(remoteSource, localSource)
                instance = temp
                temp
            }


        }
    }

}