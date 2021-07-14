package com.masai.flairbnbapp.repository

import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.masai.flairbnbapp.models.BookPlaceModel
import com.masai.flairbnbapp.models.RoomModel
import com.masai.flairbnbapp.models.UserModel
import com.masai.flairbnbapp.models.WishlistModel
import com.masai.flairbnbapp.ui.WishlistsFragment
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashSet


class FlairRepository {

    val presentBookPlaceModel: MutableLiveData<BookPlaceModel> = MutableLiveData()
    val bookPlaceModels: MutableLiveData<ArrayList<BookPlaceModel>> = MutableLiveData()
    val listOfMyWishListPlaces: MutableLiveData<ArrayList<RoomModel>> =
        MutableLiveData<ArrayList<RoomModel>>()
    var selectedRoom: RoomModel = RoomModel()
    val isSaveDone: MutableLiveData<Boolean> = MutableLiveData<Boolean>()
    val allCities: MutableLiveData<HashSet<String>> = MutableLiveData<HashSet<String>>()

    init {
        isSaveDone.value = false
    }

    var imageList: ArrayList<Uri> = ArrayList()
    val dbRootReference = FirebaseDatabase.getInstance()
    var userModel = MutableLiveData<UserModel>()
    val roomsModelList = MutableLiveData<ArrayList<RoomModel>>()
    val searchRoomList = MutableLiveData<ArrayList<RoomModel>>()
    var roomModel: RoomModel? = null
    var roomServiceList: ArrayList<String>? = null
    var downloadImageList: ArrayList<Uri> = ArrayList()
    var counter = 0

    var currentPlaceUser = MutableLiveData<UserModel>()

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
                        getMyWishListData(userId)
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

    fun getAllPlacesList() {
        dbRootReference.getReference("places")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        try {
                            allCities.value?.clear()
                            snapshot.children.forEach {
                                allCities.value?.add(it.child("city").value.toString())
                            }
                        } catch (e: Exception) {

                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
    }

    val listOfPlaces = MutableLiveData<ArrayList<RoomModel>>()

    fun getPlaces(criteria: HashMap<String, String>) {
        val dataList = ArrayList<RoomModel>()
        val city = criteria["city"]
        val state = criteria["state"]
        val country = criteria["country"]

        if (city != null && city != "") {
            dbRootReference.getReference("places")
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            snapshot.children.forEach {
                                if (it.child("city").value?.toString()?.trim()?.lowercase()
                                        .equals(city.trim().lowercase())!!
                                ) {
                                    val x = it.getValue(
                                        RoomModel::class.java
                                    )
                                    if (x != null)
                                        dataList.add(x)
                                }
                            }
                        }
                        listOfPlaces.value = dataList
                    }

                    override fun onCancelled(error: DatabaseError) {

                    }
                })
        } else if (state != null && state != "") {
            dbRootReference.getReference("places")
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            snapshot.children.forEach {
                                if (it.child("state").value?.toString()?.trim()?.lowercase()
                                        .equals(state.trim().lowercase())
                                ) {
                                    val x = it.getValue(
                                        RoomModel::class.java
                                    )
                                    if (x != null)
                                        dataList.add(x)
                                }
                            }
                            listOfPlaces.value = dataList
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {

                    }
                })

        } else {
            dbRootReference.getReference("places")
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            snapshot.children.forEach {
                                if (it.child("country").value?.equals(country)!!) {
                                    val x = it.getValue(
                                        RoomModel::class.java
                                    )
                                    if (x != null)
                                        dataList.add(x)
                                }
                            }
                            listOfPlaces.value = dataList
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {

                    }
                })
        }
    }

    fun search(
        text: String,
        criteria: HashMap<String, String>
    ) {

        val dataList = ArrayList<RoomModel>()
        val city = criteria["city"]
        val state = criteria["state"]
        val country = criteria["country"]

        dbRootReference.getReference("places")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    try {
                        if (snapshot.exists()) {
                            searchRoomList.value?.clear()
                            snapshot.children.forEach {

                                val snapCountry = it.child("country").value?.equals(country)!!
                                val snapCity = it.child("city").value?.equals(city)!!
                                val snapState = it.child("state").value?.equals(state)!!
                                val searchType = it.child("title").value.toString()
                                if ((snapCity || snapCountry || snapState) &&
                                    searchType.toLowerCase().contains(text.trim().toLowerCase())
                                ) {
                                    val x = it.getValue(
                                        RoomModel::class.java
                                    )
                                    if (x != null)
                                        dataList.add(x)
                                }
                            }
                            searchRoomList.value = dataList
                        }
                    } catch (e: Exception) {

                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
    }

    fun getUserNameById(hostId: String?) {
        dbRootReference.getReference("user").child(hostId.toString())
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val userModel = UserModel(
                            firstName = snapshot.child("firstName").value.toString(),
                            lastName = snapshot.child("lastName").value.toString(),
                            id = snapshot.child("id").value.toString(),
                            profilePic = snapshot.child("profilePic").value.toString(),
                            email = snapshot.child("email").value.toString(),
                            address = snapshot.child("address").value.toString(),
                            contactNumber = snapshot.child("contactNumber").value.toString(),
                            token = snapshot.child("contactNumber").value.toString(),
                            gender = null,
                            location = null,
                            loginType = null
                        )
                        currentPlaceUser.value = userModel
                    }

                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
    }

    fun getMyWishListData(uid: String) {
        listOfMyWishListPlaces.value?.clear()
        val x: ArrayList<RoomModel> = ArrayList()
        dbRootReference.getReference("wishlist").child(uid)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        snapshot.children.forEach {
                            val placeId = it.child("placeId").value.toString()
                            dbRootReference.getReference("places").child(placeId)
                                .addListenerForSingleValueEvent(object : ValueEventListener {
                                    override fun onDataChange(snapshot: DataSnapshot) {
                                        if (snapshot.exists()) {
                                            x.add(snapshot.getValue(RoomModel::class.java)!!)
                                        }
                                        listOfMyWishListPlaces.value = x
                                    }

                                    override fun onCancelled(error: DatabaseError) {
                                    }

                                })
                        }

//                        Log.d("TAG", "isnull: ${WishlistsFragment.wishlistData.value}")
//                        Log.d("TAG", "isXnull: $x")
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }

            })
    }

    var isExistInWishlist = MutableLiveData<Boolean>()

    fun addToWishList(uid: String?, id: String?): MutableLiveData<Boolean> {
        isExistInWishlist.value = false;
        dbRootReference.getReference("wishlist").child(uid!!)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.children.forEach {
                        if (it.child("placeId").value == id!!) {
                            isExistInWishlist.value = true
                        }
                    }
                    if (!isExistInWishlist.value!!) {
                        val x = WishlistModel(uid, id)
                        dbRootReference.getReference("wishlist").child(uid).child(id!!)
                            .setValue(x)
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
        return isExistInWishlist
    }

    fun bookRoom(bookPlaceModel: BookPlaceModel) {
        dbRootReference.getReference("orders").child(bookPlaceModel.userId!!)
            .child(bookPlaceModel.id!!)
            .setValue(bookPlaceModel).addOnCompleteListener {
                if (it.isSuccessful) {
                    Log.d("TAG", "bookRoom: Payment Done of ${bookPlaceModel.amount}")
                } else {
                    Log.d("TAG", "bookRoom: Payment Not Done for ${bookPlaceModel.amount}")
                }
            }
    }

    fun getAllMyOrders(userId: String) {
        dbRootReference.getReference("orders").child(userId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val x = ArrayList<BookPlaceModel>()
                        snapshot.children.forEach {
                            x.add(it.getValue(BookPlaceModel::class.java)!!)
                        }
                        bookPlaceModels.value = x
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }

            })
    }

    val tempPlace = MutableLiveData<RoomModel>()

    fun getPlaceById(id: String?) {
        dbRootReference.getReference("places").child(id!!)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val x = snapshot.getValue(RoomModel::class.java)
                        tempPlace.value = x
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
    }

    fun getInvoiceById(uid: String, invoiceId: String) {

        dbRootReference.getReference("orders").child(uid).child(invoiceId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val x = snapshot.getValue(BookPlaceModel::class.java)
                        presentBookPlaceModel.value = x
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }

            })
    }
}

