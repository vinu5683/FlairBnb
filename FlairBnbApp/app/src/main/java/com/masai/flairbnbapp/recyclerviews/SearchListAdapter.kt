package com.masai.flairbnbapp.recyclerviews

import android.location.Location
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.model.LatLng
import com.masai.flairbnbapp.R
import com.masai.flairbnbapp.models.RoomModel

class SearchListAdapter(
    val list: ArrayList<RoomModel>,
    val searchListInterface: SearchListInterface
) :
    RecyclerView.Adapter<SearchListViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchListViewHolder {
        val v =
            LayoutInflater.from(parent.context).inflate(R.layout.search_list_item, parent, false)
        return SearchListViewHolder(v, searchListInterface)
    }

    override fun onBindViewHolder(holder: SearchListViewHolder, position: Int) {
        holder.setData(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }
}

class SearchListViewHolder(itemView: View, val searchListInterface: SearchListInterface) :
    RecyclerView.ViewHolder(itemView) {

    val textview: TextView = itemView.findViewById(R.id.search_list_item_text)
    val cardView: CardView = itemView.findViewById(R.id.search_item_card)
    val latLng: LatLng = searchListInterface.getLagLang()

    fun setData(roomModel: RoomModel) {
        val results = FloatArray(1)
        Location.distanceBetween(
            latLng.latitude,
            latLng.longitude,
            roomModel.location_lat.toString().toDouble(),
            roomModel.location_long.toString().toDouble(),
            results
        )
        results[0] = results[0] / 10
        val res = (results[0].toInt() / 100).toString() + "." + results[0].toInt() % 100
        textview.text = roomModel.title + " - " + roomModel.city + " - " + res + " Km"

        cardView.setOnClickListener {
            searchListInterface.onSearchItemClick(roomModel, adapterPosition)
        }
    }

}
