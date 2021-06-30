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
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import com.masai.flairbnbapp.R
import com.masai.flairbnbapp.models.RoomModel
import com.masai.flairbnbapp.viewmodels.HostPlaceViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HostPart4Fragment : Fragment() {
    private val pvm by viewModels<HostPlaceViewModel>()
    var anim: AnimationDrawable? = null
    lateinit var roomModel: RoomModel

    lateinit var entireRoom: LinearLayout
    lateinit var privateRoom: LinearLayout
    lateinit var sharedRoom: LinearLayout


    private fun initViews(view: View) {
        view.apply {
            entireRoom = findViewById(R.id.ll_entireplace)
            privateRoom = findViewById(R.id.ll_privateroom)
            sharedRoom = findViewById(R.id.ll_sharedroom)
        }

    }

    private fun resetViews() {
        entireRoom.setBackgroundResource(R.drawable.roundrectangle_gray)
        privateRoom.setBackgroundResource(R.drawable.roundrectangle_gray)
        sharedRoom.setBackgroundResource(R.drawable.roundrectangle_gray)

    }

    private fun initListeners() {
        entireRoom.setOnClickListener {
            resetViews()
            roomModel.roomSpaceType = "An entire place"
            entireRoom.setBackgroundResource(R.drawable.roundrectangle_black)
        }
        privateRoom.setOnClickListener {
            resetViews()
            roomModel.roomSpaceType = "A private room"
            privateRoom.setBackgroundResource(R.drawable.roundrectangle_black)
        }
        sharedRoom.setOnClickListener {
            resetViews()
            roomModel.roomSpaceType = "A shared room"
            sharedRoom.setBackgroundResource(R.drawable.roundrectangle_black)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_host_part4, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val navController = Navigation.findNavController(view)
        initViews(view)

        roomModel = pvm.getTheRoomModel()
        resetViews()
        initListeners()
        val constraintLayout = view.findViewById<ConstraintLayout>(R.id.part4Container)
        anim = constraintLayout.background as AnimationDrawable
        anim?.setEnterFadeDuration(5000)
        anim?.setExitFadeDuration(2000)

        view.findViewById<Button>(R.id.btnNext5).setOnClickListener {

            pvm.setRoomObject(roomModel)
            Log.d("TAG", "onViewCreated: ${roomModel.toString()}")
            navController.navigate(R.id.action_hostPart4Fragment_to_hostPart5Fragment)
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