package com.masai.flairbnbapp.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.masai.flairbnbapp.R
import com.masai.flairbnbapp.databinding.ActivityMainBinding
import com.masai.flairbnbapp.interfaces.NavigationToggleComponent
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), NavigationToggleComponent {
    private lateinit var binding: ActivityMainBinding

    companion object {
        lateinit var navToggleInterface: NavigationToggleComponent
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        homeNavigationSetup()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        nav_view.visibility = View.GONE
    }

    private fun homeNavigationSetup() {
        navToggleInterface = this
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_mainscreen)
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.exploreFragment,
                R.id.wishlistsFragment,
                R.id.tripsFragment,
                R.id.inboxFragment,
                R.id.profileFragment
            )
        )
        navView.setupWithNavController(navController)
    }

    override fun setNavigation(flag: Boolean) {
        if (flag) {
            nav_view.visibility = View.VISIBLE
        } else
            nav_view.visibility = View.GONE
    }

    override fun navigateToProfile() {

    }
}