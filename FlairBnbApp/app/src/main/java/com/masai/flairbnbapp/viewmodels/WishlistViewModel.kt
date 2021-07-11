package com.masai.flairbnbapp.viewmodels

import androidx.lifecycle.ViewModel
import com.masai.flairbnbapp.repository.FlairRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class WishlistViewModel @Inject public constructor(
    val repository: FlairRepository
) : ViewModel() {



}