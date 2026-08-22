package com.example.weatherforecasts.network

import com.example.weatherforecasts.model.WeatherData
import kotlinx.coroutines.flow.Flow


// not use flow in newtwork
interface RemoteSource {

    suspend fun getNetworkWeather(
        lat: Double,
        long: Double,
        unit: String,
        lang: String
    ): Flow<WeatherData>

}

