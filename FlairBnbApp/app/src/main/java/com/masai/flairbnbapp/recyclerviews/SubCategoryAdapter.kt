package com.masai.flairbnbapp.recyclerviews

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.masai.flairbnbapp.R
import com.masai.flairbnbapp.models.SubCategoryModel

class SubCategoryAdapter(
    val list: ArrayList<SubCategoryModel>,
    val subCategoryInterface: SubCategoryInterface
) :
    RecyclerView.Adapter<SubCategoryViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubCategoryViewHolder {
        val v =
            LayoutInflater.from(parent.context).inflate(R.layout.subcategory_item, parent, false)
        return SubCategoryViewHolder(v)
    }

    override fun onBindViewHolder(holder: SubCategoryViewHolder, position: Int) {
        val subCategoryModel = list[position]
        holder.setData(subCategoryModel, subCategoryInterface)
    }

    override fun getItemCount(): Int {
        return list.size
    }
}

class SubCategoryViewHolder(itemView: View) :
    RecyclerView.ViewHolder(itemView) {
    val ll_sub_cat: LinearLayout = itemView.findViewById(R.id.ll_sub_cat)
    val title: TextView = itemView.findViewById(R.id.mainTitle)
    val subTitle: TextView = itemView.findViewById(R.id.subtitle)

    fun setData(subCategoryModel: SubCategoryModel, subCategoryInterface: SubCategoryInterface) {
        if (subCategoryModel.isSelected) {
            ll_sub_cat.setBackgroundResource(R.drawable.roundrectangle_black)
        } else {
            ll_sub_cat.setBackgroundResource(R.drawable.roundrectangle_gray)
        }
        title.text = subCategoryModel.title
        subTitle.text = subCategoryModel.subTitle

        ll_sub_cat.setOnClickListener {
            subCategoryInterface.onClickEvent(subCategoryModel)
        }

    }

}
