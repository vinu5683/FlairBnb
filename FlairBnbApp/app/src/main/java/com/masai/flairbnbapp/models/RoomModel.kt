package com.masai.flairbnbapp.models

data class RoomModel(
    val id: String?,
    var title: String?,
    var description: String?,
    var category: String?,
    var roomSpaceType:String?,
    var subCategory:String?,
    var price: Long?,
    var priceForWhat: String?,
    var location_lat: String?,
    var location_long: String?,
    var host_id: String?,
    val listOfAvailableServices: String?,
    val rating: Int?,
    var rooms: Int?,
    var total_capacity: Int?,
    var total_bathrooms: Int?,
    var beds: Int?,
    val maxMembers: Int?,
    var contactNo: String?,
    var city: String?,
    var state: String?,
    var country: String?,
)