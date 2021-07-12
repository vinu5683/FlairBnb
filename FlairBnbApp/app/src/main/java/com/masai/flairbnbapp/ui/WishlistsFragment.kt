package com.masai.flairbnbapp.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.util.Pair
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.masai.flairbnbapp.R
import com.masai.flairbnbapp.localdatabases.LocalKeys
import com.masai.flairbnbapp.localdatabases.PreferenceHelper
import com.masai.flairbnbapp.models.RoomModel
import com.masai.flairbnbapp.recyclerviews.PlacesListAdapter
import com.masai.flairbnbapp.recyclerviews.PlacesListInterface
import com.masai.flairbnbapp.viewmodels.PlacesViewModel
import com.masai.flairbnbapp.viewmodels.WishlistViewModel
import com.todkars.shimmer.ShimmerRecyclerView
import dagger.hilt.android.AndroidEntryPoint
import java.text.Format
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime

@AndroidEntryPoint
class WishlistsFragment : Fragment(), PlacesListInterface {
    val wishListViewModel by viewModels<WishlistViewModel>()
    val placesViewModel by viewModels<PlacesViewModel>()

    private var list = ArrayList<RoomModel>()
    lateinit var navController: NavController
    private var adapter = PlacesListAdapter(list, this)
    lateinit var rvWishList: RecyclerView
    lateinit var v: View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_wishlists, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        v = view
        PreferenceHelper.getSharedPreferences(view.context)

        navController = Navigation.findNavController(view)
        initViews(view)
        setRecyclerview(view)
        wishListViewModel.getMyWishListData(PreferenceHelper.readStringFromPreference(LocalKeys.KEY_USER_GOOGLE_ID))
            .observe(viewLifecycleOwner, {
                if (it != null) {
                    Log.d("TAG", "onViewCreated: $it")
                    list = it
                    adapter = PlacesListAdapter(list, this)
                    rvWishList.adapter = adapter
                    adapter.notifyDataSetChanged()
                }
            })

    }

    private fun setRecyclerview(view: View) {
        rvWishList.layoutManager = LinearLayoutManager(view.context)
        rvWishList.adapter = adapter
    }

    private fun initViews(view: View) {

        rvWishList = view.findViewById(R.id.rvWishList)

    }

    override fun onHrItemClick(roomModel: RoomModel, pos: Int) {

    }

    override fun setMarkOnMap(roomModel: RoomModel, pos: Int) {
    }

    @ExperimentalTime
    override fun onItemClick(model: RoomModel, adapterPosition: Int) {
        placesViewModel.setTheSelectedRoomModel(model)
        getCurrentLocationOfUser(v)
        showDateTimeRangePicker()
    }


    @ExperimentalTime
    private fun showDateTimeRangePicker() {
        if (PlacesFragment.check_in != null && PlacesFragment.check_out != null) {
            val format: Format = SimpleDateFormat("yyyy MMM dd")

            Log.d("TAG", "showDateTimeRangePicker: ${setTheCheckinCheckoutTime("12")}")

            Log.d(
                "TAG",
                "showDateTimeRangePicker: ${format.format(PlacesFragment.check_in)} \n ${
                    format.format(
                        PlacesFragment.check_out
                    )
                }"
            )
            navController.navigate(R.id.action_wishlistsFragment_to_placeDetailsFragment)
            return;
        }
        val calender: Calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        calender.clear()

        val today = MaterialDatePicker.todayInUtcMilliseconds()
        calender.set(Calendar.MONTH, Calendar.JANUARY)

        calender.timeInMillis = today

        val calConstBuilder: CalendarConstraints.Builder = CalendarConstraints.Builder()
        calConstBuilder.setValidator(DateValidatorPointForward.now())

        val mBuilder: MaterialDatePicker.Builder<Pair<Long, Long>> =
            MaterialDatePicker.Builder.dateRangePicker()
        mBuilder.setTitleText("Check-in - Check-out")
        mBuilder.setCalendarConstraints(calConstBuilder.build())

        val materialDatePicker = mBuilder.build()
        materialDatePicker.show(activity?.supportFragmentManager!!, "DATE_PIKER")
        materialDatePicker.addOnPositiveButtonClickListener {
            PlacesFragment.check_in = Date(it.first)
            PlacesFragment.check_out = Date(it.second)
            val format: Format = SimpleDateFormat("yyyy MMM dd")

            PreferenceHelper.writeStringToPreference(
                LocalKeys.CHECK_IN_TIME,
                "${it.first} ${format.format(PlacesFragment.check_in)}"
            )
            PreferenceHelper.writeStringToPreference(
                LocalKeys.CHECK_OUT_TIME,
                "${it.second} ${format.format(PlacesFragment.check_out)}"
            )
            Log.d("TAG", "showDateTimeRangePicker: ${setTheCheckinCheckoutTime("12")}")

            Log.d(
                "TAG",
                "showDateTimeRangePicker: ${format.format(PlacesFragment.check_in)} \n ${
                    format.format(
                        PlacesFragment.check_out
                    )
                }"
            )
            navController.navigate(R.id.action_wishlistsFragment_to_placeDetailsFragment)

        }
    }

    @ExperimentalTime
    @SuppressLint("SetTextI18n")
    private fun setTheCheckinCheckoutTime(price: String): Int {
        val checkinTime = PreferenceHelper.readStringFromPreference(LocalKeys.CHECK_IN_TIME)
        val checkoutTime = PreferenceHelper.readStringFromPreference(LocalKeys.CHECK_OUT_TIME)
        val checkin = checkinTime.split(" ")
        val checkout = checkoutTime.split(" ")

        if (checkinTime != null && checkoutTime != null &&
            checkin.size == 4 && checkout.size == 4
        ) {


            try {
                val x = DurationUnit.DAYS.convert(
                    checkout[0].toLong() - checkin[0].toLong(),
                    DurationUnit.MILLISECONDS
                )
                PreferenceHelper.writeIntToPreference(LocalKeys.NUMBER_OF_DAYS, x.toInt());
                return (x * price.toInt()).toInt()

            } catch (e: Exception) {
                Log.d("TAG", "setTheCheckinCheckoutTime: ${e.printStackTrace()}")
            }
        }
        return price.toInt()

    }

    private fun getCurrentLocationOfUser(view: View) {
        val fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(activity?.applicationContext!!)
        if (ActivityCompat.checkSelfPermission(
                view.context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                view.context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationProviderClient.lastLocation.addOnSuccessListener {
                Log.d("TAG", "onViewCreated: ")

                if (it != null) {
                    PreferenceHelper.writeStringToPreference(
                        LocalKeys.KEY_USER_CITY_LAT,
                        it.latitude.toString()
                    )
                    PreferenceHelper.writeStringToPreference(
                        LocalKeys.KEY_USER_CITY_LONG,
                        it.longitude.toString()
                    )

                }
            }
        }
    }

    override fun getTotal(price: String): Int {
        return 0
    }

    companion object {
    }


}