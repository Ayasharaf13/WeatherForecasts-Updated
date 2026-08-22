package com.example.weatherforecasts.homescreen.view

import android.annotation.SuppressLint
import android.app.LocaleManager
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.os.LocaleList
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherforecasts.R
import com.example.weatherforecasts.alertscreen.viewmodel.AlertViewModel
import com.example.weatherforecasts.alertscreen.viewmodel.AlertViewModelFactory
import com.example.weatherforecasts.homescreen.viewmodel.HomeViewModel
import com.example.weatherforecasts.homescreen.viewmodel.HomeViewModelFactory
import com.example.weatherforecasts.model.Alert
import com.example.weatherforecasts.model.ConcreteLocalSource
import com.example.weatherforecasts.model.Location
import com.example.weatherforecasts.model.Repository
import com.example.weatherforecasts.network.ApiState
import com.example.weatherforecasts.network.WeatherClient
import com.example.weatherforecasts.settingscreen.viewmodel.SettingViewModel
import com.example.weatherforecasts.utilise.NetworkMonitor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
//this website to get alert for location
//https://www.weather.gov/
class HomeFragment : Fragment(), NetworkMonitor.NetworkStatusListener {
    val arg: HomeFragmentArgs by navArgs()


    private var layoutMainContent: ConstraintLayout? = null
    private var layoutNoInternet: LinearLayout? = null
    private var btnRetry: Button? = null

    private lateinit var networkMonitor: NetworkMonitor

    lateinit var settingViewModel: SettingViewModel
    lateinit var location: Location

    lateinit var recyclerViewDaily: RecyclerView
    lateinit var homeAdapterDaily: DailyAdapter
    lateinit var recyclerViewHour: RecyclerView
    lateinit var hourlyAdapter: HourlyAdapter
    lateinit var tvCity: TextView
    lateinit var tvTemp: TextView
    lateinit var tvSky: TextView
    lateinit var tvdata: TextView

    lateinit var imgIconCloud: ImageView
    lateinit var imgeIconHum: ImageView
    lateinit var imgeIconPress: ImageView
    lateinit var imageWind: ImageView
    lateinit var tvHum: TextView
    lateinit var tvCloud: TextView
    lateinit var tvPress: TextView
    lateinit var tvWind: TextView
    lateinit var btnFarward: ImageButton
    lateinit var btnBack: ImageButton
    lateinit var viewModel: HomeViewModel
    lateinit var allFactory: HomeViewModelFactory

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        arguments?.let {


            Log.i("Loding wait", "Loddinggggggggggggggggggg")


        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        val view = inflater.inflate(R.layout.fragment_home, container, false)
        return view
    }

    @SuppressLint("SetTextI18n", "SuspiciousIndentation", "SimpleDateFormat")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Retrieve data from the arguments
        // val lat = arguments?.getDouble("lat")
        // val lon = arguments?.getDouble("lon")

        btnFarward = view.findViewById(R.id.imageButton)
        recyclerViewDaily = view.findViewById(R.id.recyclerViewDaily)
        recyclerViewDaily.layoutManager = LinearLayoutManager(context)

        recyclerViewHour = view.findViewById(R.id.recycler_Hourly)
        var horizontalLayoutManagaer = LinearLayoutManager(
            context, LinearLayoutManager.HORIZONTAL,
            false
        )
        recyclerViewHour.layoutManager = horizontalLayoutManagaer

        tvCity = view.findViewById(R.id.tvcity)
        tvTemp = view.findViewById(R.id.tvtemp)
        tvSky = view.findViewById(R.id.tvsky)
        tvdata = view.findViewById(R.id.tvdata_time)

        tvHum = view.findViewById(R.id.tvHum)
        tvCloud = view.findViewById(R.id.tvCloud)
        tvWind = view.findViewById(R.id.tvWind)
        tvPress = view.findViewById(R.id.tvPress)
        btnBack = view.findViewById(R.id.imageButtonBack)

        layoutMainContent = view.findViewById(R.id.layoutMainContent)
        layoutNoInternet = view.findViewById(R.id.layoutNoInternet)
        btnRetry = view.findViewById(R.id.btnRetry)

        btnRetry?.setOnClickListener { checkAndApplyConnectionState() }


        setupNetworkMonitor()

        var alertViewModelFactory = AlertViewModelFactory(
            Repository.getInstance(
                WeatherClient.getInstance(),
                ConcreteLocalSource.getInstance(requireContext())
            )
        )

        var viewModelAlert = ViewModelProvider(
            requireActivity(),
            alertViewModelFactory
        ).get(AlertViewModel::class.java)



        viewModelAlert.getAllLocation()

        val prefrence =
            requireActivity().getSharedPreferences("PREFERENCE_NAME", Context.MODE_PRIVATE)

        val editor = prefrence.edit()
        editor.apply()

        var lat = arg.latitude.toDouble() //32.7765
        var lon = arg.longitude.toDouble() //-79.9311

        Log.i("navvvvlat: ", lat.toString())
        Log.i("navvvvlon: ", lon.toString())


        val sharedPreferences =
            requireContext().getSharedPreferences("LocationPrefs", Context.MODE_PRIVATE)
        var editorLoc: SharedPreferences.Editor = sharedPreferences.edit()
        editorLoc.putString("latitude", lat.toString()) // Store latitude as float
        editorLoc.putString("longitude", lon.toString()) // Store longitude as float
        editorLoc.apply() // Apply changes


        Log.d("HomeFragment", "Received latitude: $lat, longitude: $lon")
        val allFactory =
            HomeViewModelFactory(
                Repository.getInstance(
                    WeatherClient.getInstance(),
                    ConcreteLocalSource.getInstance(requireContext())
                )
            )
        viewModel =
            ViewModelProvider(requireActivity(), allFactory).get(HomeViewModel::class.java)

        settingViewModel =
            ViewModelProvider(requireActivity()).get(SettingViewModel::class.java)
        val unitTemp = prefrence.getString("unit", "standard")
        val language = prefrence.getString("lang", "en")


        if (language != null) {
            // updateConfiggg(language)
        }

        Log.i("unittemmpllllll", "$language")
        Log.i("unittemmp", "$unitTemp")
        if (unitTemp != null) {

            if (language != null) {
                viewModel.getNetworkWeather(lat, lon, unitTemp, language)

            }


            if (arg.homeLocation == "Home") {

                viewModel.setHomeLocation(lat, lon)

            } else if (arg.homeLocation == "Fav") {
                Log.i("Homerrrrrrr", "erg.Fav")

                viewModel.setFavLocation(lat, lon)


            }



            lifecycleScope.launch(Dispatchers.IO) {


                val existingLocation = viewModelAlert.getLocById(lat, lon) // Fetch from DB
                Log.i("LocationnnnnnnnnnnHomeLat", lat.toString())
                Log.i("LocationnnnnnnnnnnHomeLong", lon.toString())
                Log.i("exussitBeforeeee", existingLocation.toString())
                if (existingLocation != null) {
                    Log.i("exussit", existingLocation.toString())
                    editorLoc.putInt("idLocation", existingLocation)

                } else {
                    // viewModelAlert.saveLoc(location)
                    editorLoc.remove("idLocation").apply()
                    //Log.i("LocationStatus", "Location not found in DB, preference cleared.")
                    val existingLocation = viewModelAlert.getLocById(lat, lon) // Fetch from DB
                    Log.i("exussitafterrrrrre", existingLocation.toString())
                    if (existingLocation != null) {
                        editorLoc.putInt("idLocation", existingLocation)
                    }

                }

                editorLoc.apply()
            }


        }


        btnBack.setOnClickListener(View.OnClickListener {
            recyclerViewHour.smoothScrollBy(-150, 0) // Adjust value to control scroll distance


        });

        btnFarward.setOnClickListener(View.OnClickListener {
            recyclerViewHour.smoothScrollBy(150, 0) // Adjust value to control scroll distance

        });


        lifecycleScope.launchWhenStarted {
            settingViewModel.sharedFlow.collect {
                Log.i("changelang", "which language$it")


            }
        }

        lifecycleScope.launch {

            viewModel.weather.collectLatest { result ->
                when (result) {
                    is ApiState.Loading -> {
                        Log.i("Loading", "Loading...")
                    }

                    is ApiState.Success -> {

                        val windSpeedMps = result.data.current.wind_speed
                        val sharedPref = requireContext().getSharedPreferences(
                            "PREFERENCE_NAME",
                            Context.MODE_PRIVATE
                        )
                        val isMph = sharedPref.getBoolean("chakedWindH", false)

                        val windText = if (isMph) {
                            "%.1f mph".format(windSpeedMps * 2.23694)
                        } else {
                            "%.1f m/s".format(windSpeedMps)
                        }

                        val nf: NumberFormat = NumberFormat.getInstance(Locale("ar", "EG"))
                        // val formatArabic = nf.format(result.data.current.temp)
                        tvCity.text = result.data.timezone

                        Log.i("WeatherResponse", result.data.daily.get(0).summary)
                        var listOfAlerts: List<Alert> = result.data.alerts

                        if (!listOfAlerts.isNullOrEmpty()) {

                            //  lifecycleScope.launch {

                            Log.i("listapi", "$listOfAlerts")
                            // }

                        } else {
                            Log.i("testsavve", "list is not set ")

                        }

                        val unitTemp = prefrence.getString("unit", "standard")
                        val language = prefrence.getString("lang", "en")

                        val displayTemp = when (unitTemp) {
                            "metric" -> {
                                val df = DecimalFormat.getInstance(Locale(language, "EG"))
                                "${df.format(result.data.current.temp)} °C"
                            }

                            "imperial" -> {
                                val df = DecimalFormat.getInstance(Locale(language, "EG"))
                                "${df.format(result.data.current.temp)} °F"
                            }

                            "standard" -> {
                                val df = DecimalFormat.getInstance(Locale(language, "EG"))
                                "${df.format(result.data.current.temp)} K"
                            }

                            else -> {
                                val df = DecimalFormat.getInstance(Locale(language, "EG"))
                                "${df.format(result.data.current.temp)} K"
                            }
                        }

                        tvTemp.text = displayTemp
                        var unitStateWeather = displayTemp.split(" ")

                        hourlyAdapter = HourlyAdapter(unitStateWeather.get(1), requireContext())
                        homeAdapterDaily = DailyAdapter(unitStateWeather.get(1), requireContext())
                        tvSky.text = result.data.current.weather.get(0).description
                        // tvdata.text// = result.data.current.dt.toString()
                        val myTime = result.data.current.dt.toLong()
                        tvHum.text =
                            getString(R.string.humidity_label, result.data.current.humidity)
                        tvCloud.text = getString(R.string.cloud_label, result.data.current.clouds)
                        tvPress.text =
                            getString(R.string.pressure_label, result.data.current.pressure)
                        tvWind.text =
                            getString(R.string.wind_speed_label, result.data.current.wind_speed)

                        val locale = Locale(language, "EG")
                        val sdf = java.text.SimpleDateFormat("dd MMMM ,  hh:mm a", locale)
                        val date = java.util.Date(myTime * 1000)

                        tvdata.text = sdf.format(date)
                        homeAdapterDaily.submitList(result.data.daily)
                        recyclerViewDaily.adapter = homeAdapterDaily

                        hourlyAdapter.submitList(result.data.hourly)
                        recyclerViewHour.adapter = hourlyAdapter

                        Log.i("Success", "${result.data}")
                    }

                    is ApiState.Failure -> {
                        Log.i("Failed", "Failedyyyooo:${result.msg} ")
                    }

                    else -> {}
                }
            }


        }




    }


    companion object {

        var dLocale: Locale? = null

        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }

    fun updateConfiggg(lang: String) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val localeManager = requireContext().getSystemService(LocaleManager::class.java)
            localeManager.applicationLocales = LocaleList.forLanguageTags(lang)

        } else {
            val locale = Locale(lang)
            val res = resources
            val dm = res.displayMetrics
            val conf = res.configuration

            conf.setLocale(locale)

            requireActivity().baseContext.resources.updateConfiguration(
                conf,
                requireActivity().baseContext.resources.displayMetrics
            )

            requireActivity().apply {
                val intent = intent
            }
        }

    }


    fun updateConfig(lang: String) {
        val locale = Locale(lang)
        val res = resources
        val dm = res.displayMetrics
        val conf = res.configuration
        conf.locale = locale


    }

    private fun setupNetworkMonitor() {
        networkMonitor = NetworkMonitor(requireContext())
    }

    override fun onStart() {
        super.onStart()
        networkMonitor.registerNetworkCallback(this)
        checkAndApplyConnectionState()
    }

    override fun onStop() {
        super.onStop()
        networkMonitor.unregisterNetworkCallback()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Clear view references to avoid memory leaks
        layoutMainContent = null
        layoutNoInternet = null
        btnRetry = null
    }

    private fun checkAndApplyConnectionState() {
        if (networkMonitor.isOnline) {
            showMainContent()
        } else {
            showNoInternetState()
        }
    }

    private fun showMainContent() {
        layoutMainContent?.visibility = View.VISIBLE
        layoutNoInternet?.visibility = View.GONE
        // Load or refresh remote API data here if needed
    }

    private fun showNoInternetState() {
        layoutMainContent?.visibility = View.GONE
        layoutNoInternet?.visibility = View.VISIBLE
    }

    override fun onNetworkAvailable() {
        showMainContent()
    }

    override fun onNetworkLost() {
        showNoInternetState()

    }


}


