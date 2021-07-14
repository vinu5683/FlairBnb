package com.masai.flairbnbapp.recyclerviews

import android.graphics.Paint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
//import com.denzcoskun.imageslider.ImageSlider
//import com.denzcoskun.imageslider.models.SlideModel
import com.masai.flairbnbapp.R
import com.masai.flairbnbapp.localdatabases.LocalKeys
import com.masai.flairbnbapp.localdatabases.PreferenceHelper
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
            return
        }
        val myAdapter = ImageLoopAdapter(model.images)
        carouselViewItem.setSliderAdapter(myAdapter)

        place_rating.text = model.rating.toString()
        place_name.text = model.title.toString()
        shortdiscription.text = model.description.toString()
        val total = placesListInterface.getTotal(model.price.toString());

        val price: String = if (model.price!! > 999) {
            val x = (model.price!!.toString().reversed().substring(0, 3) + ",").reversed()
            val y = model.price!!.toString().reversed().substring(3).reversed()
            y + x
        } else
            model.price.toString()
        placeItemContainer.setOnClickListener {
            placesListInterface.onItemClick(model, adapterPosition)
        }
        if (total == 0) {
            place_price.text = "₹${price}"
            priceforwhat.text = model.priceForWhat.toString()
            Log.d("TAG", "setData: from wishlist $model ")
            return
        }
        totalPrice.text =
            "Total ₹$total (${PreferenceHelper.readIntFromPreference(LocalKeys.NUMBER_OF_DAYS)} days)"
        totalPrice.paintFlags = totalPrice.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        place_price.text = "₹${price}"
        priceforwhat.text = model.priceForWhat.toString()
        placesListInterface.setMarkOnMap(model, adapterPosition)
    }


}
