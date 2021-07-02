package com.masai.flairbnbapp.viewmodels

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.masai.flairbnbapp.models.RoomModel
import com.masai.flairbnbapp.models.ServiceListModel
import com.masai.flairbnbapp.repository.FlairRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.ArrayList
import javax.inject.Inject

@HiltViewModel
class HostPlaceViewModel @Inject public constructor(
    val repository: FlairRepository
) : ViewModel() {

    var isSaveDone: MutableLiveData<Boolean> = repository.isSaveDone

    var imageList = repository.imageList
    val placesList = repository.roomsModelList
    val roomModel = repository.roomModel
    var serviceList = repository.roomServiceList


    fun addPlace(roomModel: RoomModel) {
        repository.addPlace(roomModel)
    }

    fun setSaveDone(b: Boolean) {
        repository.setSaveDone(false)
    }

    fun setRoomObject(roomModel: RoomModel) {
        repository.setRoomObject(roomModel)
    }

    fun setImageListObject(imageList: ArrayList<Uri>) {
        repository.setImageListObject(imageList)
    }

    fun getTheRoomModel(): RoomModel {
        return roomModel!!
    }

    fun setServices(list: ArrayList<ServiceListModel>) {
        val l = ArrayList<String>()
        for (i in 0 until list.size) {
            if (list[i].isSelected) {
                l.add(list[i].title)
            }
        }
        repository.setServiceList(l)

    }
}