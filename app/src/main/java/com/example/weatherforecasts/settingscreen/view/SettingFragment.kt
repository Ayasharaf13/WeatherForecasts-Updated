package com.example.weatherforecasts.settingscreen.view

import android.annotation.SuppressLint
import android.content.Context
import android.os.*
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.Navigation
import com.example.weatherforecasts.R
import com.example.weatherforecasts.settingscreen.viewmodel.SettingViewModel
import kotlinx.coroutines.flow.MutableSharedFlow

import kotlinx.coroutines.launch
import java.util.*


class SettingFragment : Fragment() {
    val SELECTED_LANGUAGEK_Key = "SELECTED_LANGUAGE"
    val CHAKED_Key = "CHAKED "

    val SELECTED_LANGUAGEK_Key_AR = "SELECTED_LANGUAGE_AR"
    val CHAKED_Key_AR = "CHAKED_AR "

    lateinit var radioButtonEn: RadioButton
    lateinit var radioButtonAr: RadioButton

    lateinit var radioButtonCsTemp: RadioButton
    lateinit var radioButtonFrTemp: RadioButton
    lateinit var radioButtonKlTemp: RadioButton

    lateinit var radioButtonWindSc: RadioButton
    lateinit var radioButtonwindMH: RadioButton

    lateinit var mapRadioButton: RadioButton
    lateinit var gpsRadioButton: RadioButton

    lateinit var settingViewModel: SettingViewModel

    fun moveToActivityToSetLang() {
        requireActivity().apply {
            val intent = intent
            finish()
            startActivity(intent)
            overridePendingTransition(
                android.R.anim.fade_in,
                android.R.anim.fade_out
            ) // Smooth transition
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {


        arguments?.let {

        }
        return inflater.inflate(R.layout.fragment_setting, container, false)
    }

    @SuppressLint("SuspiciousIndentation", "UnsafeRepeatOnLifecycleDetector")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val sharedLang = MutableSharedFlow<String>()
        val sharedUnit = MutableSharedFlow<String>()

        radioButtonAr = view.findViewById(R.id.ArRadioButton)
        radioButtonEn = view.findViewById(R.id.EnRadioButton)

        radioButtonCsTemp = view.findViewById(R.id.tempCelsiusRadioButton1)
        radioButtonKlTemp = view.findViewById(R.id.tempKelvinRadioButton)
        radioButtonFrTemp = view.findViewById(R.id.tempFahreRadioButton1)

        radioButtonWindSc = view.findViewById(R.id.WindSecRadioButton1)
        radioButtonwindMH = view.findViewById(R.id.WindhourRadioButton)

        mapRadioButton = view.findViewById(R.id.MapRadioButton1)

        gpsRadioButton = view.findViewById(R.id.GPSRadioButton)




        mapRadioButton.setOnClickListener {
            Log.d("SettingFragment", "Map radio button clicked")


            val action = SettingFragmentDirections.actionSettingFragmentToLocationPickerDialog()
            Navigation.findNavController(view).navigate(action)


            Toast.makeText(requireContext(), "toast", Toast.LENGTH_SHORT).show()
        }


        settingViewModel =
            ViewModelProvider(requireActivity()).get(SettingViewModel::class.java)

        val prefrence =
            requireActivity().getSharedPreferences("PREFERENCE_NAME", Context.MODE_PRIVATE)

        val editor = prefrence.edit()
        var ischaked = true


        fun selectGps() {
            Toast.makeText(context, "Already open app by GBS", Toast.LENGTH_SHORT).show()
            editor.putBoolean("checkedGps", true)
            mapRadioButton.isChecked = false
            gpsRadioButton.isChecked = true
        }



        gpsRadioButton.setOnClickListener {
            selectGps()

        }



        fun selectbtnEn() {
            Toast.makeText(context, "checked English", Toast.LENGTH_SHORT).show()

            editor.putString("lang", "en")
            editor.putBoolean(CHAKED_Key, ischaked)
            editor.putBoolean(CHAKED_Key_AR, false)

            editor.apply()

        }


        fun selectBtnAr() {

            Toast.makeText(context, "checked Arabic", Toast.LENGTH_SHORT).show()
            editor.putString("lang", "ar")
            editor.putBoolean(CHAKED_Key_AR, ischaked)
            editor.putBoolean(CHAKED_Key, false)

            editor.apply()


        }


        fun selectTempCs() {


            editor.putString("unit", "metric")
            editor.putBoolean("chakedTempCs", true)
            editor.putBoolean("chakedTempFr", false)
            editor.putBoolean("chakedTempKl", false)

            radioButtonCsTemp.isChecked = true
            radioButtonFrTemp.isChecked = false
            radioButtonKlTemp.isChecked = false

            editor.apply()


        }

        fun selectTempFr() {


            editor.putString("unit", "imperial")
            editor.putBoolean("chakedTempFr", true)
            editor.putBoolean("chakedTempKl", false)
            editor.putBoolean("chakedTempCs", false)
            radioButtonFrTemp.isChecked = true
            radioButtonKlTemp.isChecked = false
            radioButtonCsTemp.isChecked = false
//val test =  prefrence.getBoolean("chakedTempFr" , false)
//  Log.i("Cstemp", "$test")

            editor.apply()


        }


        fun selectTempKl() {


            editor.putString("unit", "standard")
            editor.putBoolean("chakedTempFr", false)
            editor.putBoolean("chakedTempKl", false)
            editor.putBoolean("chakedTempKl", true)
            radioButtonKlTemp.isChecked = true
            radioButtonFrTemp.isChecked = false
            radioButtonCsTemp.isChecked = false


            editor.apply()


        }


        editor.putString("unit", "standard")
        editor.putBoolean("chakedTempFr", false)
        editor.putBoolean("chakedTempKl", false)
        editor.putBoolean("chakedTempKl", true)
        radioButtonKlTemp.isChecked = true
        radioButtonFrTemp.isChecked = false
        radioButtonCsTemp.isChecked = false



        fun selectWindSc() {

            editor.putString("unit", "metric")
            editor.putBoolean("chakedWindS", true)
            editor.putBoolean("chakedWindH", false)
            radioButtonWindSc.isChecked = true
            radioButtonwindMH.isChecked = false
            editor.apply()
        }


        fun selectWindMH() {

            editor.putString("unit", "imperial")
            editor.putBoolean("chakedWindH", true)
            editor.putBoolean("chakedWindS", false)

            radioButtonWindSc.isChecked = false
            radioButtonwindMH.isChecked = true

            editor.apply()
        }


        val units = prefrence.getString("unit", "standard")
        Log.i("unitTemp", "$units")
        radioButtonFrTemp.isChecked = prefrence.getBoolean("chakedTempFr", false)
        radioButtonKlTemp.isChecked = prefrence.getBoolean("chakedTempKl", false)
        radioButtonCsTemp.isChecked = prefrence.getBoolean("chakedTempCs", false)

        val chakeunitTemFr = prefrence.getBoolean("chakedTempFr", false)
        val chakeunitTemKL = prefrence.getBoolean("chakedTempKl", false)
        val chakeunitTemCs = prefrence.getBoolean("chakedTempCs", false)

        Log.i("unitTempChaked_F", "$chakeunitTemFr")
        Log.i("unitTempChaked_K", "$chakeunitTemKL")
        Log.i("unitTempChaked_C", "$chakeunitTemCs")


        lifecycleScope.launch {
            if (units != null) {
                sharedUnit.emit(units)
            }


        }

        lifecycleScope.launch {
            sharedUnit.collect { units ->

                when (units) {
                    "metric" -> radioButtonCsTemp.isChecked = true
                    "imperial" -> radioButtonFrTemp.isChecked = true
                    "standard" -> radioButtonKlTemp.isChecked = true

                }


            }
        }

        radioButtonwindMH.isChecked = prefrence.getBoolean("chakedWindH", false)
        radioButtonWindSc.isChecked = prefrence.getBoolean("chakedWindS", false)


        gpsRadioButton.isChecked = prefrence.getBoolean("checkedGps", true)

        radioButtonEn.isChecked = prefrence.getBoolean(CHAKED_Key, false)
        radioButtonAr.isChecked = prefrence.getBoolean(CHAKED_Key_AR, false)

        val langEn = prefrence.getString(SELECTED_LANGUAGEK_Key, " ")// en //        //en " "
        val langAr = prefrence.getString(SELECTED_LANGUAGEK_Key_AR, " ")// ar " "    // ar


        if (langAr != null && langEn != null) {
            val activeLang = when {
                langAr.isNotBlank() -> "ar"
                langEn.isNotBlank() -> "en"
                else -> "en"
            }



            lifecycleScope.launch {
                lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    sharedLang.collect { lang ->
                        Log.i("language", lang)
                        when (lang) {

                            "ar" -> selectBtnAr()
                            "en" -> selectbtnEn()

                        }
                    }
                }
            }


            if (activeLang.isNotBlank()) {
                Log.i("Activelanguage", activeLang)
                lifecycleScope.launch {
                    sharedLang.emit(activeLang)
                }
            }


        }

        radioButtonEn.setOnClickListener {
            selectbtnEn()
            moveToActivityToSetLang()
        }

        radioButtonAr.setOnClickListener {
            selectBtnAr()
            moveToActivityToSetLang()
        }

        radioButtonFrTemp.setOnClickListener {
            Toast.makeText(context, "checked Fr", Toast.LENGTH_SHORT).show()
            selectTempFr()


        }

        radioButtonKlTemp.setOnClickListener {
            Toast.makeText(context, "checked KL", Toast.LENGTH_SHORT).show()
            selectTempKl()


        }

        radioButtonWindSc.setOnClickListener {

            selectWindSc()

        }


        radioButtonwindMH.setOnClickListener {
            selectWindMH()
        }

        radioButtonCsTemp.setOnClickListener {
            Toast.makeText(context, "checked CS", Toast.LENGTH_SHORT).show()
            selectTempCs()

        }


    }


    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SettingFragment().apply {
                arguments = Bundle().apply {

                }

            }
    }


}











































































































































































































































































































































































































































































































































































































































































































































































































































































