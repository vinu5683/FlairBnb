package com.masai.flairbnbapp.recyclerviews

import com.masai.flairbnbapp.models.CityModel

interface CityItemInterface {
    fun onCityItemClick(cityModel: CityModel)
    fun getTheDriveTime(cityModel: CityModel): String
}