package com.masai.flairbnbapp.recyclerviews

import com.google.android.gms.maps.model.LatLng
import com.masai.flairbnbapp.models.RoomModel

interface SearchListInterface {
    fun onSearchItemClick(roomModel: RoomModel,pos:Int)
    fun getLagLang(): LatLng
}