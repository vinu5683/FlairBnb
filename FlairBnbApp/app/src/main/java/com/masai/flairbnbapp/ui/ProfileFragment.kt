package com.masai.flairbnbapp.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.masai.flairbnbapp.R
import com.masai.flairbnbapp.localdatabases.LocalKeys
import com.masai.flairbnbapp.localdatabases.PreferenceHelper
import com.masai.flairbnbapp.viewmodels.UsersViewModel
import dagger.hilt.android.AndroidEntryPoint
import de.hdodenhof.circleimageview.CircleImageView

@AndroidEntryPoint

class ProfileFragment : Fragment() {

    val userViewMode by viewModels<UsersViewModel>()

    lateinit var circularProfileImageView: CircleImageView
    lateinit var tvUserName_Profile: TextView
    lateinit var tvShowProfile_Profile: TextView
    lateinit var tvLogout: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        PreferenceHelper.getSharedPreferences(view.context)
        initViews(view)
        setViews(view)
    }

    private fun setViews(view: View) {
        userViewMode.getUser(PreferenceHelper.readStringFromPreference(LocalKeys.KEY_USER_GOOGLE_ID))
            .observe(viewLifecycleOwner, {
                if (it != null) {
                    try {
                        Glide.with(circularProfileImageView.context).load(it.profilePic)
                            .into(circularProfileImageView)
                        tvUserName_Profile.text = it.firstName + " " + it.lastName
                    } catch (e: Exception) {
                        Glide.with(circularProfileImageView.context)
                            .load(R.drawable.placeholder_user)
                            .into(circularProfileImageView)
                    }

                }
            })
        tvShowProfile_Profile.setOnClickListener {

        }
        tvLogout.setOnClickListener {
            MainActivity.navToggleInterface.logout()
        }
    }

    private fun initViews(view: View) {

        view.apply {
            circularProfileImageView = findViewById(R.id.circularProfileImageView)
            tvUserName_Profile = findViewById(R.id.tvUserName_Profile)
            tvShowProfile_Profile = findViewById(R.id.tvShowProfile_Profile)
            tvLogout = findViewById(R.id.tvLogout)
        }
    }

}