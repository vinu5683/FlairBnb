package com.masai.flairbnbapp.ui

import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RadioGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.masai.flairbnbapp.R
import com.masai.flairbnbapp.localdatabases.LocalKeys
import com.masai.flairbnbapp.localdatabases.PreferenceHelper
import com.masai.flairbnbapp.models.UserModel
import com.masai.flairbnbapp.viewmodels.UsersViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_add_info.*
import kotlinx.coroutines.delay

@AndroidEntryPoint
class AddInfoFragment : Fragment() {

    val userViewModel by viewModels<UsersViewModel>()

    var anim: AnimationDrawable? = null


    lateinit var etFirstName: TextInputLayout
    lateinit var etLastName: TextInputLayout
    lateinit var etContactNumber: TextInputLayout
    lateinit var etAddress: TextInputLayout
    lateinit var genderGroup: RadioGroup
    private fun initViews(view: View) {
        view.apply {
            etFirstName = findViewById(R.id.etFirstName)
            etLastName = findViewById(R.id.etLastName)
            etAddress = findViewById(R.id.etAddress)
            etContactNumber = findViewById(R.id.etContactNumber)
            genderGroup = findViewById(R.id.rgGender)
        }
    }

    private fun setToView(view: View) {
        try {
            etFirstName.editText?.setText(userModel?.firstName)
            etLastName.editText?.setText(userModel?.lastName)
            etContactNumber.editText?.setText(userModel?.contactNumber)
            etAddress.editText?.setText(userModel?.address)
            if (userModel?.gender == null || userModel?.gender == "Male" || userModel?.gender == "") {
                rbMale.isSelected = true
            } else if (userModel?.gender == "Female") rbFemale.isSelected = true
            else if (userModel?.gender == "Other") rbOther.isSelected = true
        } catch (e: Exception) {

        }
    }

    var userModel: UserModel? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_info, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        PreferenceHelper.getSharedPreferences(view.context)
        val navController = Navigation.findNavController(view)
        userViewModel.getUser(PreferenceHelper.readStringFromPreference(LocalKeys.KEY_USER_GOOGLE_ID))
        userViewModel.userModel.observe(viewLifecycleOwner, {
            userModel = it
            if (userModel != null) {
                setToView(view)
            }
        })

        initViews(view)

        val constraintLayout = view.findViewById<ConstraintLayout>(R.id.addInfoContainer)
        anim = constraintLayout.background as AnimationDrawable
        anim?.setEnterFadeDuration(5000)
        anim?.setExitFadeDuration(2000)

        view.findViewById<Button>(R.id.btnSaveInfo).setOnClickListener {
            if (checkValidations()) {
                userModel?.address = etAddress.editText?.text.toString()
                userModel?.firstName = etFirstName.editText?.text.toString()
                userModel?.lastName = etLastName.editText?.text.toString()
                userModel?.contactNumber = etContactNumber.editText?.text.toString()
                var gender = "";
                when {
                    rbMale.isSelected -> gender = "Male"
                    rbFemale.isSelected -> gender = "Female"
                    rbOther.isSelected -> gender = "Other"
                }
                userModel?.gender = gender

                userViewModel.saveUserModel(userModel!!)

                navController.navigate(R.id.action_addInfoFragment_to_termsAndConditionsFragment)

            }

        }

    }


    private fun checkValidations(): Boolean {
        return true;
    }


    override fun onResume() {
        super.onResume()
        if (anim != null && !anim!!.isRunning) anim!!.start()
    }

    override fun onPause() {
        super.onPause()
        if (anim != null && anim!!.isRunning) anim!!.stop()
    }


}