package com.masai.flairbnbapp.models

data class RoomModel(
    val id: String?,
    val title: String?,
    val description: String?,
    var category: String?,
    var roomSpaceType:String?,
    var subCategory:String?,
    val price: Long?,
    val priceForWhat: String?,
    val image_blob_id: String?,
    var location_lat: String?,
    var location_long: String?,
    val host_id: String?,
    val listOfAvailableServices: String?,
    val rating: Int?,
    val rooms: Int?,
    var total_capacity: Int?,
    var total_bathrooms: Int?,
    var beds: Int?,
    val maxMembers: Int?,
    val contactNo: String?,
    val city: String?,
    val state: String?,
    val country: String?,
)