package com.masai.flairbnbapp.recyclerviews

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.masai.flairbnbapp.R

class ImageListingAdapter(val list: ArrayList<Uri>) :
    RecyclerView.Adapter<ImageListingViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageListingViewHolder {
        val v =
            LayoutInflater.from(parent.context).inflate(R.layout.image_item_layout, parent, false)
        return ImageListingViewHolder(v)
    }

    override fun onBindViewHolder(holder: ImageListingViewHolder, position: Int) {
        val uri = list[position]
        holder.setData(uri, list.size)
    }

    override fun getItemCount(): Int {
        return list.size
    }
}

class ImageListingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val tvImagePosition: TextView = itemView.findViewById(R.id.tvImagePosition)
    val imageItem = itemView.findViewById<ImageView>(R.id.itemImage)

    fun setData(uri: Uri, size: Int) {
        Glide.with(imageItem.context).load(uri).into(imageItem)
        tvImagePosition.text = (adapterPosition + 1).toString() + "/" + size
    }

}
