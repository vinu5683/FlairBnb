package com.masai.flairbnbapp.ui

import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
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
        val window: Window = this.window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.setStatusBarColor(this.resources.getColor(R.color.black))

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
        try {
            if (flag) {
                nav_view.visibility = View.VISIBLE
            } else
                nav_view.visibility = View.GONE
        } catch (e: Exception) {

        }
    }

    override fun navigateToProfile() {

    }
}