package com.masai.flairbnbapp.ui

import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.masai.flairbnbapp.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SplashFragment : Fragment() {
    val SPLASH_LENGTH: Long = 3000
    private lateinit var navController: NavController

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

    private fun checkLogin(): Boolean {
        return true;
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
        Handler().postDelayed(Runnable {
            if (checkLogin()) {
                navController!!.navigate(R.id.action_splashFragment_to_exploreFragment)
            } else {
                navController!!.navigate(R.id.action_splashFragment_to_loginFragment)
            }
        }, SPLASH_LENGTH)
    }

}