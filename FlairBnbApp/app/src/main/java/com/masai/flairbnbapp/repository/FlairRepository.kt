package com.masai.flairbnbapp.repository

import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.FirebaseDatabase
import com.masai.flairbnbapp.models.UserModel


class FlairRepository {

    val dbRootReference = FirebaseDatabase.getInstance()
    val userModel = MutableLiveData<UserModel>()

    init {

    }


}