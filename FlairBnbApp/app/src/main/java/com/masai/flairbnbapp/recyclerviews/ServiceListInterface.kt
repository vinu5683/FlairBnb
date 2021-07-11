package com.masai.flairbnbapp.recyclerviews

import com.masai.flairbnbapp.models.ServiceListModel

interface ServiceListInterface {
    fun onServiceSelected(model: ServiceListModel)
}