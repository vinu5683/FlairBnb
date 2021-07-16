package com.masai.flairbnbapp.ui

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.masai.flairbnbapp.R
import com.masai.flairbnbapp.databinding.ActivityMainBinding
import com.masai.flairbnbapp.interfaces.NavigationToggleComponent
import com.masai.flairbnbapp.localdatabases.LocalKeys
import com.masai.flairbnbapp.localdatabases.PreferenceHelper
import com.razorpay.PaymentResultListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*


@AndroidEntryPoint
class MainActivity : AppCompatActivity(), NavigationToggleComponent,
    PaymentResultListener {
    private lateinit var binding: ActivityMainBinding
    lateinit var gso: GoogleSignInOptions
    lateinit var googleSignInClient: GoogleSignInClient

    companion object {
        lateinit var navToggleInterface: NavigationToggleComponent
        private val REQUEST_EXTERNAL_STORAGE = 1


        private val PERMISSIONS_STORAGE = arrayOf<String>(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )

        fun verifyStoragePermissions(activity: Activity?) {
            // Check if we have write permission
            val permission = ActivityCompat.checkSelfPermission(
                activity!!,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            if (permission != PackageManager.PERMISSION_GRANTED) {
                // We don't have permission so prompt the user
                ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
                )
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        homeNavigationSetup()
        PreferenceHelper.getSharedPreferences(this)
        gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)
        val window: Window = this.window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.setStatusBarColor(this.resources.getColor(R.color.black))
        verifyStoragePermissions(this)
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

    override fun logout() {
        googleSignInClient.signOut()
            .addOnCompleteListener(this, OnCompleteListener<Void?> {
                PreferenceHelper.writeStringToPreference(LocalKeys.KEY_USER_GOOGLE_ID, null)
                PreferenceHelper.writeStringToPreference(LocalKeys.KEY_IS_USER_LOGGED_IN, null)
                PreferenceHelper.writeStringToPreference(LocalKeys.KEY_USER_CITY_LONG, null)
                PreferenceHelper.writeStringToPreference(LocalKeys.KEY_USER_CITY_LAT, null)
                PreferenceHelper.writeStringToPreference(LocalKeys.KEY_USER_LOGIN_TYPE, null)
                PreferenceHelper.writeStringToPreference(LocalKeys.CHECK_IN_TIME, null)
                PreferenceHelper.writeStringToPreference(LocalKeys.CHECK_OUT_TIME, null)
                this.finish()
                startActivity(intent)
            })
    }

    override fun onPaymentSuccess(p0: String?) {
        PlaceDetailsFragment.onPaymentDoneInterface.onPaymentDone(p0!!)
    }

    override fun onPaymentError(p0: Int, p1: String?) {

    }
}