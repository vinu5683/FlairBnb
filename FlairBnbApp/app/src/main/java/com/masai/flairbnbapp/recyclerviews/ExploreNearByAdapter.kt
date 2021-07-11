package com.masai.flairbnbapp.recyclerviews

import android.icu.text.DateTimePatternGenerator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.masai.flairbnbapp.R
import com.masai.flairbnbapp.models.CityModel

class ExploreNearByAdapter(
    val list: ArrayList<CityModel>,
    val cityItemInterface: CityItemInterface
) :
    RecyclerView.Adapter<ExploreNearByViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExploreNearByViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(
            R.layout.city_item_layout, parent, false
        )
        return ExploreNearByViewHolder(v)
    }

    override fun onBindViewHolder(holder: ExploreNearByViewHolder, position: Int) {
        holder.setData(list[position], cityItemInterface, list.size)
    }

    override fun getItemCount(): Int {
        return list.size
    }
}

class ExploreNearByViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    val container: ConstraintLayout = itemView.findViewById(R.id.cityItemContainer)
    val tvCityName: TextView = itemView.findViewById(R.id.cityNameItem)
    val tvDriveDistance: TextView = itemView.findViewById(R.id.cityDistanceItem)
    val cityImage: ImageView = itemView.findViewById(R.id.cityImageItem)

    fun setData(cityModel: CityModel, cityItemInterface: CityItemInterface, size: Int) {
        tvCityName.text = cityModel.name
        Glide.with(itemView.context).load(cityModel.iconId).into(cityImage)
        tvDriveDistance.text = cityItemInterface.getTheDriveTime(cityModel)
        container.setOnClickListener {
            cityItemInterface.onCityItemClick(cityModel)
        }
        if (adapterPosition > 1) {
            container.layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
        }
    }

}
