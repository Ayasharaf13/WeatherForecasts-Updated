package com.example.weatherforecasts


import android.content.pm.PackageManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.Manifest
import android.annotation.SuppressLint
import android.app.LocaleManager
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.os.Build
import android.os.LocaleList
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.widget.ProgressBar
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.example.weatherforecasts.homescreen.view.HomeActivity
import com.google.android.gms.location.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.lang.reflect.Modifier.*
import java.util.*


class SplashFragment : Fragment() {
    val PERMISSIONID = 13
    var progress = 5
    lateinit var progressBar: ProgressBar
    lateinit var mlocationCallback: LocationCallback
    lateinit var fusedLocation: FusedLocationProviderClient





    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
         Log.i("ssssssplashfraggggg","ssssssplash")
        }
        val prefrence =
            requireActivity().getSharedPreferences("PREFERENCE_NAME", Context.MODE_PRIVATE)

        val editor = prefrence.edit()
        editor.apply()
        val language = prefrence.getString("lang","en")
        if (language != null) {
            updateConfiggg(language)
        }

    }





    @SuppressLint("SuspiciousIndentation")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {


        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_splash, container, false)

        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fusedLocation = LocationServices.getFusedLocationProviderClient(requireActivity())
        progressBar = view.findViewById(R.id.progressBar2)



        mlocationCallback = object : LocationCallback() {

            @SuppressLint("SetTextI18n", "SuspiciousIndentation")
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                val lastLocation = locationResult.lastLocation


              viewLifecycleOwner. lifecycleScope.launch(Dispatchers.Main) {
                    while (progress < 100) {
                        progress += 5 // Increase by 5
                        progressBar.progress = progress
                        delay(50) // Wait 100ms

                    }


                    if (lastLocation != null) {
                        var lat = lastLocation.latitude.toString()
                        var lon = lastLocation.longitude.toString()

                        val action =
                            SplashFragmentDirections.actionSplashFragmentToHomeFragment(lat, lon)
                        Navigation.findNavController(view).navigate(action)

                    }

                }


            }


        }
    }




    fun checkPermission(): Boolean {
        val result = ActivityCompat.checkSelfPermission(
            requireActivity(), Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
            requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        return result
    }


    override fun onResume() {
        super.onResume()
        getLastLocation()

    }


    fun requestPermissions() {
        ActivityCompat.requestPermissions(
            requireActivity(), arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION
            ), PERMISSIONID
        )
    }

    fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            context?.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )

    }


    fun getLastLocation() {

        if (checkPermission()) {
            if (isLocationEnabled()) {
                requestNewLocationData()

            } else {

                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }

        } else {
            requestPermissions()
        }

    }


    @SuppressLint("MissingPermission")
    private fun requestNewLocationData() {
        val mlocationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5000)
            .setMinUpdateDistanceMeters(1000f)
            .build()

        fusedLocation.requestLocationUpdates(mlocationRequest, mlocationCallback, Looper.myLooper())

    }

    override fun onDestroyView() {
        super.onDestroyView()
        fusedLocation.removeLocationUpdates(mlocationCallback) // ✅ Stop updates when fragment is destroyed

    }



    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) = SplashFragment().apply {
            arguments = Bundle().apply {

            }

        }

    }

    fun updateConfiggg(lang:String) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val localeManager = requireContext().getSystemService(LocaleManager::class.java)
            localeManager.applicationLocales = LocaleList.forLanguageTags(lang)

        }else {
            val locale = Locale(lang)
            val res = resources
            val dm = res.displayMetrics
            val conf = res.configuration
            // conf.locale = locale
            conf.setLocale(locale)
            //   requireActivity().createConfigurationContext(conf)
            activity?.baseContext?.resources?.updateConfiguration(
                conf,
                activity?.baseContext?.resources?.displayMetrics
            )




        }

   }



}