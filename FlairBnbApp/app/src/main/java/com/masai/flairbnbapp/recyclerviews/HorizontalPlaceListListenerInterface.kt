package com.masai.flairbnbapp.recyclerviews

import com.masai.flairbnbapp.models.RoomModel

interface HorizontalPlaceListListenerInterface {
    fun onHrItemClick(roomModel: RoomModel, pos: Int)
    fun onItemClick(roomModel: RoomModel, adapterPosition: Int)
}