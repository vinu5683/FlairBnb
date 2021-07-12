package com.masai.flairbnbapp.models


data class BookPlaceModel(
    val id: String?,
    val placeId: String?,
    val userId: String?,
    val startDate: String?,
    val endDate: String?,
    val totalDays: String?,
    val status: String?,
    val amount: Long?,
    val transactionId: String?,
    val transactionDate:String?
) {
}