package com.example.weatherforecasts.homescreen.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherforecasts.model.Alarm
import com.example.weatherforecasts.model.Alert
import com.example.weatherforecasts.model.Location
import com.example.weatherforecasts.model.RepositoryInterface
import com.example.weatherforecasts.network.ApiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch


class HomeViewModel(private val repo: RepositoryInterface) : ViewModel() {

    private var _weather = MutableStateFlow<ApiState>(ApiState.Loading)
    val weather = _weather
    private var _homeLocation = MutableLiveData<Pair<Double, Double>>()
    val homeLocation: LiveData<Pair<Double, Double>> get() = _homeLocation

    private var _favLocation = MutableLiveData<Pair<Double, Double>>()
    val favLocation: LiveData<Pair<Double, Double>> get() = _favLocation

    fun setHomeLocation(lat: Double, lon: Double) {
        _homeLocation.value = Pair(lat, lon)
    }

    fun setFavLocation(lat: Double, lon: Double) {
        _favLocation.value = Pair(lat, lon)
    }


    fun getNetworkWeather(lat: Double, lon: Double, unit: String, lang: String) =
        viewModelScope.launch(Dispatchers.IO) {

            try {
                val response = repo.getFromNetwork(lat, lon, unit, lang).collect {
                    Log.i("testt", "$it")
                    _weather.value = ApiState.Success(it)

                }

            } catch (e: Throwable) {
                _weather.value = ApiState.Failure(e)
            }

        }


    fun deletealertapii(alert: List<Alert>) {

        viewModelScope.launch(Dispatchers.IO) {

        }
    }


}

