package com.example.weatherforecasts.network

import com.example.weatherforecasts.model.WeatherData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object RetrofitClient {
    private const val BASE_URL = "https://api.openweathermap.org/data/3.0/"

    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    }
}

class WeatherClient() : RemoteSource {

    companion object {
        var latt: Double = 0.0
        var lonn: Double = 0.0
        var language = ""
        private var remoteSource: RemoteSource? = null

        fun getInstance(): WeatherClient {
            if (remoteSource == null) {
                remoteSource = WeatherClient()
            }
            return remoteSource as WeatherClient
        }
    }


    val apiService: WeatherService by lazy {
        RetrofitClient.retrofit.create(WeatherService::class.java)


    }

    override suspend fun getNetworkWeather(
        lat: Double,
        lon: Double,
        unit: String,
        lang: String
    ): Flow<WeatherData> = flow {
        latt = lat
        lonn = lon
        language = lang

        emit(apiService.getWeatherData(lat, lon, unit, lang))


    }


}



