package com.example.weatherforecasts



import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import java.util.*
import android.app.LocaleManager
import android.os.Build
import android.os.LocaleList
import android.util.Log
import android.widget.Button
import com.example.weatherforecasts.homescreen.viewmodel.HomeViewModel
import com.example.weatherforecasts.homescreen.viewmodel.HomeViewModelFactory
import com.google.android.gms.location.*



class
MainActivity : AppCompatActivity() {

    lateinit var btn: Button
    val PERMISSIONID = 13

    lateinit var fusedLocation: FusedLocationProviderClient
    lateinit var homeViewmodel: HomeViewModel
    lateinit var homefactory: HomeViewModelFactory

    @SuppressLint("MissingInflatedId", "IntentReset")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Log.i("Mainnnnnnnnnnn", "maninactocityyyyy")
        updateConfiggg("ar")

    }


    fun updateConfiggg(lang: String) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val localeManager = this.getSystemService(LocaleManager::class.java)
            localeManager.applicationLocales = LocaleList.forLanguageTags(lang)

        } else {
            val locale = Locale(lang)
            val res = resources
            val dm = res.displayMetrics
            val conf = res.configuration

            conf.setLocale(locale)
            this.baseContext.resources.updateConfiguration(
                conf,
                this.baseContext.resources.displayMetrics
            )

            this.apply {
                val intent = intent

            }
        }

    }

}























