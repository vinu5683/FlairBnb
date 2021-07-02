package com.masai.flairbnbapp.models

data class UserModel(
    val id: String?,
    var firstName: String?,//
    var lastName: String?,//
    var gender: String?,//
    val email: String?,
    val profilePic: String?,
    var contactNumber: String?,//
    var address: String?,//
    val location: String?,
    val token: String?,
    val loginType: String?,
) {
}