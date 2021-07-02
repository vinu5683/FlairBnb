package com.masai.flairbnbapp.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.masai.flairbnbapp.models.RoomModel
import com.masai.flairbnbapp.models.ServiceListModel
import com.masai.flairbnbapp.models.UserModel


class FlairRepository {

    val dbRootReference = FirebaseDatabase.getInstance()
    var userModel = MutableLiveData<UserModel>()
    val roomsModelList = MutableLiveData<ArrayList<RoomModel>>()
    var roomModel: RoomModel? = null
    var roomServiceList: ArrayList<String>? = null

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

    fun setServices(id: String?, l: java.util.ArrayList<String>) {
        dbRootReference.getReference("places").child(id!!).setValue(l).addOnCompleteListener {
            if (it.isSuccessful) {
                Log.d("TAG", "setServices: Success")
            } else
                Log.d("TAG", "setServices: Failed")
        }
    }

    fun getUserModel(userId: String) {

        FirebaseDatabase.getInstance().getReference("users").child(userId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        userModel.value = UserModel(
                            snapshot.child("id").value.toString(),
                            snapshot.child("firstName").value.toString(),
                            snapshot.child("lastName").value.toString(),
                            snapshot.child("gender").value.toString(),
                            snapshot.child("email").value.toString(),
                            snapshot.child("profilePic").value.toString(),
                            snapshot.child("contactNumber").value.toString(),
                            snapshot.child("address").value.toString(),
                            snapshot.child("location").value.toString(),
                            snapshot.child("token").value.toString(),
                            snapshot.child("loginType").value.toString(),
                        )
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
    }

    fun saveUserModel(userModel: UserModel) {
        dbRootReference.getReference("users").child(userModel.id.toString()).setValue(userModel)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    Log.d("TAG", "saveUserModel: Succ")
                } else Log.d("TAG", "saveUserModel: fail")

            }
    }
}