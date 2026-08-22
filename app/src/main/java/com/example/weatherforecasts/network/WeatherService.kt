package com.example.weatherforecasts.network

import com.example.weatherforecasts.model.WeatherData
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherService {

    @GET("onecall")
    suspend fun getWeatherData(
//8084bd2c7fd59779ef2676212ff216e8
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("units") units: String,

        @Query("lang") lan: String = "en",
        //   @Query("appid") appid:String ="07499aba09bc7f760e6f15af565f5b13"
        @Query("appid") appid: String = "e315959fbe479848d4ca3ee9d1301721"//"8084bd2c7fd59779ef2676212ff216e8"


    ): WeatherData


}