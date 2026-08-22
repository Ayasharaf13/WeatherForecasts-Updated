package com.example.weatherforecasts.favourit.view

import android.app.Activity
import android.content.Context
import android.location.Geocoder
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.navigation.Navigation
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherforecasts.R
import com.example.weatherforecasts.model.Fav
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.util.*


class FavAdapter(var data: SendItemToDelte, var cxt: Context) :
    ListAdapter<Fav, FavAdapter.ViewHolder>(FavDiffUtil()) {

    inner class ViewHolder(itemview: View) : RecyclerView.ViewHolder(itemview) {
        var textNameLocationFav: TextView = itemview.findViewById(R.id.locNameFav)
        var removeIconFav: ImageView = itemview.findViewById(R.id.imgRemoveFav)

    }

    val prefrence =
        cxt.getSharedPreferences("PREFERENCE_NAME", Context.MODE_PRIVATE)

    val editor = prefrence.edit()

    val language = prefrence.getString("lang", "en")

    suspend fun getCityName(latitude: Double, longitude: Double, traslate: Locale): String? {


        /*
         Some devices and Android versions have issues with Geocoder.
         Solution: Try using Google's Geocoding API instead of Geocoder.
         Google’s API provides more reliable results but requires an API key.
         */
        return withContext(Dispatchers.IO) {
            try {
                Log.i("latttgecityname", latitude.toString())
                Log.i("latttgggggggggggggecityname", longitude.toString())


                val geocoder = Geocoder(cxt, traslate)
                val addresses = geocoder.getFromLocation(latitude, longitude, 5)
                addresses?.firstOrNull()?.locality
                    ?: addresses?.firstOrNull()?.adminArea
                    ?: "Unknown City"
                // addresses?.get(0)?.locality // Get city name
            } catch (e: IOException) {
                e.printStackTrace()
                "Unknown City" // Default value on failure
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.row_fav, parent, false)
        return ViewHolder(view)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentObj = getItem(position)

        holder.itemView.setOnClickListener {
            val action = FavoriteFragmentDirections.actionFavouritFragmentToHomeFragment(
                currentObj.latFav.toString(),
                currentObj.lonFav.toString(),
                "Fav"
            )
            val navController = Navigation.findNavController(
                cxt as Activity,
                R.id.nav_host_fragment_content_main
            )
            navController.navigate(action)

        }
        val locale = Locale(
            language
        )
        CoroutineScope(Dispatchers.Main).launch {
            var city = getCityName(
                currentObj.latFav,
                currentObj.lonFav,
                locale
            )//(latLng.latitude, latLng.longitude ,locale)
            holder.textNameLocationFav.text = city//currentObj.locationName
        }
        holder.removeIconFav.setOnClickListener {
            val builder = AlertDialog.Builder(cxt)
                .setTitle(cxt.getString(R.string.delete_fav_title))
                .setMessage(cxt.getString(R.string.delete_fav_message))
                .setPositiveButton(cxt.getString(R.string.yes)) { _, _ ->
                    data.sendItem(currentObj)
                }
                .setNegativeButton(cxt.getString(R.string.cancel), null)
            builder.show()
        }


    }


    class FavDiffUtil() : DiffUtil.ItemCallback<Fav>() {
        override fun areItemsTheSame(oldItem: Fav, newItem: Fav): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Fav, newItem: Fav): Boolean {
            return oldItem == newItem
        }

    }


}