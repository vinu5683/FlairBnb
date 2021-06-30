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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.masai.flairbnbapp.R
import com.masai.flairbnbapp.localdatabases.LocalKeys
import com.masai.flairbnbapp.models.RoomModel
import com.masai.flairbnbapp.models.SubCategoryModel
import com.masai.flairbnbapp.recyclerviews.SubCategoryAdapter
import com.masai.flairbnbapp.recyclerviews.SubCategoryInterface
import com.masai.flairbnbapp.viewmodels.HostPlaceViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HostPart3Fragment : Fragment(), SubCategoryInterface {
    private val pvm by viewModels<HostPlaceViewModel>()
    var anim: AnimationDrawable? = null
    var list = ArrayList<SubCategoryModel>()
    lateinit var roomModel: RoomModel
    var lock = false
    var adapter = SubCategoryAdapter(list, this)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_host_part3, container, false)
    }

    lateinit var v: View
    lateinit var rvPart3: RecyclerView
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val navController = Navigation.findNavController(view)
        v = view
        setAnimation(view)
        initViews(view)
        roomModel = pvm.getTheRoomModel()
        list.clear()
        getListType()
        rvPart3 = view.findViewById<RecyclerView>(R.id.rvPart3);
        setRecyclerView(view)

        view.findViewById<Button>(R.id.btnNext4).setOnClickListener {
            pvm.setRoomObject(roomModel)
            Log.d("TAG", "onViewCreated: "+roomModel.toString())
            navController.navigate(R.id.action_hostPart3Fragment_to_hostPart4Fragment)
        }

    }

    private fun getListType() {
        when (roomModel.category) {
            "Flat" -> list = LocalKeys.getFlatSubCategoryList()
            "House" -> list = LocalKeys.getHouseSubCategoryList()
            "Unique space" -> list = LocalKeys.getUniqueSubCategoryList()
            "Secondary unit" -> list = LocalKeys.getSecondarySubCategoryList()
        }
    }

    private fun setRecyclerView(view: View) {
        rvPart3.layoutManager = LinearLayoutManager(view.context)
        adapter = SubCategoryAdapter(list, this)
        rvPart3.adapter = adapter
        adapter.notifyDataSetChanged()
    }

    private fun setAnimation(view: View) {
        val constraintLayout = view.findViewById<ConstraintLayout>(R.id.part3Container)
        anim = constraintLayout.background as AnimationDrawable
        anim?.setEnterFadeDuration(5000)
        anim?.setExitFadeDuration(2000)
    }

    private fun initViews(view: View) {

    }

    override fun onResume() {
        super.onResume()
        if (anim != null && !anim!!.isRunning) anim!!.start()
    }

    override fun onPause() {
        super.onPause()
        if (anim != null && anim!!.isRunning) anim!!.stop()
    }

    override fun onClickEvent(subCategoryModel: SubCategoryModel) {

        for (i in 0 until list.size) {
            if (list[i].isSelected) {
                list[i].isSelected = false
                adapter.notifyItemChanged(i)
            }
        }
        for (i in 0 until list.size) {
            if (list[i].title == subCategoryModel.title) {
                list[i].isSelected = true
                lock = true
                roomModel.subCategory = list[i].title
                adapter.notifyItemChanged(i)
                return
            }
        }
    }
}