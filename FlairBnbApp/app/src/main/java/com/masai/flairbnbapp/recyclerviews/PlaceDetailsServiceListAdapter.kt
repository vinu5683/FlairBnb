package com.masai.flairbnbapp.recyclerviews

import android.media.Image
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.masai.flairbnbapp.R
import com.masai.flairbnbapp.localdatabases.LocalKeys

class PlaceDetailsServiceListAdapter(val list: ArrayList<String>) :
    RecyclerView.Adapter<PlaceDetailsServiceViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PlaceDetailsServiceViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.service_item_in_place_details, parent, false);
        return PlaceDetailsServiceViewHolder(v)
    }

    override fun onBindViewHolder(holder: PlaceDetailsServiceViewHolder, position: Int) {
        holder.setData(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }
}

class PlaceDetailsServiceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    val item_service_icon: ImageView = itemView.findViewById<ImageView>(R.id.item_service_icon)
    val serviceName: TextView = itemView.findViewById<TextView>(R.id.item_service_name)

    fun setData(s: String) {
        serviceName.text = s
        val id = LocalKeys.getServiceIconIdByName(s)
        Glide.with(itemView.context).load(id).into(item_service_icon)
    }


}
