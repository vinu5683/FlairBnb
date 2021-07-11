package com.masai.flairbnbapp.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
}