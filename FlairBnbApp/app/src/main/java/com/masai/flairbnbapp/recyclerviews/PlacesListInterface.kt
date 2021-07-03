package com.masai.flairbnbapp.recyclerviews

import com.masai.flairbnbapp.models.RoomModel

interface PlacesListInterface {
    fun onItemClick(roomModel: RoomModel, pos: Int)
    fun setMarkOnMap(roomModel: RoomModel,pos: Int)
}