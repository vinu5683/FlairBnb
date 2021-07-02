package com.masai.flairbnbapp.ui

import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.firebase.auth.FirebaseAuth
import com.masai.flairbnbapp.R
import com.masai.flairbnbapp.localdatabases.LocalKeys
import com.masai.flairbnbapp.localdatabases.PreferenceHelper
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SplashFragment : Fragment() {
    val SPLASH_LENGTH: Long = 3000
    private lateinit var navController: NavController
    lateinit var mAuth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        getLoginDetails()
        return inflater.inflate(R.layout.fragment_splash, container, false)
    }

    private fun getLoginDetails() {

    }

    private fun checkLogin(view: View): Boolean {

        return if (PreferenceHelper.readBooleanFromPreference(LocalKeys.KEY_IS_USER_LOGGED_IN)) {
            GoogleSignIn.getLastSignedInAccount(view.context) != null || mAuth.currentUser != null
        } else {
            false
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        PreferenceHelper.getSharedPreferences(view.context)
        navController = Navigation.findNavController(view)
        mAuth = FirebaseAuth.getInstance()
        Handler().postDelayed(Runnable {
            if (checkLogin(view)) {
                navController!!.navigate(R.id.action_splashFragment_to_exploreFragment)
            } else {
                navController!!.navigate(R.id.action_splashFragment_to_loginFragment)
            }
        }, SPLASH_LENGTH)
    }

}