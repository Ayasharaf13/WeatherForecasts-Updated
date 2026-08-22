package com.example.weatherforecasts.alertscreen.view

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.weatherforecasts.R
import com.example.weatherforecasts.alertscreen.viewmodel.AlertViewModel
import com.example.weatherforecasts.alertscreen.viewmodel.AlertViewModelFactory
import com.example.weatherforecasts.homescreen.viewmodel.HomeViewModel
import com.example.weatherforecasts.homescreen.viewmodel.HomeViewModelFactory
import com.example.weatherforecasts.model.*
import com.example.weatherforecasts.network.ApiState
import com.example.weatherforecasts.network.WeatherClient
import com.example.weatherforecasts.utilise.AlarmSchedulerItem
import com.example.weatherforecasts.utilise.AndroidAlarmScheduler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.time.*
import java.time.format.DateTimeFormatter
import java.util.*


class AlertDialogFragment : DialogFragment() {

    @RequiresApi(Build.VERSION_CODES.O)
    val alarm: Alarm = Alarm()

    lateinit var sharedPreferences: SharedPreferences
    lateinit var location: Location
    lateinit var intent: Intent
    lateinit var calendarT: Calendar
    lateinit var btnCancel: TextView

    private lateinit var instantFromTimestampStart: Instant
    private lateinit var instantFromTimestampEnd: Instant
    private lateinit var instantFromPicker: Instant
    private lateinit var instantToPicker: Instant
    lateinit var locaDateFrom: LocalDate
    lateinit var localTimeFrom: LocalTime
    lateinit var localDateTo: LocalDate
    lateinit var localTimeTo: LocalTime
    var isTimeFrom: Boolean = true
    lateinit var timeTxtFrom: TextView
    lateinit var timeTxtTo: TextView
    lateinit var selectdate: TextView
    lateinit var viewModelAlert: AlertViewModel
    lateinit var alertViewModelFactory: AlertViewModelFactory
    lateinit var btnSave: TextView
    lateinit var textFrom: TextView
    lateinit var textTo: TextView
    lateinit var displayCalenderTxtFrom: TextView
    lateinit var displayCalenderTxtTo: TextView
    lateinit var viewModel: HomeViewModel
    lateinit var allFactory: HomeViewModelFactory


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }

        alertViewModelFactory = AlertViewModelFactory(
            Repository.getInstance(
                WeatherClient.getInstance(),
                ConcreteLocalSource.getInstance(requireContext())
            )
        )
        viewModelAlert = ViewModelProvider(
            requireParentFragment(),
            alertViewModelFactory
        ).get(AlertViewModel::class.java)

        intent = Intent(requireContext(), MyReceiver::class.java)

    }

    val c = Calendar.getInstance()
    val year = c.get(Calendar.YEAR)
    val month = c.get(Calendar.MONTH)
    val day = c.get(Calendar.DAY_OF_MONTH)

    @RequiresApi(Build.VERSION_CODES.O)
    fun displayCalender(txt: String) {

        val timePickerDialog = TimePickerDialog(
            requireContext(),
            { _, hourOfDay, minute ->
                val isAM = hourOfDay < 12
                val hour = if (hourOfDay % 12 == 0) 12 else hourOfDay % 12
                val amPm = if (isAM) "AM" else "PM"
                val calendartime = Calendar.getInstance()
                calendartime.set(Calendar.HOUR_OF_DAY, hourOfDay)
                calendartime.set(Calendar.MINUTE, minute)
                // Set Arabic Locale
                //  val locale = Locale("ar", "EG") // You can use "ar" only if you prefer
                val formattedTime = String.format("%02d:%02d %s", hour, minute, amPm)

                //  val locale = if (language == "ar") Locale("ar", "EG") else Locale("en", "US")
                val sdf = SimpleDateFormat("hh:mm a")
                val dateArabic = sdf.format(calendartime.time.time)
                if (isTimeFrom) {
                    timeTxtFrom.text = dateArabic//formattedTime
                    alarm.textFromTime = dateArabic //formattedTime
                    localTimeFrom = LocalTime.of(hourOfDay, minute)
                    alarm.localTimeFrom = localTimeFrom
                } else {
                    timeTxtTo.text = dateArabic//formattedTime
                    alarm.textToTime = dateArabic//formattedTime
                    localTimeTo = LocalTime.of(hourOfDay, minute)
                    alarm.localTimeTo = localTimeTo
                }
                Log.d("TimePicker", "Selected time: $formattedTime")
            },
            12, 0, false // The third parameter 'false' indicates 12-hour format
        )

        timePickerDialog.show()
        // on below line we are creating a
        // variable for date picker dialog.
        val datePickerDialog = DatePickerDialog(
            // on below line we are passing context.
            requireContext(),
            { view, year, monthOfYear, dayOfMonth ->
                c.set(year, monthOfYear, dayOfMonth)
                val sdf = SimpleDateFormat("dd - MM - yyyy")
                val dat = sdf.format(c.time) // ✅ This will show Arabic month names


                Toast.makeText(
                    requireContext(),
                    "PPOO$year:${monthOfYear + 1}:$dayOfMonth",
                    Toast.LENGTH_SHORT
                ).show()
                // dateEdt.setText(dat)
                if (txt == "F") {
                    displayCalenderTxtFrom.setText(dat)
                    locaDateFrom = LocalDate.of(year, (monthOfYear + 1), dayOfMonth)
                    alarm.localDateFrom = locaDateFrom
                    alarm.textDateFrom = dat

                } else if (txt == "T") {

                    displayCalenderTxtTo.setText(dat)
                    localDateTo = LocalDate.of(year, (monthOfYear + 1), dayOfMonth)
                    alarm.localDateTo = localDateTo
                    alarm.textDateTo = dat

                } else {
                    Toast.makeText(requireContext(), "please Enter F or T ", Toast.LENGTH_SHORT)
                        .show()
                }

            },
            // on below line we are passing year, month
            // and day for the selected date in our date picker.
            year,
            month,
            day
        )

        datePickerDialog.show()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        sharedPreferences = requireContext().getSharedPreferences(
            "LocationPrefs",
            Context.MODE_PRIVATE
        )

        val sharedPreferences =
            requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_alert_dialog, container, false)
    }


    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SuspiciousIndentation")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        calendarT = Calendar.getInstance() // Get the current date and time

        calendarT[Calendar.YEAR] = 2026
        calendarT.set(Calendar.MONTH, 11 - 1); // Subtract 1 because Calendar.MONTH is zero-based

        calendarT.set(Calendar.MONTH, Calendar.NOVEMBER); // Directly use the predefined constant

        calendarT[Calendar.DAY_OF_MONTH] = 28
        calendarT[Calendar.HOUR_OF_DAY] = 11 // 8 AM (24-hour format)
        calendarT[Calendar.MINUTE] = 43
        calendarT[Calendar.SECOND] = 0
        calendarT[Calendar.MILLISECOND] = 0

        textFrom = view.findViewById(R.id.txt_from)
        textTo = view.findViewById(R.id.txt_to)

        displayCalenderTxtFrom = view.findViewById(R.id.display_from)
        displayCalenderTxtTo = view.findViewById(R.id.display_To)

        timeTxtFrom = view.findViewById(R.id.txtTimeFrom)
        timeTxtTo = view.findViewById(R.id.txtTimeTo)

        allFactory =
            HomeViewModelFactory(
                Repository.getInstance(
                    WeatherClient.getInstance(),
                    ConcreteLocalSource.getInstance(requireContext())
                )
            )
        viewModel =
            ViewModelProvider(requireActivity(), allFactory).get(HomeViewModel::class.java)

        btnSave = view.findViewById(R.id.btn_save)
        btnCancel = view.findViewById(R.id.btnCancel)


        btnCancel.setOnClickListener {

            dismiss()
        }

        textFrom.setOnClickListener {
            isTimeFrom = true

            displayCalender("F")
        }

        textTo.setOnClickListener {
            isTimeFrom = false

            displayCalender("T")

        }


        Log.i("saveapiObserve", "Enterdialog")

        // دالة مساعدة سريعة للـ Toast
        fun showToast(resId: Int) {
            Toast.makeText(activity, getString(resId), Toast.LENGTH_SHORT).show()
        }

        // 1. دالة التحقق من المدخلات (تتلقى 0 arguments)
        fun validateInputs(): Boolean {
            val isValid = alarm.textDateFrom.isNotEmpty() &&
                    alarm.textDateTo.isNotEmpty() &&
                    alarm.textFromTime.isNotEmpty() &&
                    alarm.textToTime.isNotEmpty()

            if (!isValid) {
                showToast(R.string.please_enter_data_and_time)
                Log.i("AlertDialog", "Save clicked but validation failed.")
            }
            return isValid
        }

        // 3. دالة جدولة الـ System Alarm الخاصة بالـ UI
        fun scheduleSystemAlarm(message: String) {
            val finalMessage = message.ifEmpty { getString(R.string.no_alert_weather_good) }

            val alarmItem = AlarmSchedulerItem(
                triggerTimeMillis = System.currentTimeMillis(),
                message = finalMessage
            )

            val scheduler = AndroidAlarmScheduler(requireContext())
            scheduler.schedule(alarmItem)
        }

        // 2. دالة المعالجة الحسابية
        fun executeSaveProcess(lat: String, lon: String) {
            val weatherState = viewModel.weather.value

            if (weatherState is ApiState.Success) {
                val listOfAlerts = weatherState.data.alerts

                val request = AlertViewModel.SaveAlarmRequest(
                    alarm = alarm,
                    latitude = lat,
                    longitude = lon,
                    sharedPreferences = sharedPreferences,
                    weatherAlerts = listOfAlerts
                )

                viewModelAlert.saveAlarmAndLocation(request).observe(viewLifecycleOwner) { result ->
                    if (result.isSuccess) {
                        scheduleSystemAlarm(result.alarmMessage)
                        dismiss()

                    }
                }

            }
        }

        btnSave.setOnClickListener {
            if (!validateInputs()) return@setOnClickListener

            val lat = sharedPreferences.getString("latitude", null)
            val lon = sharedPreferences.getString("longitude", null)

            if (lat.isNullOrEmpty() || lon.isNullOrEmpty()) {
                showToast(R.string.location_missing)
                return@setOnClickListener
            }

            executeSaveProcess(lat, lon)
        }


    }


    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AlertDialogFragment().apply {
                arguments = Bundle().apply {

                }

            }

    }


}


/*




      /*  btnSave.setOnClickListener {
            // 1. التحقق من إدخال البيانات المطلوبة
            if (alarm.textDateFrom.isEmpty() || alarm.textDateTo.isEmpty() ||
                alarm.textFromTime.isEmpty() || alarm.textToTime.isEmpty()
            ) {
                Toast.makeText(
                    activity,
                    "please_enter_data_and_time", // يُفضل استخدام الموارد النصية
                    Toast.LENGTH_LONG
                ).show()
                Log.i("AlertDialog", "Save clicked but validation failed.")
                return@setOnClickListener
            }

            // 2. جلب الموقع من SharedPreferences
            val lat = sharedPreferences.getString("latitude", null)
            val lon = sharedPreferences.getString("longitude", null)

            Log.i("lattt_Dialog",lat.toString())
            Log.i("lattt_Dialog",lat.toString())

            if (lat.isNullOrEmpty() || lon.isNullOrEmpty()) {
                Toast.makeText(activity, "Location missing!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }


            fun Double.roundTo4Decimals(): Double {
                return String.format(Locale.US, "%.4f", this).toDouble()
            }

// استخدامه قبل البحث والحفظ:
            val safeLat = lat.toDouble().roundTo4Decimals()
            val safeLon = lon.toDouble().roundTo4Decimals()


            val location = Location(
                idLoc = 0, // 👈 مهم جداً: صفر ليتم التوليد التلقائي (Auto-Increment)
                lat = safeLat,
                lon = safeLon
            )




            // 3. تنفيذ العمليات في الخلفية بأمان
            lifecycleScope.launch(Dispatchers.IO) {

                // بدلاً من collectLatest المفتوح، نأخذ القيمة الحالية المتاحة لـ Weather State
                val weatherState = viewModel.weather.value

                if (weatherState is ApiState.Success) {
                    val listOfAlerts = weatherState.data.alerts

                    // التعامل مع قاعدة البيانات
                    var locId = viewModelAlert.getLocById(location.lat, location.lon)
                    Log.i("idLoc_1",locId.toString())

                    if (locId == null || locId <= 0) {

                        Log.i("idLoc_2",locId.toString())
                      locId =  viewModelAlert.saveLoc(location)
                        Log.i("idLoc_3",locId.toString())

                        var editorLoc: SharedPreferences.Editor = sharedPreferences.edit()
                        editorLoc.putInt("idLocation",locId)
                        editorLoc.apply()


                    }

                    if (locId > 0) {
                        if (!listOfAlerts.isNullOrEmpty()) {
                            viewModelAlert.saveAlarmApi(listOfAlerts, locId)
                        }


                        // حفظ التنبيه الخاص بالمستخدم
                        viewModelAlert.addToAlarm(alarm, locId)

                    }

                    // العودة لـ Main Thread للتعامل مع الـ UI و Navigation/Alerts
                    withContext(Dispatchers.Main) {
                        if (listOfAlerts.isNullOrEmpty()) {
                            val message = getString(R.string.no_alert_weather_good)
                          //  setAlarm(massages = message)

                            val alarmItem = AlarmSchedulerItem(
                                triggerTimeMillis = System.currentTimeMillis(),
                                message = message
                            )


                            val scheduler = AndroidAlarmScheduler(requireContext())
                            scheduler.schedule(alarmItem)

                        } else {
                            val firstEvent = listOfAlerts[0].event ?: "Weather Alert"

                            val alarmItem = AlarmSchedulerItem(
                                triggerTimeMillis = System.currentTimeMillis(),
                                message = firstEvent
                            )

                            val scheduler = AndroidAlarmScheduler(requireContext())
                            scheduler.schedule(alarmItem)

                          // setAlarm(massages = firstEvent)

                        }

                        dismiss() // إغلاق الـ Dialog
                    }
                }
            }
        }*/
 */