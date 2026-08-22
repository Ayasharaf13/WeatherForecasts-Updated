package com.example.weatherforecasts.model

import android.annotation.SuppressLint
import android.content.Context
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow

class ConcreteLocalSource : LocalSource {

    val context: Context
    lateinit var storelist: Flow<List<Alarm>>
    lateinit var storelistApi: Flow<List<Alert>>
    val alarmDao: AlarmDao

    companion object {
        @SuppressLint("StaticFieldLeak")
        private var localsource: ConcreteLocalSource? = null

        fun getInstance(con: Context): ConcreteLocalSource {
            if (localsource == null) {
                localsource = ConcreteLocalSource(con)
            }
            return localsource as ConcreteLocalSource
        }

    }

    private constructor (con: Context) {

        this.context = con
        val db: AppDataBase = AppDataBase.getInstance(context.applicationContext)
        alarmDao = db.getProdDao()


    }


    override suspend fun delete(alarm: Alarm) {
        alarmDao.deleteAlarm(alarm)
    }

    override suspend fun deleteAlertApi(alert: Alert) {
        alarmDao.deleteAlarmApi(alert)
    }

    override suspend fun insert(alarm: Alarm): Long {

        return alarmDao.insertAlarm(alarm)
    }

    override suspend fun insertAlertApiLocal(alert: List<Alert>) {
        return alarmDao.insertAlarmAApi(alert)
    }

    override fun getStoredAlarmUserByLoc(idLoc: Int): Flow<List<Alarm>> = flow {

        emitAll(alarmDao.getAlarmsUserByLocation(idLoc))
    }

    override fun getStoredAlertAPIByLoc(idLoc: Int): Flow<List<Alert>> = flow {

        emitAll(alarmDao.getAlertsAPIByLocation(idLoc))

    }

    override fun getAllLocationIds(): Flow<List<Int>> = flow {

        emitAll(alarmDao.getAllLocationIds())

    }


    override suspend fun insertLocation(loc: Location): Long {
        return alarmDao.insertLocation(loc)
    }

    override suspend fun deleteLocation(loc: Location) {

        alarmDao.deleteLocation(loc);
    }

    override suspend fun getLocationById(locationId: Int): Location {

        return alarmDao.getLocationById(locationId)

    }

    override suspend fun getLocationById(lat: Double, lon: Double): Int? {
        return alarmDao.getLocationById(lat, lon)
    }

    override suspend fun getAllCityName(): Flow<List<Fav>> = flow {


        emitAll(alarmDao.getAllCityName())
    }

    override suspend fun insertCityName(cityName: Fav) {

        alarmDao.insertCityName(cityName)
    }

    override suspend fun deleteCityName(cityName: Fav) {

        alarmDao.deleteCityName(cityName)


    }


}