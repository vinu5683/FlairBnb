package com.masai.flairbnbapp.recyclerviews

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
//import com.denzcoskun.imageslider.ImageSlider
//import com.denzcoskun.imageslider.models.SlideModel
import com.masai.flairbnbapp.R
import com.masai.flairbnbapp.models.RoomModel
import com.smarteist.autoimageslider.SliderView

class PlacesListAdapter(
    val list: ArrayList<RoomModel>,
    val placesListInterface: PlacesListInterface
) :
    RecyclerView.Adapter<PlacesListViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlacesListViewHolder {
        val v =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.place_item_with_carousel_layout, parent, false)
        return PlacesListViewHolder(v)
    }

    override fun onBindViewHolder(holder: PlacesListViewHolder, position: Int) {
        val model = list[position]
        holder.setData(model, placesListInterface)
    }

    override fun getItemCount(): Int {
        return list.size
    }
}

class PlacesListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    var carouselViewItem: SliderView
    var placeItemContainer: ConstraintLayout
    var place_rating: TextView
    var place_name: TextView
    var shortdiscription: TextView
    var place_price: TextView
    var priceforwhat: TextView
    var totalPrice: TextView


    init {
        itemView.apply {
            carouselViewItem = findViewById(R.id.carouselViewItem)
            placeItemContainer = findViewById(R.id.placeItemContainer)
            place_rating = findViewById(R.id.place_rating)
            place_name = findViewById(R.id.place_name)
            shortdiscription = findViewById(R.id.shortdiscription)
            place_price = findViewById(R.id.place_price)
            priceforwhat = findViewById(R.id.priceforwhat)
            totalPrice = findViewById(R.id.totalPrice)
        }
    }

    fun setData(model: RoomModel, placesListInterface: PlacesListInterface) {
        if (model.images == null) {
            Log.d("TAG", "setData: No Images for model \n" + model.toString())
            return
        }

        val myAdapter = ImageLoopAdapter(model.images)
        carouselViewItem.setSliderAdapter(myAdapter)

        place_rating.text = model.rating.toString()
        place_name.text = model.title.toString()
        shortdiscription.text = model.description.toString()
        place_price.text = model.price.toString()
        priceforwhat.text = model.priceForWhat.toString()
        totalPrice.text = model.price.toString()
        placesListInterface.setMarkOnMap(model, adapterPosition)

        placeItemContainer.setOnClickListener {
            placesListInterface.onItemClick(model, adapterPosition)
        }
    }


}
