package com.masai.flairbnbapp.ui

import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import com.masai.flairbnbapp.R
import com.masai.flairbnbapp.models.RoomModel
import com.masai.flairbnbapp.viewmodels.HostPlaceViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HostPart2Fragment : Fragment() {
    private val pvm by viewModels<HostPlaceViewModel>()
    var anim: AnimationDrawable? = null

    lateinit var roomModel: RoomModel

    lateinit var flat: LinearLayout
    lateinit var house: LinearLayout
    lateinit var secondary: LinearLayout
    lateinit var unique: LinearLayout
    lateinit var bedbreakfast: LinearLayout
    lateinit var boutique: LinearLayout


    private fun initViews(view: View) {
        view.apply {
            flat = findViewById(R.id.ll_cat_flat)
            house = findViewById(R.id.ll_cat_house)
            secondary = findViewById(R.id.ll_cat_secondaryunit)
            unique = findViewById(R.id.ll_cat_uniquespace)
            bedbreakfast = findViewById(R.id.ll_cat_bed_and_breakfast)
            boutique = findViewById(R.id.ll_cat_boutique)
        }

    }

    private fun resetViews() {
        flat.setBackgroundResource(R.drawable.roundrectangle_gray)
        house.setBackgroundResource(R.drawable.roundrectangle_gray)
        secondary.setBackgroundResource(R.drawable.roundrectangle_gray)
        unique.setBackgroundResource(R.drawable.roundrectangle_gray)
        bedbreakfast.setBackgroundResource(R.drawable.roundrectangle_gray)
        boutique.setBackgroundResource(R.drawable.roundrectangle_gray)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_host_part2, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val navController = Navigation.findNavController(view)

        initViews(view)
        roomModel = pvm.getTheRoomModel()
        resetViews()
        initListeners(view)
        val constraintLayout = view.findViewById<ConstraintLayout>(R.id.part2Container)
        anim = constraintLayout.background as AnimationDrawable
        anim?.setEnterFadeDuration(5000)
        anim?.setExitFadeDuration(2000)

        //Log.d("TAG", "onViewCreated: " + placeViewModel.roomModel?.id)

        view.findViewById<Button>(R.id.btnNext3).setOnClickListener {
            pvm.setRoomObject(pvm.roomModel!!)
            navController.navigate(R.id.action_hostPart2Fragment_to_hostPart3Fragment)

        }
    }

    private fun initListeners(view: View) {
        flat.setOnClickListener {
            resetViews()
            roomModel?.category = "Flat"
            flat.setBackgroundResource(R.drawable.roundrectangle_black)
        }
        boutique.setOnClickListener {
            resetViews()
            roomModel?.category = "Boutique hotel"
            boutique.setBackgroundResource(R.drawable.roundrectangle_black)
        }
        house.setOnClickListener {
            resetViews()
            roomModel?.category = "House"
            Log.d("TAG", "initListeners: ${roomModel.category}")
            house.setBackgroundResource(R.drawable.roundrectangle_black)
        }
        unique.setOnClickListener {
            resetViews()
            roomModel?.category = "Unique space"
            unique.setBackgroundResource(R.drawable.roundrectangle_black)
        }
        bedbreakfast.setOnClickListener {
            resetViews()
            roomModel?.category = "Bed and breakfast"
            bedbreakfast.setBackgroundResource(R.drawable.roundrectangle_black)
        }
        secondary.setOnClickListener {
            resetViews()
            roomModel?.category = "Secondary unit"
            secondary.setBackgroundResource(R.drawable.roundrectangle_black)
        }
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