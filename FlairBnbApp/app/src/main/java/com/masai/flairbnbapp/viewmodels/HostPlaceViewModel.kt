package com.masai.flairbnbapp.viewmodels

import androidx.lifecycle.ViewModel
import com.masai.flairbnbapp.models.RoomModel
import com.masai.flairbnbapp.repository.FlairRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HostPlaceViewModel @Inject constructor(
    val repository: FlairRepository
) : ViewModel() {
    val placesList = repository.roomsModelList
    val roomModel = repository.roomModel
    fun addPlace(roomModel: RoomModel) {
        repository.addPlace(roomModel)
    }

    fun setRoomObject(roomModel: RoomModel) {
        repository.setRoomObject(roomModel)
    }

    fun getTheRoomModel(): RoomModel {
        return roomModel!!
    }
}