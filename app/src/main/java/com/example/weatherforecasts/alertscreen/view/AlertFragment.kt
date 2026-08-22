package com.example.weatherforecasts.alertscreen.view

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherforecasts.R
import com.example.weatherforecasts.alertscreen.viewmodel.AlertViewModel
import com.example.weatherforecasts.alertscreen.viewmodel.AlertViewModelFactory
import com.example.weatherforecasts.model.Alarm
import com.example.weatherforecasts.model.ConcreteLocalSource
import com.example.weatherforecasts.model.Repository
import com.example.weatherforecasts.network.WeatherClient
import com.example.weatherforecasts.utilise.AndroidAlarmScheduler
import com.google.android.material.floatingactionbutton.FloatingActionButton


class AlertFragment : Fragment(), SendData {
    lateinit var floatingActionBtn: FloatingActionButton
    lateinit var recyclerView: RecyclerView
    lateinit var adapterAlert: AlarmAdapter
    lateinit var viewModelAlert: AlertViewModel
    lateinit var alertViewModelFactory: AlertViewModelFactory


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.fragment_alert, container, false)
    }


    @SuppressLint("SuspiciousIndentation")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.i("AlarmListSize", "Size: OnViewCreated...........")
        floatingActionBtn = view.findViewById(R.id.floatingActionButtonAlert)
        recyclerView = view.findViewById(R.id.recycler_alert)
        adapterAlert = AlarmAdapter(this, requireContext())
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapterAlert

        floatingActionBtn.setOnClickListener {

            var dialog = AlertDialogFragment()
            dialog.show(childFragmentManager, "AlertFragment")

        }

        alertViewModelFactory = AlertViewModelFactory(
            Repository.getInstance(
                WeatherClient.getInstance(),
                ConcreteLocalSource.getInstance(requireContext())
            )
        )

        viewModelAlert =
            ViewModelProvider(this, alertViewModelFactory).get(AlertViewModel::class.java)

        val sharedPreferences =
            requireContext().getSharedPreferences("LocationPrefs", Context.MODE_PRIVATE)


        Log.i("AlarmListSize", "Size: before list display")

        setupCombinedDataObserver()
         /* viewModelAlert.currentLocation.observe(viewLifecycleOwner) { locationIds ->
          if (!locationIds.isNullOrEmpty()) {
              // حذف جميع الأماكن دفعة واحدة في الـ Background عبر الـ ViewModel
              viewModelAlert.deleteAllLocations(locationIds)
          }
      }*/


       // sharedPreferences.edit().clear().apply()
        val idLocation = sharedPreferences.getInt("idLocation", -1)
        Log.i("AlarmListSize_LocationId", idLocation.toString())

        if (idLocation > 0) {
            viewModelAlert.setLocation(idLocation)
        }


    }


    private fun setupCombinedDataObserver() {

        viewModelAlert.combinedData.observe(viewLifecycleOwner) { (alarms, alerts) ->
            if (!alarms.isNullOrEmpty()) {
                // 1. تحديث واجهة الـ Adapter بالبيانات الجديدة فوراً
                adapterAlert.submitList(alarms.toList())
                Log.i("AlarmListSize", "Size: ${alarms.size}")
                // 2. تشغيل الفحص للوجيك الإشعارات في الخلفية
                viewModelAlert.processAlarmsAndCheckAlerts(alarms, alerts ?: emptyList())

            } else {
                adapterAlert.submitList(emptyList())

                Log.i("AlarmListSizeEmpty", "Size: ${alarms.size}")
            }
        }
        // 2. استقبال النتيجة المجهزة وجدولتها باستخدام AlarmManager
        viewModelAlert.scheduleAlarmEvent.observe(viewLifecycleOwner) { alarmItem ->
            val scheduler = AndroidAlarmScheduler(requireContext())
            scheduler.schedule(alarmItem)
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AlertFragment().apply {
                arguments = Bundle().apply {


                }
            }
    }

    override fun sendAlarm(alarm: Alarm) {
        viewModelAlert.delete(alarm)

    }

    override fun onResume() {
        super.onResume()

        // إعادة قراءة الـ ID عند كل عودة للشاشة لضمان استخدام أحدث موقع تم اختياره
        val sharedPreferences =
            requireContext().getSharedPreferences("LocationPrefs", Context.MODE_PRIVATE)
        val idLocation = sharedPreferences.getInt("idLocation", -1)

        Log.i("AlarmListSize_LocationId", "Current Location ID: $idLocation")

        if (idLocation >= 0 || idLocation != null) {

            viewModelAlert.setLocation(idLocation)

        } else {
            Log.w("AlarmListSize_LocationId__", "No valid idLocation found in SharedPreferences!")
        }
    }



}