package com.example.weatherforecasts.map

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.pm.PackageManager


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.example.weatherforecasts.R

import com.example.weatherforecasts.favourit.viewmodel.FavoriteViewModelFactory
import com.example.weatherforecasts.favourit.viewmodel.FavouriteViewModel
import com.example.weatherforecasts.model.ConcreteLocalSource
import com.example.weatherforecasts.model.Fav

import com.example.weatherforecasts.model.Repository
import com.example.weatherforecasts.network.WeatherClient
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


@Suppress("UNREACHABLE_CODE")
class LocationPickerDialog : DialogFragment(), OnMapReadyCallback, GoogleMap.OnMapClickListener {

    lateinit var viewModelFav: FavouriteViewModel
    lateinit var favViewModelFactory: FavoriteViewModelFactory
    lateinit var fav: Fav
    private lateinit var mMap: GoogleMap
    private var mapLocationLatitude: Double? = null
    private var mapLocationLongitude: Double? = null
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private var marker: Marker? = null
    private lateinit var floatingButton: FloatingActionButton

    private val LOCATION_PERMISSION_REQUEST_CODE = 1

    // 1. متغير يحفظ الإحداثية المختارة حالياً من الخريطة
    private var selectedLatLng: LatLng? = null

    interface OnLocationSelectedListener {
        fun onLocationSelected(lat: Double, lng: Double)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val rootView = inflater.inflate(R.layout.dialog_map, container, false)

        fav = Fav()
        return rootView

    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val fm = childFragmentManager
        floatingButton = view.findViewById(R.id.floatingActionButtonMap)
        favViewModelFactory = FavoriteViewModelFactory(
            Repository.getInstance(
                WeatherClient.getInstance(),
                ConcreteLocalSource.getInstance(requireContext())
            )
        )
        viewModelFav =
            ViewModelProvider(this, favViewModelFactory).get(FavouriteViewModel::class.java)


        var mapFragment = fm.findFragmentByTag("mapFragment") as? SupportMapFragment

        if (mapFragment == null) {
            mapFragment = SupportMapFragment.newInstance()
            val ft = fm.beginTransaction()
            ft.replace(R.id.map, mapFragment, "mapFragment")
            ft.commit()

        }

        mapFragment.getMapAsync(this)
        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireActivity())
        val addLocation: FrameLayout = view.findViewById(R.id.btn_add_location)

        addLocation.setOnClickListener {
            val listener = activity as? OnLocationSelectedListener
            listener?.onLocationSelected(mapLocationLatitude ?: 0.0, mapLocationLongitude ?: 0.0)
            dismiss()
        }

        // ربط الزر يتم مرة واحدة فقط عند إنشاء الشاشة!
        setupFloatingButtonListener()

    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState)

        AlertDialog.Builder(requireContext())
            .setMessage(getString(R.string.order_confirmation))
            .setPositiveButton(getString(R.string.ok)) { _, _ -> }
            .create()


    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        if (!checkPermissions()) {
            showCustomLocationPermissionDialog()
            return
        }
        // Permissions granted, enable location features
        mMap.isMyLocationEnabled = true
        mMap.setOnMapClickListener(this)
        getDeviceLocation()


    }

    @SuppressLint("SuspiciousIndentation")
    override fun onMapClick(latLng: LatLng) {
        if (mMap == null) return

        updateMapMarker(latLng)
        selectedLatLng = latLng
        // setupFloatingButtonListener(latLng)
    }

    // 1. دالة مسؤولة فقط عن رسم الـ Marker والتحديث
    private fun updateMapMarker(latLng: LatLng) {
        marker?.remove()
        marker = mMap.addMarker(
            MarkerOptions()
                .position(latLng)
                .title("You can add this location")
        )
        marker?.showInfoWindow()

        mapLocationLatitude = latLng.latitude
        mapLocationLongitude = latLng.longitude
    }

    // 2. دالة مسؤولة عن ربط زر الضغط وتجهيز الديالوج
    private fun setupFloatingButtonListener() {

        floatingButton.setOnClickListener {
            val currentLocation = selectedLatLng
            if (currentLocation != null) {
                showOrderConfirmationDialog(currentLocation)
            } else {
                showToast("Please select a location on the map first")
            }

        }
    }

    // 3. دالة مسؤولة عن إنشاء وعرض الـ AlertDialog
    private fun showOrderConfirmationDialog(latLng: LatLng) {
        AlertDialog.Builder(requireContext())
            .setMessage(getString(R.string.order_confirmation))
            .setPositiveButton(getString(R.string.ok)) { dialogInterface, _ ->
                processLocationSaveAndNavigate(latLng)
                dialogInterface.dismiss()
            }
            .setNegativeButton(getString(R.string.cancel)) { dialogInterface, _ ->
                dialogInterface.dismiss()
            }
            .create()
            .show()
    }

    // 4. دالة المعالجة في الخلفية والتنقل (Background Processing & Navigation)
    private fun processLocationSaveAndNavigate(latLng: LatLng) {
        lifecycleScope.launch(Dispatchers.IO) {
            delay(100)
            Log.i("savgetLocation", latLng.longitude.toString())

            if (fav.latFav != null && fav.lonFav != null) {
                fav.latFav = latLng.latitude
                fav.lonFav = latLng.longitude

                viewModelFav.saveNameLoc(fav)
                Log.i("savecityName", "cityName")

                withContext(Dispatchers.Main) {
                    navigateToFavorites(latLng)
                }
            } else {
                withContext(Dispatchers.Main) {
                    showToast("Location is not available")
                }
            }
        }
    }

    // 5. دوال مساعدة قصيرة للـ UI (Single Responsibility)
    private fun navigateToFavorites(latLng: LatLng) {
        val action = LocationPickerDialogDirections.actionLocationPickerDialogToFavouritFragment(
            latLng.latitude.toString(),
            latLng.longitude.toString()
        )
        val navController = Navigation.findNavController(
            requireActivity(),
            R.id.nav_host_fragment_content_main
        )
        navController.navigate(action)
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }


    /*  @SuppressLint("SuspiciousIndentation")
      override fun onMapClick(latLng: LatLng) {
          if (mMap != null) {
              marker?.remove()
              marker =
                  mMap.addMarker(MarkerOptions().position(latLng).title("You can add this location"))
              marker?.showInfoWindow()
              mapLocationLatitude = latLng.latitude
              mapLocationLongitude = latLng.longitude

              floatingButton.setOnClickListener { marker ->

                  val dialogBuilder = AlertDialog.Builder(requireContext())
                      .setMessage(getString(R.string.order_confirmation))
                      .setPositiveButton(getString(R.string.ok)) { dialogInterface, _ ->
                          lifecycleScope.launch(Dispatchers.IO) {
                              delay(100)
                              Log.i("savgetLocation", latLng.longitude.toString())
                              if (fav.latFav != null && fav.lonFav != null) {

                                  fav.latFav = latLng.latitude
                                  fav.lonFav = latLng.longitude


                                  viewModelFav.saveNameLoc(fav)
                                  withContext(Dispatchers.Main) {
                                      val action =
                                          LocationPickerDialogDirections.actionLocationPickerDialogToFavouritFragment(
                                              latLng.latitude.toString(),
                                              latLng.longitude.toString()
                                          )
                                      val navController = Navigation.findNavController(
                                          requireActivity(),
                                          R.id.nav_host_fragment_content_main
                                      )
                                      navController.navigate(action)
                                  }
                                  Log.i("savecityName", "cityName")
                              } else {
                                  withContext(Dispatchers.Main) {
                                      Toast.makeText(
                                          requireContext(),
                                          "Location is not available",
                                          Toast.LENGTH_SHORT
                                      ).show()
                                  }
                              }

                          }


                          // Dismissing the dialog
                          dialogInterface.dismiss()
                      }
                      .setNegativeButton(getString(R.string.cancel)) { dialogInterface, _ ->
                          // Handle cancel action if needed
                          dialogInterface.dismiss()
                      }

                  // Displaying the AlertDialog
                  val dialog = dialogBuilder.create()
                  dialog.show()

              }

          }
      }*/


    private fun checkPermissions(): Boolean {
        return ActivityCompat.checkSelfPermission(
            requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(
                    requireActivity(), Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestSpecificPermission(permission: String) {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(permission),
            LOCATION_PERMISSION_REQUEST_CODE
        )
    }

    private fun showCustomLocationPermissionDialog() {
        val dialogView =
            LayoutInflater.from(requireContext()).inflate(R.layout.dialog_location_permission, null)

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setCancelable(false)
            .create()

        val btnFineLocation = dialogView.findViewById<Button>(R.id.btn_fine_location)
        val btnCoarseLocation = dialogView.findViewById<Button>(R.id.btn_coarse_location)

        btnFineLocation.setOnClickListener {
            requestSpecificPermission(Manifest.permission.ACCESS_FINE_LOCATION)
            dialog.dismiss()
        }

        btnCoarseLocation.setOnClickListener {
            requestSpecificPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
            dialog.dismiss()
        }

        dialog.show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, proceed with location access
                if (ActivityCompat.checkSelfPermission(
                        requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED ||
                    ActivityCompat.checkSelfPermission(
                        requireActivity(), Manifest.permission.ACCESS_COARSE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    // Enable location features
                    enableLocationFeatures()
                }
            } else {
                // Permission denied, show a message to the user
                Toast.makeText(
                    context,
                    "Location permission is required to use this feature",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun enableLocationFeatures() {
        mMap.isMyLocationEnabled = true
        getDeviceLocation()
    }

    private fun getDeviceLocation() {
        try {
            if (checkPermissions()) {
                val locationResult = fusedLocationProviderClient.lastLocation
                locationResult.addOnCompleteListener(requireActivity()) { task ->
                    if (task.isSuccessful) {
                        val lastKnownLocation = task.result
                        if (lastKnownLocation != null) {
                            mMap.moveCamera(
                                CameraUpdateFactory.newLatLngZoom(
                                    LatLng(lastKnownLocation.latitude, lastKnownLocation.longitude),
                                    DEFAULT_ZOOM.toFloat()
                                )
                            )
                        }
                    } else {
                        Log.e(TAG, "Current location is null. Using defaults.")
                        mMap.uiSettings.isMyLocationButtonEnabled = false
                    }
                }
            }
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message, e)
        }
    }

    companion object {
        const val TAG = "LocationPickerDialog"
        private const val DEFAULT_ZOOM = 15

    }

}