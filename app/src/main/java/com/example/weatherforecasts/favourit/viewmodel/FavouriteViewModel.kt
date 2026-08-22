package com.example.weatherforecasts.favourit.viewmodel


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherforecasts.model.Fav

import com.example.weatherforecasts.model.RepositoryInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FavouriteViewModel(private val repo: RepositoryInterface) : ViewModel() {
    private val _cityName = MutableLiveData<List<Fav>>()
    val cityName: LiveData<List<Fav>> = _cityName

    init {
        getAllLCityName()
    }

    fun getAllLCityName() {
        viewModelScope.launch(Dispatchers.IO) {
            repo.getAllCityName().collect {

                _cityName.postValue(it)

            }
        }
    }

    suspend fun saveNameLoc(cityName: Fav) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.insertCityName(cityName)
        }.join()

    }

    fun deleteNameLoc(cityName: Fav) {
        viewModelScope.launch(Dispatchers.IO) {

            repo.deleteCityName(cityName)
        }

    }
}