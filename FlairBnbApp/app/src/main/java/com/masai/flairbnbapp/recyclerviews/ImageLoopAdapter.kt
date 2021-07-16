package com.masai.flairbnbapp.recyclerviews

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.masai.flairbnbapp.R
import com.smarteist.autoimageslider.SliderViewAdapter

class ImageLoopAdapter(val list: ArrayList<String>) :
    SliderViewAdapter<ImageLoopAdapter.MyViewHolder>() {

    class MyViewHolder(itemView: View?) : SliderViewAdapter.ViewHolder(itemView) {
        fun setData(s: String) {

            Glide.with(itemView.context).load(s).into(iv!!)
        }

        val iv: ImageView?

        init {
            iv = itemView?.findViewById(R.id.imgView_item)
        }
    }

    override fun getCount(): Int {
        return list.size
    }

    override fun onCreateViewHolder(parent: ViewGroup?): MyViewHolder {
        val v = LayoutInflater.from(parent?.context).inflate(R.layout.image_item, parent, false)
        return MyViewHolder(v)
    }

    override fun onBindViewHolder(viewHolder: MyViewHolder?, position: Int) {
        if (list[position] == null)
            return
        viewHolder?.setData(list[position])
    }
}

