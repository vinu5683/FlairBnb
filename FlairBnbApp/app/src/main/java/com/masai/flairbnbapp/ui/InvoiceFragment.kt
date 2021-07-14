package com.masai.flairbnbapp.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.masai.flairbnbapp.R
import com.masai.flairbnbapp.localdatabases.LocalKeys
import com.masai.flairbnbapp.localdatabases.PreferenceHelper
import com.masai.flairbnbapp.models.BookPlaceModel
import com.masai.flairbnbapp.viewmodels.PlacesViewModel
import com.masai.flairbnbapp.viewmodels.UsersViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class InvoiceFragment : Fragment() {

    val userViewModel by viewModels<UsersViewModel>()
    val placesViewModel by viewModels<PlacesViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_invoice, container, false)
    }

    lateinit var navController: NavController
    lateinit var v: View

    lateinit var selectedOrder: BookPlaceModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        PreferenceHelper.getSharedPreferences(view.context)
        navController = Navigation.findNavController(view)
        v = view

        if (myPresentId != null) {
            placesViewModel.getInvoiceById(
                PreferenceHelper.readStringFromPreference(LocalKeys.KEY_USER_GOOGLE_ID),
                myPresentId!!
            ).observe(viewLifecycleOwner, {
                if (it == null) {
                    Toast.makeText(view.context, "This invoice not exist", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    selectedOrder = it
                    initViews(view)
                    setData(view)
                }
            })
        }

    }

    //btn
    lateinit var tvEmailReceipt: TextView
    lateinit var tvPrintReceipt: TextView

    lateinit var receiptDate: TextView
    lateinit var tvGuestName_invoice: TextView
    lateinit var tvPlaceName_invoice: TextView
    lateinit var PlaceAddress_invoice: TextView
    lateinit var tvAccommodationType_invoice: TextView
    lateinit var tvNumberOfNights: TextView
    lateinit var tvCheckinDate: TextView
    lateinit var tvCheckOutDate: TextView
    lateinit var paymentDate: TextView
    lateinit var tvPricePerNight_invoice: TextView
    lateinit var tvTotalPrice_invoice: TextView
    lateinit var tvTransactionId: TextView

    private fun initViews(view: View) {
        view.apply {
            tvEmailReceipt = findViewById(R.id.tvEmailReceipt)
            tvPrintReceipt = findViewById(R.id.tvPrintReceipt)
            receiptDate = findViewById(R.id.receiptDate)
            tvGuestName_invoice = findViewById(R.id.tvGuestName_invoice)
            tvPlaceName_invoice = findViewById(R.id.tvPlaceName_invoice)
            PlaceAddress_invoice = findViewById(R.id.PlaceAddress_invoice)
            tvAccommodationType_invoice = findViewById(R.id.tvAccommodationType_invoice)
            tvNumberOfNights = findViewById(R.id.tvNumberOfNights)
            tvCheckinDate = findViewById(R.id.tvCheckinDate)
            tvCheckOutDate = findViewById(R.id.tvCheckOutDate)
            paymentDate = findViewById(R.id.paymentDate)
            tvPricePerNight_invoice = findViewById(R.id.tvPricePerNight_invoice)
            tvTotalPrice_invoice = findViewById(R.id.tvTotalPrice_invoice)
            tvTransactionId = findViewById(R.id.tvTransactionId)
        }
    }

    private fun setData(view: View) {
        val sdf = SimpleDateFormat("yyyy.MM.dd hh:mm:ss");
        val sd = SimpleDateFormat("yyyy.MM.dd");

        receiptDate.text = sdf.format(Date(selectedOrder.transactionDate?.toLong()!!)).toString()
        userViewModel.getUser(selectedOrder.userId!!).observe(viewLifecycleOwner, {
            tvGuestName_invoice.text = it.firstName + it.lastName
        })

        placesViewModel.getPlaceById(selectedOrder.placeId!!).observe(viewLifecycleOwner, {
            tvPlaceName_invoice.text = it.title
            PlaceAddress_invoice.text = it.city + "," + it.state + "," + it.country
            tvAccommodationType_invoice.text = it.subCategory + " " + it.roomSpaceType
            tvPricePerNight_invoice.text = it.price.toString()
        })
        tvNumberOfNights.text = selectedOrder.totalDays
        tvCheckinDate.text = sd.format(Date(selectedOrder.startDate?.toLong()!!)).toString()
        tvCheckOutDate.text = sd.format(Date(selectedOrder.endDate?.toLong()!!)).toString()
        paymentDate.text = sdf.format(Date(selectedOrder.transactionDate?.toLong()!!)).toString()
        tvTotalPrice_invoice.text = selectedOrder.amount.toString()
        tvTransactionId.text = selectedOrder.transactionId
    }

    companion object {
        var myPresentId: String? = null
    }


}