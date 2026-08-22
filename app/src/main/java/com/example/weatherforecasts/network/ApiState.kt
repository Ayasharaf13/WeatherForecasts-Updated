package com.example.weatherforecasts.network


import com.example.weatherforecasts.model.WeatherData

sealed class ApiState {

    class Success(val data: WeatherData) : ApiState()
    class Failure(val msg: Throwable) : ApiState()
    object Loading : ApiState()


}
