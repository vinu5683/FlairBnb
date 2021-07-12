package com.masai.flairbnbapp.interfaces

import com.masai.flairbnbapp.models.BookPlaceModel

interface OnPaymentInterface {
    fun onPaymentDone(transactionId:String)
}