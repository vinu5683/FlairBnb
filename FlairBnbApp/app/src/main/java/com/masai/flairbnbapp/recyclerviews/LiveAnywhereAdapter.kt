package com.masai.flairbnbapp.recyclerviews

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.masai.flairbnbapp.R
import com.masai.flairbnbapp.models.LiveAnywhereModel

class LiveAnywhereAdapter(
    val list: ArrayList<LiveAnywhereModel>,
    val liveAnywhereInterface: LiveAnywhereInterface
) : RecyclerView.Adapter<LiveAnywhereViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LiveAnywhereViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.live_anywhere_item_layout, parent, false)
        return LiveAnywhereViewHolder(v)
    }

    override fun onBindViewHolder(holder: LiveAnywhereViewHolder, position: Int) {
        holder.setData(list[position], liveAnywhereInterface)
    }

    override fun getItemCount(): Int {
        return list.size
    }

}

class LiveAnywhereViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    val tvTitle: TextView = itemView.findViewById(R.id.tvTitleLiveAnywhere)
    val ivItem: ImageView = itemView.findViewById(R.id.liveAnywhereImage)
    val layoutItem: ConstraintLayout = itemView.findViewById(R.id.liveAnywhereItem)
    fun setData(
        liveAnywhereModel: LiveAnywhereModel,
        liveAnywhereInterface: LiveAnywhereInterface
    ) {
        tvTitle.text = liveAnywhereModel.title
        Glide.with(itemView.context).load(liveAnywhereModel.pic_id).into(ivItem)
        layoutItem.setOnClickListener {
            liveAnywhereInterface.onItemClick(liveAnywhereModel)
        }
    }

}
