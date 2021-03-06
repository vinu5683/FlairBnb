package com.masai.flairbnbapp.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.masai.flairbnbapp.models.BookPlaceModel
import com.masai.flairbnbapp.models.RoomModel
import com.masai.flairbnbapp.models.UserModel
import com.masai.flairbnbapp.repository.FlairRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlacesViewModel @Inject public constructor(
    val repository: FlairRepository
) : ViewModel() {

    var listOfPlaces = MutableLiveData<ArrayList<RoomModel>>()
    var currentPlaceUser = MutableLiveData<UserModel>()
    var presentBillPlace = MutableLiveData<RoomModel>()
    var presentBookPlaceModel: MutableLiveData<BookPlaceModel> = MutableLiveData()

    fun getTheSelectedRoom(): RoomModel {
        return repository.selectedRoom
    }

    fun getInvoiceById(uid: String, invoiceId: String): MutableLiveData<BookPlaceModel> {
        repository.getInvoiceById(uid, invoiceId);
        viewModelScope.launch {
            presentBookPlaceModel = repository.presentBookPlaceModel
        }
        return presentBookPlaceModel
    }

    fun getListOfPlaces(
        criteria: HashMap<String, String>,
    ): MutableLiveData<ArrayList<RoomModel>> {
        repository.getPlaces(criteria)
        viewModelScope.launch {
            listOfPlaces =
                repository.listOfPlaces
        }
        return listOfPlaces
    }

    fun search(
        text: String,
        hashCriteria: java.util.HashMap<String, String>
    ): MutableLiveData<ArrayList<RoomModel>> {
        repository.search(text, hashCriteria)
        return repository.searchRoomList
    }

    fun getPlaceById(placeId: String): MutableLiveData<RoomModel> {
        viewModelScope.launch {
            repository.getPlaceById(placeId)
            presentBillPlace = repository.tempPlace
        }
        return presentBillPlace
    }

    fun setTheSelectedRoomModel(roomModel: RoomModel) {
        repository.selectedRoom = roomModel
    }

    fun getUserNameById(hostId: String?) {
        repository.getUserNameById(hostId)
        currentPlaceUser = repository.currentPlaceUser
    }

    fun bookroom(bookPlaceModel: BookPlaceModel) {
        presentBookPlaceModel.value = bookPlaceModel
        repository.bookRoom(bookPlaceModel)
    }

}