package com.masai.flairbnbapp.models


data class BookPlaceModel public constructor(
    val id: String?,
    val placeId: String?,
    val userId: String?,
    val startDate: String?,
    val endDate: String?,
    val totalDays: String?,
    val status: String?,
    val amount: Long?,
    val transactionId: String?,
    val transactionDate: String?,
) {

    constructor() : this(
        null, null, null, null, null, null,
        null, null, null, null,
    )

}
//101137671152378241083
//112684305281247950673
//113149173757340186719