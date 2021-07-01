package com.masai.flairbnbapp.ui

import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.slider.Slider
import com.masai.flairbnbapp.R
import com.masai.flairbnbapp.localdatabases.LocalKeys
import com.masai.flairbnbapp.models.RoomModel
import com.masai.flairbnbapp.models.ServiceListModel
import com.masai.flairbnbapp.models.SubCategoryModel
import com.masai.flairbnbapp.recyclerviews.ServiceListAdapter
import com.masai.flairbnbapp.recyclerviews.ServiceListInterface
import com.masai.flairbnbapp.recyclerviews.SubCategoryAdapter
import com.masai.flairbnbapp.viewmodels.HostPlaceViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint

class HostPart5Fragment : Fragment(), ServiceListInterface {

    private val pvm by viewModels<HostPlaceViewModel>()
    var anim: AnimationDrawable? = null
    var list = ArrayList<ServiceListModel>()
    lateinit var roomModel: RoomModel
    var adapter = ServiceListAdapter(list, this)

    lateinit var rvPart5: RecyclerView
    lateinit var slider_bathroom: Slider
    lateinit var slider_beds: Slider
    lateinit var slider_guest: Slider
    lateinit var slider_rooms: Slider

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_host_part5, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val navController = Navigation.findNavController(view)
        initViews(view)
        roomModel = pvm.getTheRoomModel()
        list = LocalKeys.getServiceList()
        setRecyclerView(view)

        val constraintLayout = view.findViewById<ConstraintLayout>(R.id.part5Container)
        anim = constraintLayout.background as AnimationDrawable
        anim?.setEnterFadeDuration(5000)
        anim?.setExitFadeDuration(2000)
        view.findViewById<Button>(R.id.btnNext6).setOnClickListener {

            roomModel.beds = slider_beds.value.toInt()
            roomModel.total_bathrooms = slider_bathroom.value.toInt()
            roomModel.total_capacity = slider_guest.value.toInt()
            roomModel.rooms = slider_guest.value.toInt()

            pvm.setRoomObject(roomModel)
            pvm.setServices(list)
            Log.d("TAG", "onViewCreated: $roomModel")
            navController.navigate(R.id.action_hostPart5Fragment_to_hostPart6Fragment)
        }
    }

    private fun setRecyclerView(view: View) {
        rvPart5.layoutManager = GridLayoutManager(view.context, 2)
        adapter = ServiceListAdapter(list, this)
        rvPart5.adapter = adapter
        adapter.notifyDataSetChanged()
    }

    private fun initViews(view: View) {
        rvPart5 = view.findViewById(R.id.rvPart5)
        slider_bathroom = view.findViewById(R.id.slider_bathroom)
        slider_beds = view.findViewById(R.id.slider_beds)
        slider_guest = view.findViewById(R.id.slider_guest)
    }

    override fun onServiceSelected(model: ServiceListModel) {
        for (i in 0 until list.size) {
            if (list[i].title == model.title) {
                list[i].isSelected = !list[i].isSelected
                adapter.notifyItemChanged(i)
                return
            }
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