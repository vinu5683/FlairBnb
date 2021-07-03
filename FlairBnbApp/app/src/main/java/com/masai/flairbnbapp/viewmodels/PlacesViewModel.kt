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
class PlacesViewModel @Inject public constructor(
    val repository: FlairRepository
) : ViewModel() {

    var listOfPlaces = MutableLiveData<ArrayList<RoomModel>>()

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

}