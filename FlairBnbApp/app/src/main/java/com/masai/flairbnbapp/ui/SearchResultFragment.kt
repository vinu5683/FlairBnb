package com.masai.flairbnbapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import androidx.fragment.app.Fragment
import com.masai.flairbnbapp.R


class SearchResultFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search_result, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        val backdropContainer = view.findViewById(R.id.search_result_backdrop) as BackdropContainer
//        val height = this.resources.getDimensionPixelSize(R.dimen.sneek_height)
//
//        backdropContainer
//            .dropInterpolator(LinearInterpolator())
//            .dropHeight(height)
//            .build()
    }

}