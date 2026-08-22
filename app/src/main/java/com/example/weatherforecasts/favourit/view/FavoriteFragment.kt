package com.example.weatherforecasts.favourit.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherforecasts.R
import com.example.weatherforecasts.favourit.viewmodel.FavoriteViewModelFactory
import com.example.weatherforecasts.favourit.viewmodel.FavouriteViewModel
import com.example.weatherforecasts.model.ConcreteLocalSource
import com.example.weatherforecasts.model.Fav
import com.example.weatherforecasts.model.Repository
import com.example.weatherforecasts.network.WeatherClient
import com.google.android.material.floatingactionbutton.FloatingActionButton


class FavoriteFragment : Fragment(), SendItemToDelte {

    lateinit var floatinButton: FloatingActionButton
    lateinit var recyclerViewFav: RecyclerView
    lateinit var adapterFav: FavAdapter
    lateinit var viewModelFav: FavouriteViewModel
    lateinit var favViewModelFactory: FavoriteViewModelFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.fragment_favourit, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        floatinButton = view.findViewById(R.id.floatingActionButtonFav)
        recyclerViewFav = view.findViewById(R.id.recyclerFav)
        adapterFav = FavAdapter(this, requireContext())
        recyclerViewFav.layoutManager = LinearLayoutManager(requireContext())


        favViewModelFactory = FavoriteViewModelFactory(
            Repository.getInstance(
                WeatherClient.getInstance(),
                ConcreteLocalSource.getInstance(requireContext())
            )
        )


        viewModelFav =
            ViewModelProvider(this, favViewModelFactory).get(FavouriteViewModel::class.java)

        viewModelFav.cityName.observe(viewLifecycleOwner, Observer {

            adapterFav.submitList(it)
            recyclerViewFav.adapter = adapterFav
        })


        floatinButton.setOnClickListener {
            val action = FavoriteFragmentDirections.actionFavouritFragmentToLocationPickerDialog()
            Navigation.findNavController(requireView()).navigate(action)


        }

    }

    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FavoriteFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }

    override fun sendItem(fav: Fav) {

        viewModelFav.deleteNameLoc(fav)
    }
}