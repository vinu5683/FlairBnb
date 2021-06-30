package com.masai.flairbnbapp.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.FirebaseDatabase
import com.masai.flairbnbapp.models.RoomModel
import com.masai.flairbnbapp.models.UserModel


class FlairRepository {


    val dbRootReference = FirebaseDatabase.getInstance()
    val userModel = MutableLiveData<UserModel>()
    val roomsModelList = MutableLiveData<ArrayList<RoomModel>>()
    var roomModel: RoomModel? = null

    fun setRoomObject(room: RoomModel) {
        roomModel = room
    }

    fun addPlace(roomModel: RoomModel) {
        dbRootReference.getReference("places").child(
            roomModel.id.toString()
        ).setValue(roomModel).addOnCompleteListener {
            if (it.isSuccessful) {
                Log.d("TAG", "addPlace: Success")
            } else
                Log.d("TAG", "addPlace: Failed")
        }
    }
}