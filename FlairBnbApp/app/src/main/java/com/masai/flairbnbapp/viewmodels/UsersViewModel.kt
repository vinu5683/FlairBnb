package com.masai.flairbnbapp.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.masai.flairbnbapp.models.BookPlaceModel
import com.masai.flairbnbapp.models.UserModel
import com.masai.flairbnbapp.repository.FlairRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UsersViewModel @Inject constructor(
    val repository: FlairRepository
) : ViewModel() {

    lateinit var userModel: MutableLiveData<UserModel>
    lateinit var bookPlaceModels: MutableLiveData<ArrayList<BookPlaceModel>>
    var presentOrder: BookPlaceModel? = null

    fun getUser(userId: String): MutableLiveData<UserModel> {
        viewModelScope.launch {
            repository.getUserModel(userId)
            userModel = repository.userModel
        }
        return userModel
    }

    fun saveUserModel(userModel: UserModel) {
        this.userModel.value = userModel
        repository.saveUserModel(userModel)
    }

    fun getAllMyOrders(userId: String): MutableLiveData<ArrayList<BookPlaceModel>> {
        viewModelScope.launch {
            repository.getAllMyOrders(userId)
            bookPlaceModels = repository.bookPlaceModels
        }
        return bookPlaceModels
    }

    fun setMyPresentOrder(order: BookPlaceModel) {
        presentOrder = order
        Log.d("TAG", "setMyPresentOrder: $presentOrder")
    }

    fun getMyPresentOrder(): BookPlaceModel? {
        return presentOrder
    }

}