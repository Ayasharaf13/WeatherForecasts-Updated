package com.example.weatherforecasts.settingscreen.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch


class SettingViewModel : ViewModel() {

    private var _sharedFlow = MutableSharedFlow<String>()
    val sharedFlow = _sharedFlow.asSharedFlow()


    fun setSharedFlow(lang: String) {
        viewModelScope.launch {

            if (lang.isNotBlank()) {
                _sharedFlow.emit(lang)
            }

        }

    }
}
