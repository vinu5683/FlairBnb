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
import com.masai.flairbnbapp.models.ServiceListModel

class ServiceListAdapter(
    val list: ArrayList<ServiceListModel>,
    val serviceListInterface: ServiceListInterface
) : RecyclerView.Adapter<ServiceListViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServiceListViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.service_list_item_layout, parent, false)
        return ServiceListViewHolder(v)
    }

    override fun onBindViewHolder(holder: ServiceListViewHolder, position: Int) {
        val model = list[position]
        holder.setData(model, serviceListInterface)
    }

    override fun getItemCount(): Int {
        return list.size
    }
}

class ServiceListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    val serviceItemIcon: ImageView = itemView.findViewById(R.id.serviceItemIcon)
    val serviceItemParent: ConstraintLayout = itemView.findViewById(R.id.serviceItemParent)
    val serviceItemTitle: TextView = itemView.findViewById(R.id.serviceItemTitle)

    fun setData(
        model: ServiceListModel, serviceListInterface: ServiceListInterface
    ) {
        if (model.isSelected)
            serviceItemParent.setBackgroundResource(R.drawable.roundrectangle_black)
        else
            serviceItemParent.setBackgroundResource(R.drawable.roundrectangle_gray)

        Glide.with(serviceItemIcon.context).load(model.icon).into(serviceItemIcon)
        serviceItemTitle.text = model.title
        serviceItemParent.setOnClickListener {
            serviceListInterface.onServiceSelected(model)
        }
    }

}
