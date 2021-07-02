package com.masai.flairbnbapp.repository

import android.net.Uri
import android.util.Log
import android.webkit.MimeTypeMap
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.masai.flairbnbapp.models.RoomModel
import com.masai.flairbnbapp.models.ServiceListModel
import com.masai.flairbnbapp.models.UserModel
import java.util.*
import kotlin.collections.ArrayList


class FlairRepository {

    val isSaveDone: MutableLiveData<Boolean> = MutableLiveData<Boolean>()

    init {
        isSaveDone.value = false
    }

    var imageList: ArrayList<Uri> = ArrayList()
    val dbRootReference = FirebaseDatabase.getInstance()
    var userModel = MutableLiveData<UserModel>()
    val roomsModelList = MutableLiveData<ArrayList<RoomModel>>()
    var roomModel: RoomModel? = null
    var roomServiceList: ArrayList<String>? = null
    var downloadImageList: ArrayList<Uri> = ArrayList()
    var counter = 0
    fun setRoomObject(room: RoomModel) {
        roomModel = room
    }

    fun addPlace(roomModel: RoomModel) {
        addPlaceBasicDetials(roomModel)
        addPlaceServiceModel(roomModel)
        addPlaceImagesModel(roomModel)
    }

    private fun addPlaceImagesModel(roomModel: RoomModel) {
        downloadImageList.clear()
        for (i in 0 until imageList.size) {
            val uri: Uri = imageList[i]
            uploadTask(roomModel, uri, roomModel.id, i)
        }

    }

    private fun uploadTask(roomModel: RoomModel, uri: Uri, shopId: String?, i: Int) {
        val storageReference =
            FirebaseStorage.getInstance().getReference("placepics").child(shopId!!)
                .child(UUID.randomUUID().toString() + ".jpg")
        Log.d("TAG", "uploadTask: ongoinig")
        storageReference
            .putFile(uri)
            .continueWithTask { task ->
                if (!task.isSuccessful) {
                    throw task.exception!!
                }
                Log.d("TAG", "uploadTask: " + storageReference.downloadUrl)
                storageReference.downloadUrl
            }.addOnCompleteListener {
                if (it.isSuccessful) {
                    val downloadUri = it.result
                    Log.d("TAG", "uploadTask: " + downloadUri)
                    downloadImageList.add(downloadUri)
                    uploadToRealTimeDatabase(roomModel, downloadUri, i)
                    if (i + 1 >= imageList.size) {
                        setSaveDone(true)
                    }
                }
            }
    }

    fun setSaveDone(b: Boolean) {
        isSaveDone.value = b
    }

    private fun uploadToRealTimeDatabase(roomModel: RoomModel, downloadUri: Uri?, i: Int) {
        dbRootReference.getReference("places").child(roomModel.id.toString())
            .child("images").child(i.toString()).setValue(downloadUri.toString())
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    Log.d("TAG", "addPlaceImages: Success " + downloadUri.toString())
                } else
                    Log.d("TAG", "addPlaceImages: Failed")
            }
    }


    private fun addPlaceServiceModel(roomModel: RoomModel) {
        dbRootReference.getReference("places").child(roomModel.id.toString())
            .child("services").setValue(roomServiceList).addOnCompleteListener {
                if (it.isSuccessful) {
                    Log.d("TAG", "addPlaceService: Success")
                } else
                    Log.d("TAG", "addPlaceService: Failed")
            }
    }

    private fun addPlaceBasicDetials(roomModel: RoomModel) {
        dbRootReference.getReference("places").child(
            roomModel.id.toString()
        ).setValue(roomModel).addOnCompleteListener {
            if (it.isSuccessful) {
                Log.d("TAG", "addPlace: Success")
            } else
                Log.d("TAG", "addPlace: Failed")
        }
    }

    fun setImageListObject(imageList: ArrayList<Uri>) {
        this.imageList = imageList
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

    fun setServiceList(l: java.util.ArrayList<String>) {
        roomServiceList = l
    }
}