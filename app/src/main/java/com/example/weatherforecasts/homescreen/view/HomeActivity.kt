package com.example.weatherforecasts.homescreen.view

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.example.weatherforecasts.R
import com.example.weatherforecasts.alertscreen.view.AlertFragmentDirections
import com.example.weatherforecasts.favourit.view.FavoriteFragmentDirections
import com.example.weatherforecasts.homescreen.viewmodel.HomeViewModel
import com.example.weatherforecasts.homescreen.viewmodel.HomeViewModelFactory
import com.example.weatherforecasts.model.ConcreteLocalSource
import com.example.weatherforecasts.model.Repository
import com.example.weatherforecasts.network.WeatherClient
import com.example.weatherforecasts.settingscreen.view.SettingFragmentDirections
import com.google.android.material.navigation.NavigationView


class HomeActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    lateinit var mAppBarConfiguration: AppBarConfiguration
    lateinit var viewModel: HomeViewModel
    lateinit var drawerLayout: DrawerLayout
    lateinit var navView: NavigationView


    @SuppressLint("MissingInflatedId", "SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)



        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.navgitor_view)

        Log.i("ssssHomeActivity", "HomeActivity")


        mAppBarConfiguration = AppBarConfiguration.Builder(
            R.id.homeFragment, R.id.favouritFragment, R.id.alertFragment, R.id.settingFragment
        )
            .setOpenableLayout(drawerLayout)
            .build()
        val allFactory =
            HomeViewModelFactory(
                Repository.getInstance(
                    WeatherClient.getInstance(),
                    ConcreteLocalSource.getInstance(this)
                )
            )
        viewModel =
            ViewModelProvider(this, allFactory).get(HomeViewModel::class.java)

        val navController: NavController =
            Navigation.findNavController(this, R.id.nav_host_fragment_content_main)

        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration)
        NavigationUI.setupWithNavController(navView, navController)

        navView.setNavigationItemSelectedListener(this);

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.homeFragment -> {


                    Log.d("Navigation", "Navigated to HomeFragment")
                    drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
                    // Perform any actions needed when HomeFragment is active
                }

                R.id.settingFragment -> {
                    Log.d("Navigation", "Navigated to SettingFragment")
                    drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
                    // Perform any actions needed when SettingFragment is active
                }

                R.id.favouritFragment -> {
                    // Perform any actions needed when FavouritFragment is active
                    Log.d("Navigation", "Navigated to FavFragment")
                    drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
                }

                R.id.alertFragment -> {
                    // Perform any actions needed when AlertFragment is active
                    Log.d("Navigation", "Navigated to alertFragment")
                    drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
                }

                R.id.splashFragment -> {
                    // Perform any actions needed when SplashFragment is active
                    Log.d("Navigation", "Navigated to splashFragment")
                    drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
                }
            }
        }


        val navHeaderView = navView.inflateHeaderView(R.layout.headerdrawer) // Get header view
        // Pass the bundle to the fragment
        val fragment = HomeFragment().apply {
            //  arguments = bundle
        }


    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // return super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        //  return super.onSupportNavigateUp()

        val navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main)
        return (NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp())
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        Log.i("NavigationDrawer", "Selected item ID: ${item.itemId}")

        when (item.itemId) {


            R.id.homeFragment -> {

                // Get the current destination
                val currentDestination = navController.currentDestination?.id
                var lat = viewModel.homeLocation.value?.first
                var lon = viewModel.homeLocation.value?.second

                if (currentDestination == R.id.favouritFragment) {


                    Log.i("NavigationDrawer", "Navigating to HomeFragment")
                    // Create action with arguments
                    val action = FavoriteFragmentDirections.actionFavouritFragmentToHomeFragment(
                        lat.toString(),
                        lon.toString(), homeLocation = "Fav"
                    )

                    // Navigate using the action
                    navController.navigate(action)
                } else if (currentDestination == R.id.homeFragment) {


                    Log.i("NavigationDrawer", "Navigating to HomeFragment")
                    // Create action with arguments
                    val action = HomeFragmentDirections.actionHomeFragmentSelf(
                        lat.toString(),
                        lon.toString(),
                        homeLocation = "Home"
                    )

                    // Navigate using the action
                    navController.navigate(action)
                } else if (currentDestination == R.id.alertFragment) {

                    Log.i("NavigationDrawer", "Navigating to HomeFragment")
                    // Create action with arguments
                    val action = AlertFragmentDirections.actionAlertFragmentToHomeFragment(
                        lat.toString(),
                        lon.toString(),
                        homeLocation = "Home"
                    )

                    // Navigate using the action
                    navController.navigate(action)

                } else if (currentDestination == R.id.settingFragment) {


                    Log.i("NavigationDrawer", "Navigating to HomeFragment")
                    // Create action with arguments
                    val action = SettingFragmentDirections.actionSettingFragmentToHomeFragment(
                        lat.toString(),
                        lon.toString(),
                        homeLocation = "Home"
                    )

                    // Navigate using the action
                    navController.navigate(action)

                }
            }

            R.id.favouritFragment -> {
                Log.i("NavigationDrawer", "Navigating to FavouritFragment")
                navController.navigate(R.id.favouritFragment)


            }

            R.id.alertFragment -> {
                Log.i("NavigationDrawer", "Navigating to AlertFragment")
                navController.navigate(R.id.alertFragment)
            }

            R.id.settingFragment -> {
                Log.i("NavigationDrawer", "Navigating to SettingFragment")
                navController.navigate(R.id.settingFragment)
            }

            else -> {
                Log.w("NavigationDrawer", "Unknown menu item selected")
            }
        }

        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }




}



