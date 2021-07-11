package com.masai.flairbnbapp.recyclerviews

import com.masai.flairbnbapp.models.RoomModel

interface PlacesListInterface {
    fun onHrItemClick(roomModel: RoomModel, pos: Int)
    fun setMarkOnMap(roomModel: RoomModel,pos: Int)
    fun onItemClick(model: RoomModel, adapterPosition: Int)
    fun getTotal(price: String): Int
}