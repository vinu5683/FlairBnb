package com.masai.flairbnbapp.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.masai.flairbnbapp.models.RoomModel
import com.masai.flairbnbapp.repository.FlairRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WishlistViewModel @Inject public constructor(
    val repository: FlairRepository
) : ViewModel() {

    var listOfMyWishListPlaces = MutableLiveData<ArrayList<RoomModel>>()

    fun getMyWishListData(uid: String): MutableLiveData<ArrayList<RoomModel>> {
        repository.getMyWishListData(uid);
        viewModelScope.launch {
            listOfMyWishListPlaces = repository.listOfMyWishListPlaces
        }

        return listOfMyWishListPlaces
    }

    fun addToWishList(uid: String?, id: String?): MutableLiveData<Boolean> {
        return repository.addToWishList(uid, id)
    }


}