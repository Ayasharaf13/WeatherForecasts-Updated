package com.example.weatherforecasts.favourit.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weatherforecasts.model.RepositoryInterface

class FavoriteViewModelFactory(private val repo: RepositoryInterface) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(FavouriteViewModel::class.java)) {
            FavouriteViewModel(repo) as T

        } else {
            throw IllegalArgumentException("View Model is not found")
        }


    }


}
