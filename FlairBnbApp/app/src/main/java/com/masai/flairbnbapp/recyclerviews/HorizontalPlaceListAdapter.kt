package com.masai.flairbnbapp.recyclerviews

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.masai.flairbnbapp.R
import com.masai.flairbnbapp.models.RoomModel

class HorizontalPlaceListAdapter(
    val list: ArrayList<RoomModel>,
    val horizontalPlaceListListenerInterface: HorizontalPlaceListListenerInterface
) : RecyclerView.Adapter<HorizontalPlaceListViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): HorizontalPlaceListViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.horizontal_place_item_layout, parent, false)
        return HorizontalPlaceListViewHolder(v)
    }

    override fun onBindViewHolder(holder: HorizontalPlaceListViewHolder, position: Int) {
        holder.setData(list[position], horizontalPlaceListListenerInterface)
    }

    override fun getItemCount(): Int {
        return list.size
    }
}

class HorizontalPlaceListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    val item_card: CardView
    val item_image_hr: ImageView
    val tvRating_hr: TextView
    val tvTitle_hr: TextView
    val tvSubTitle_hr: TextView
    val tvPrice_hr: TextView
    val tvPriceForWhat_hr: TextView
    val addToWishList_hr: ImageView

    init {
        itemView.apply {
            item_card = findViewById(R.id.item_card)
            item_image_hr = findViewById(R.id.item_image_hr)
            tvRating_hr = findViewById(R.id.tvRating_hr)
            tvTitle_hr = findViewById(R.id.tvTitle_hr)
            tvSubTitle_hr = findViewById(R.id.tvSubTitle_hr)
            tvPrice_hr = findViewById(R.id.tvPrice_hr)
            tvPriceForWhat_hr = findViewById(R.id.tvPriceForWhat_hr)
            addToWishList_hr = findViewById(R.id.addToWishList_hr)
        }
    }

    fun setData(
        roomModel: RoomModel,
        horizontalPlaceListListenerInterface: HorizontalPlaceListListenerInterface
    ) {
        var str =
            "https://i0.wp.com/reviveyouthandfamily.org/wp-content/uploads/2016/11/house-placeholder.jpg?ssl=1"
        if (roomModel.images?.get(0) != null)
            str = roomModel.images[0]
        Glide.with(itemView.context).load(str).into(item_image_hr)
        tvRating_hr.text = roomModel.rating.toString()
        tvTitle_hr.text = roomModel.title.toString()
        tvSubTitle_hr.text = roomModel.description.toString()
        tvPriceForWhat_hr.text = " / " + roomModel.priceForWhat.toString()
        val price: String = if (roomModel.price!! > 999) {
            (roomModel.price!! / 1000).toInt().toString() + "," + (roomModel.price!! % 1000)
        } else
            roomModel.price.toString()
        tvPrice_hr.text = "₹$price"

        //wishlistcode comes here
        //-- - - - - - - - - - -

        item_card.setOnClickListener {
            horizontalPlaceListListenerInterface.onHrItemClick(roomModel, adapterPosition)
        }
    }

}
