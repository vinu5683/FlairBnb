package com.masai.flairbnbapp.ui

import android.annotation.SuppressLint
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.libraries.places.api.Places
import com.google.maps.android.ui.IconGenerator
import com.masai.flairbnbapp.R
import com.masai.flairbnbapp.interfaces.OnPaymentInterface
import com.masai.flairbnbapp.localdatabases.LocalKeys
import com.masai.flairbnbapp.localdatabases.PreferenceHelper
import com.masai.flairbnbapp.models.BookPlaceModel
import com.masai.flairbnbapp.models.RoomModel
import com.masai.flairbnbapp.models.UserModel
import com.masai.flairbnbapp.recyclerviews.ImageLoopAdapter
import com.masai.flairbnbapp.recyclerviews.PlaceDetailsServiceListAdapter
import com.masai.flairbnbapp.viewmodels.PlacesViewModel
import com.masai.flairbnbapp.viewmodels.UsersViewModel
import com.razorpay.Checkout
import com.smarteist.autoimageslider.SliderView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_place_details.*
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.*
import kotlin.collections.ArrayList


@AndroidEntryPoint
class PlaceDetailsFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener,
    OnPaymentInterface {
    val placesViewModel by viewModels<PlacesViewModel>()
    val userViewModel by viewModels<UsersViewModel>()

    lateinit var v: View
    lateinit var selectedRoom: RoomModel
    private var map: GoogleMap? = null
    private var cameraPosition: CameraPosition? = null
    private val options = MarkerOptions()
    private lateinit var navController: NavController
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_place_details, container, false)
    }


    lateinit var carouselViewItem: SliderView
    lateinit var rv_serviceList_place_details: RecyclerView
    lateinit var placeHost: UserModel

    var placeDetailsServiceAdapter: PlaceDetailsServiceListAdapter = PlaceDetailsServiceListAdapter(
        ArrayList()
    )


    //view variables
    lateinit var backPlaceDetails: CardView
    lateinit var sharePlaceDetails: CardView
    lateinit var likePlaceDetails: CardView

    lateinit var title_place_details: TextView
    lateinit var reviewsAddress_places_Details: TextView
    lateinit var roomSpaceType_place_details: TextView
    lateinit var typeShortDec_places_Details: TextView
    lateinit var guest_bbb_places_Details: TextView
    lateinit var tvAllAboutSomeOnePlace: TextView
    lateinit var tvAllAboutSomeOnePlaceDesc: TextView
    lateinit var tvAvailability: TextView
    lateinit var tvCacelationPolicy: TextView
    lateinit var tvCostAndForWhat: TextView
    lateinit var tvFromToDate: TextView
    lateinit var approxDistance: TextView
    lateinit var tvFreeCancellationDate: TextView

    lateinit var showAllAmenities: TextView
    lateinit var btnAllAboutDescShowMore: Button
    lateinit var btnReserve: Button
    lateinit var backBtnPlaceDetails: ImageButton

    @SuppressLint("SetTextI18n")
    private fun setDetails() {
        try {
            title_place_details.text = selectedRoom.title + " at " + selectedRoom.city + "."
            reviewsAddress_places_Details.text =
                selectedRoom.rating.toString() + "(14 reviews) · ${selectedRoom.city}, ${selectedRoom.state}, ${selectedRoom.country}"
            placesViewModel.currentPlaceUser.observe(viewLifecycleOwner, {
                if (it != null) {
                    placeHost = it
                    roomSpaceType_place_details.text =
                        "${selectedRoom.roomSpaceType} of ${selectedRoom.category} is hosted by " +
                                "${it.firstName}."
                    tvAllAboutSomeOnePlace.text = "All about ${it.firstName}'s place"
                }
            })
            showAllAmenities.text = "Show all ${selectedRoom.services?.size} amenities"
            typeShortDec_places_Details.text =
                "${selectedRoom.roomSpaceType} · ${selectedRoom.category} · ${selectedRoom.subCategory}."
            guest_bbb_places_Details.text =
                "${selectedRoom.total_capacity} guests · ${selectedRoom.rooms} bedroom · " +
                        "${selectedRoom.beds} beds · ${selectedRoom.total_bathrooms} bathroom"
            tvAllAboutSomeOnePlaceDesc.text = selectedRoom.description
            //recycler view
            rv_serviceList_place_details.layoutManager = LinearLayoutManager(v.context)
            placeDetailsServiceAdapter = PlaceDetailsServiceListAdapter(selectedRoom.services!!)
            rv_serviceList_place_details.adapter = placeDetailsServiceAdapter
            placeDetailsServiceAdapter.notifyDataSetChanged()
            val price: String = if (selectedRoom.price!! > 999) {
                val x =
                    (selectedRoom.price!!.toString().reversed().substring(0, 3) + ",").reversed()
                val y = selectedRoom.price!!.toString().reversed().substring(3).reversed()
                y + x
            } else
                selectedRoom.price.toString()
            tvCostAndForWhat.text = "₹$price / night"

            setTheCheckinCheckoutTime()


            //

        } catch (e: Exception) {
            Log.d("TAG", "setDetails: " + e.printStackTrace())
        }

    }

    @SuppressLint("SetTextI18n")
    private fun setTheCheckinCheckoutTime() {
        val checkinTime = PreferenceHelper.readStringFromPreference(LocalKeys.CHECK_IN_TIME)
        val checkoutTime = PreferenceHelper.readStringFromPreference(LocalKeys.CHECK_OUT_TIME)
        Log.d("TAG", "setDetails: $checkinTime")
        Log.d("TAG", "setDetails: $checkoutTime")
        val checkin = checkinTime.split(" ")
        val checkout = checkoutTime.split(" ")

        if (checkinTime != null && checkoutTime != null &&
            checkin.size == 4 && checkout.size == 4
        ) {
            tvFreeCancellationDate.text =
                "Free cancellation before ${checkin[3] + " " + checkin[2]} 12AM"
            tvFromToDate.text =
                "${checkin[3] + " " + checkin[2] + " - " + checkout[3] + " " + checkout[2]} (${
                    PreferenceHelper.readIntFromPreference(
                        LocalKeys.NUMBER_OF_DAYS
                    )
                } days)"
            tvAvailability.text = tvFromToDate.text
        }
    }


    //view initialization
    private fun initViews(view: View) {

        view.apply {

            carouselViewItem = findViewById(R.id.carouselImagesPlaceDetails)
            rv_serviceList_place_details = findViewById(R.id.rv_serviceList_place_details)

            //top bar
            backPlaceDetails = findViewById(R.id.backPlaceDetails)
            sharePlaceDetails = findViewById(R.id.sharePlaceDetails)
            likePlaceDetails = findViewById(R.id.likePlaceDetails)

            //main room content
            title_place_details = findViewById(R.id.title_place_details)
            reviewsAddress_places_Details = findViewById(R.id.reviewsAddress_places_Details)
            roomSpaceType_place_details = findViewById(R.id.roomSpaceType_place_details)
            typeShortDec_places_Details = findViewById(R.id.typeShortDec_places_Details)
            guest_bbb_places_Details = findViewById(R.id.guest_bbb_places_Details)
            tvAllAboutSomeOnePlace = findViewById(R.id.tvAllAboutSomeOnePlace)
            tvAllAboutSomeOnePlaceDesc = findViewById(R.id.tvAllAboutSomeOnePlaceDesc)
            tvAvailability = findViewById(R.id.tvAvailability)
            tvCacelationPolicy = findViewById(R.id.tvCacelationPolicy)
            tvCostAndForWhat = findViewById(R.id.tvCostAndForWhat)
            tvFromToDate = findViewById(R.id.tvFromToDate)
            approxDistance = findViewById(R.id.approxDistance)
            tvFreeCancellationDate = findViewById(R.id.tvFreeCancellationDate)

            //buttons
            showAllAmenities = findViewById(R.id.showAllAmenities)
            btnAllAboutDescShowMore = findViewById(R.id.btnAllAboutDescShowMore)
            btnReserve = findViewById(R.id.btnReserve)
            backBtnPlaceDetails = findViewById(R.id.backBtnPlaceDetails)

        }
    }

    //view listeners
    private fun setTheListeners(view: View) {
        carouselViewItem.setOnClickListener {
            //open fragment and show list of images
        }

        backBtnPlaceDetails.setOnClickListener {
            activity?.onBackPressed()
        }

        sharePlaceDetails.setOnClickListener {

        }

        likePlaceDetails.setOnClickListener {
            //add to wishlist
        }

        btnAllAboutDescShowMore.setOnClickListener {
            tvAllAboutSomeOnePlaceDesc.maxLines = 1000
            btnAllAboutDescShowMore.text = "Have a good day"
        }

        showAllAmenities.setOnClickListener {

        }

        btnReserve.setOnClickListener {
            val checkout = Checkout()
            checkout.setKeyID("rzp_test_WX3sGwJbhsIfnd");
            checkout.setImage(R.drawable.ic_noun_airbnb_colored)

            val jsonObject = JSONObject()

            try {
                jsonObject.put("name", "Flairbnb")
                jsonObject.put(
                    "description",
                    "Booking room place for ${PreferenceHelper.readIntFromPreference(LocalKeys.NUMBER_OF_DAYS)} days"
                )
                jsonObject.put("theme.color", "#FD365B")
                jsonObject.put("currency", "INR")
                jsonObject.put(
                    "amount",
                    selectedRoom.price!! * PreferenceHelper.readIntFromPreference(LocalKeys.NUMBER_OF_DAYS)
                )
                jsonObject.put("prefill.contact", "9738582200")
                jsonObject.put("prefill.email", "vinod568312@gmail.com")

                checkout.open(this.activity, jsonObject)
            } catch (e: Exception) {

            }

        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        v = view
        onPaymentDoneInterface = this
        PreferenceHelper.getSharedPreferences(view.context)
        navController = Navigation.findNavController(view)
        selectedRoom = placesViewModel.getTheSelectedRoom()
        placesViewModel.getUserNameById(selectedRoom.host_id)

        initViews(view)

        if (savedInstanceState != null) {
            cameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION)
        }

        if (!Places.isInitialized()) {
            Places.initialize(view.context, getString(R.string.google_api_key), Locale.US);
        }

        val mapFragment: SupportMapFragment? = childFragmentManager
            .findFragmentById(R.id.map_where_you_will_be) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
        setDetails()

        setImages(selectedRoom)

        setTheListeners(view)
    }


    private fun setImages(model: RoomModel) {
        if (model.images == null) {
            Log.d("TAG", "setData: No Images for model \n" + model.toString())
            return
        }

        val myAdapter = ImageLoopAdapter(model.images)
        carouselViewItem.setSliderAdapter(myAdapter)
    }


    private fun moveCamera(lat: Double, long: Double, zoom: Int) {
        val location = CameraUpdateFactory.newLatLngZoom(
            LatLng(lat, long), zoom.toFloat()
        )
        map?.animateCamera(location)
    }

    override fun onMapReady(p0: GoogleMap) {
        this.map = p0
        val roomModel = selectedRoom
        p0.setMapStyle(MapStyleOptions.loadRawResourceStyle(v.context, R.raw.map_styles));
        val title = roomModel.title
        val address = roomModel.city + ", " + roomModel.state

        //generate the icon using the view
        val iconGenerator = IconGenerator(v.context)
        iconGenerator.setBackground(v.context.getDrawable(R.drawable.bg_custom_marker))
        val inflatedView = View.inflate(context, R.layout.marker_custom_home, null)
        iconGenerator.setContentView(inflatedView)

        options.position(
            LatLng(
                roomModel.location_lat?.toDouble()!!,
                roomModel.location_long?.toDouble()!!
            )
        )
        options.title(title)
        options.snippet(address)
        options.icon(BitmapDescriptorFactory.fromBitmap(iconGenerator.makeIcon()))
        map?.addMarker(options)
        moveCamera(
            selectedRoom.location_lat.toString().toDouble(),
            selectedRoom.location_long.toString().toDouble(),
            DEFAULT_ZOOM
        )

        val results = FloatArray(1)
        Location.distanceBetween(
            PreferenceHelper.readStringFromPreference(LocalKeys.KEY_USER_CITY_LAT).toDouble(),
            PreferenceHelper.readStringFromPreference(LocalKeys.KEY_USER_CITY_LONG).toDouble(),
            roomModel.location_lat.toString().toDouble(),
            roomModel.location_long.toString().toDouble(),
            results
        )
        results[0] = results[0] / 10
        val res = (results[0].toInt() / 100).toString() + "." + results[0].toInt() % 100
        approxDistance.text = "Approx Distance: ${res}Km"
    }

    override fun onMarkerClick(p0: Marker): Boolean {
        return true
    }

    companion object {
        private const val KEY_CAMERA_POSITION = "camera_position"
        private const val DEFAULT_ZOOM = 10
        private const val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1
        lateinit var onPaymentDoneInterface: OnPaymentInterface
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onPaymentDone(transactionId: String) {
        try {
            val sdf = SimpleDateFormat("dd-M-yyyy hh:mm:ss")

            val bookPlaceModel =
                BookPlaceModel(
                    System.currentTimeMillis().toString(),
                    selectedRoom.id,
                    PreferenceHelper.readStringFromPreference(LocalKeys.KEY_USER_GOOGLE_ID),
                    PreferenceHelper.readStringFromPreference(LocalKeys.CHECK_IN_TIME)
                        .split(" ")[0],
                    PreferenceHelper.readStringFromPreference(LocalKeys.CHECK_OUT_TIME)
                        .split(" ")[0],
                    PreferenceHelper.readIntFromPreference(LocalKeys.NUMBER_OF_DAYS).toString(),
                    "Payment Done",
                    selectedRoom.price!! * PreferenceHelper.readIntFromPreference(LocalKeys.NUMBER_OF_DAYS),
                    transactionId,
                    System.currentTimeMillis().toString()
                )
            userViewModel.setMyPresentOrder(bookPlaceModel)
            InvoiceFragment.myPresentId = bookPlaceModel.id
            placesViewModel.bookroom(bookPlaceModel)
            Handler().postDelayed({
                userViewModel.setMyPresentOrder(bookPlaceModel)
                InvoiceFragment.myPresentId = bookPlaceModel.id
                navController.navigate(R.id.action_placeDetailsFragment_to_invoiceFragment)
            }, 1000)
        } catch (e: Exception) {
            Log.d("TAG", "onPaymentDone: ${e}")
        }
    }
}