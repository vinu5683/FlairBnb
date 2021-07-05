package com.masai.flairbnbapp.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.cardview.widget.CardView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.libraries.places.api.Places
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.maps.android.ui.IconGenerator
import com.masai.flairbnbapp.R
import com.masai.flairbnbapp.localdatabases.LocalKeys
import com.masai.flairbnbapp.localdatabases.PreferenceHelper
import com.masai.flairbnbapp.models.RoomModel
import com.masai.flairbnbapp.recyclerviews.*
import com.masai.flairbnbapp.viewmodels.PlacesViewModel
import com.todkars.shimmer.ShimmerRecyclerView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_places.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


@AndroidEntryPoint
class PlacesFragment : Fragment(), OnMapReadyCallback, PlacesListInterface,
    GoogleMap.OnMarkerClickListener, HorizontalPlaceListListenerInterface, SearchListInterface {

    val placesViewModel by viewModels<PlacesViewModel>()
    lateinit var sheetBehavior: BottomSheetBehavior<View>
    lateinit var searchListView: ListView

    private var map: GoogleMap? = null
    private var cameraPosition: CameraPosition? = null
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private val defaultLocation = LatLng(-33.8523341, 151.2106085)
    private var locationPermissionGranted = false
    private var list = ArrayList<RoomModel>()
    private var searchList = ArrayList<RoomModel>()
    lateinit var rvPlacesFragment: ShimmerRecyclerView
    lateinit var rvPlaces_Hr: RecyclerView
    lateinit var rvSearch: RecyclerView
    private var adapter = PlacesListAdapter(list, this)
    private var adapter_hr = HorizontalPlaceListAdapter(list, this)
    private var search_list_adapter = SearchListAdapter(searchList, this)
    lateinit var v: View
    private val options = MarkerOptions()
    val hashCriteria = HashMap<String, String>()
    lateinit var etSearchPlaceFragment: EditText


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val coordinatorLayout =
            inflater
                .inflate(R.layout.fragment_places, container, false) as CoordinatorLayout
        val contentLayout: CardView = coordinatorLayout.findViewById(R.id.contentLayout)
        sheetBehavior = BottomSheetBehavior.from(contentLayout)
        sheetBehavior.isFitToContents = false
        sheetBehavior.isHideable = false

        sheetBehavior.setState(BottomSheetBehavior.STATE_HALF_EXPANDED) //initially state to fully expanded

        return coordinatorLayout
    }

    //todo: For Shimmer Effect
    fun setShimmerViewType() {
        rvPlacesFragment.setItemViewType { layoutManagerType, position ->
            when (layoutManagerType) {
                ShimmerRecyclerView.LAYOUT_GRID -> {
                    if (position % 2 == 0) {
                        return@setItemViewType R.layout.demo_shimmer_grid
                    } else
                        return@setItemViewType R.layout.demo_shimmer_grid
                }
                ShimmerRecyclerView.LAYOUT_LIST -> {
                    if (position == 0 && position % 2 == 0) {
                        return@setItemViewType R.layout.demo_shimmer_grid
                    } else
                        return@setItemViewType R.layout.demo_shimmer_grid
                }
                else -> return@setItemViewType R.layout.demo_shimmer_grid

            }
        }
        rvPlacesFragment.showShimmer()

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        PreferenceHelper.getSharedPreferences(view.context)
        val navController = Navigation.findNavController(view)
        v = view
        getLocationPermission()
        initViews(view)

        if (savedInstanceState != null) {
            cameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION)
        }

        if (!Places.isInitialized()) {
            Places.initialize(view.context, getString(R.string.google_api_key), Locale.US);
        }
        setRecyclerview(view)
        getTheCurrentLocation(view)
        val mapFragment: SupportMapFragment? = childFragmentManager
            .findFragmentById(R.id.placesMap) as SupportMapFragment?
        mapFragment?.getMapAsync(this)



        showCurrentPlace()
        placesViewModel.listOfPlaces.observe(viewLifecycleOwner, {
            if (it != null) {
                list = it
                Log.d("TAG", "onViewCreated: " + list.toString())
                adapter.notifyDataSetChanged()
            }
        })
    }

    private fun getTheCurrentLocation(view: View) {
        fusedLocationProviderClient =
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
                    val lat = PreferenceHelper.readStringFromPreference(LocalKeys.KEY_USER_CITY_LAT)
                    val long =
                        PreferenceHelper.readStringFromPreference(LocalKeys.KEY_USER_CITY_LONG)
                    Log.d("TAG", "onViewCreated: $lat $long")
                    moveCamera(lat.toDouble(), long.toDouble(), DEFAULT_ZOOM)
                    val geoCoder = Geocoder(view.context, Locale.getDefault())
                    val addresses: List<Address> =
                        geoCoder.getFromLocation(
                            lat.toDouble(),
                            long.toDouble(),
                            1
                        )
                    if (addresses.isNotEmpty()) {
                        hashCriteria["city"] = addresses[0].locality.toString() ?: ""
                        Log.d("TAG", "getTheCurrentLocation: " + hashCriteria["city"])
                        hashCriteria["state"] = addresses[0].adminArea.toString() ?: ""
                        hashCriteria["country"] = addresses[0].countryName.toString() ?: ""
                    }
                    placesViewModel.getListOfPlaces(hashCriteria)
                        .observe(viewLifecycleOwner, { itList ->
                            if (itList != null) {
                                list = itList
                                setFurtherStuff(list)
                            }
                        })
                }
            }
        }
    }

    private fun setFurtherStuff(list: java.util.ArrayList<RoomModel>) {
        adapter = PlacesListAdapter(list, this)
        adapter_hr = HorizontalPlaceListAdapter(list, this)
        Log.d("TAG", "onViewCreatedasd: " + list.toString())
        rvPlacesFragment.adapter = adapter
        adapter.notifyDataSetChanged()
        rvPlaces_Hr.adapter = adapter_hr
        adapter_hr.notifyDataSetChanged()
        setMarkersOnMap(list)
        rvPlacesFragment.hideShimmer()
    }

    private fun setMarkersOnMap(list: java.util.ArrayList<RoomModel>) {
        for (i in 0 until list.size) {
            setMarkOnMap(list[i], i)
        }
    }

    private fun setRecyclerview(view: View) {
        rvPlaces_Hr.layoutManager =
            LinearLayoutManager(view.context, RecyclerView.HORIZONTAL, false)
        rvPlaces_Hr.adapter = adapter_hr
        adapter_hr.notifyDataSetChanged()

        rvPlacesFragment.layoutManager = LinearLayoutManager(view.context)
        rvPlacesFragment.adapter = adapter
        adapter.notifyDataSetChanged()

        rvSearch.layoutManager = LinearLayoutManager(view.context)
        rvSearch.adapter = search_list_adapter
        search_list_adapter.notifyDataSetChanged()

        val snapHelper = LinearSnapHelper()
        snapHelper.attachToRecyclerView(rvPlaces_Hr)
        setShimmerViewType()
    }

    private fun initViews(view: View) {
        rvPlacesFragment = view.findViewById(R.id.rvPlacesFragment)
        rvPlaces_Hr = view.findViewById(R.id.rvPlaces_Hr)
        rvSearch = view.findViewById(R.id.rvSearch)

        etSearchPlaceFragment = view.findViewById(R.id.etSearchPlaceFragment)
        etSearchPlaceFragment.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                rvSearch.visibility = View.VISIBLE
            } else
                rvSearch.visibility = View.GONE
        }
        rvPlaces_Hr.setOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                rvSearch.visibility = View.GONE
            }
        })

        rvPlacesFragment.setOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                rvSearch.visibility = View.GONE
            }
        })

        val searchListInterface: SearchListInterface = this

        etSearchPlaceFragment.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                Log.d("TAG", "onTextChanged: b ")
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                Log.d("TAG", "onTextChanged: o")
                try {
                    rvSearch.visibility = View.VISIBLE
                    if (etSearchPlaceFragment.text.toString().trim().length >= 3) {
                        placesViewModel.search(etSearchPlaceFragment.text.toString(), hashCriteria)
                            .observe(viewLifecycleOwner, {
                                if (it != null) {
                                    searchList = it
                                    search_list_adapter = SearchListAdapter(
                                        searchList,
                                        searchListInterface
                                    )
                                    rvSearch.adapter = search_list_adapter
                                    Log.d("TAG", "onTextChanged: " + searchList.toString())
                                    search_list_adapter.notifyDataSetChanged()
                                }
                            })
                    }
                } catch (e: Exception) {
                }
            }

            override fun afterTextChanged(s: Editable?) {

            }

        })

        view.findViewById<ImageButton>(R.id.btnBackFromPlaceFragment).setOnClickListener {
            activity?.onBackPressed()
        }
        view.findViewById<ImageButton>(R.id.btnSettingPlaceFragment).setOnClickListener {
            //open new fragment
        }

    }

    private fun getLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                activity?.applicationContext!!,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            == PackageManager.PERMISSION_GRANTED
        ) {
            locationPermissionGranted = true
        } else {
            ActivityCompat.requestPermissions(
                activity?.parent!!, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.P)
    private fun getDeviceLocation() {
        try {
            if (locationPermissionGranted) {
                val locationResult = fusedLocationProviderClient.lastLocation
                locationResult.addOnCompleteListener(activity?.mainExecutor!!) { task ->
                    if (task.isSuccessful) {
                        // Set the map's camera position to the current location of the device.
                    } else {
                        Log.d("TAG", "Current location is null. Using defaults.")
                        Log.e("TAG", "Exception: %s", task.exception)
                        map?.moveCamera(
                            CameraUpdateFactory
                                .newLatLngZoom(
                                    defaultLocation,
                                    DEFAULT_ZOOM.toFloat()
                                )
                        )
                    }
                }
            }
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message, e)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        locationPermissionGranted = false
        when (requestCode) {
            PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION -> {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED
                ) {
                    locationPermissionGranted = true
                }
            }
        }
        updateLocationUI()
    }

    @SuppressLint("MissingPermission")
    private fun showCurrentPlace() {
        if (map == null) {
            return
        }
        if (locationPermissionGranted) {
        } else {
            // The user has not granted permission.
            Log.i("TAG", "The user did not grant location permission.")
            // Prompt the user for permission.
            getLocationPermission()
        }
    }

    private fun updateLocationUI() {
        if (map == null) {
            return
        }
        try {
            if (locationPermissionGranted) {
                map?.isMyLocationEnabled = true
                map?.uiSettings?.isMyLocationButtonEnabled = true
            } else {
                map?.isMyLocationEnabled = false
                map?.uiSettings?.isMyLocationButtonEnabled = false
                getLocationPermission()
            }
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message, e)
        }
    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onMapReady(map: GoogleMap) {
        getLocationPermission()
        this.map = map
        map.setMapStyle(MapStyleOptions.loadRawResourceStyle(v.context, R.raw.map_styles));
        map.setOnCameraMoveListener {
            rvSearch.visibility = View.GONE
        }
        updateLocationUI()
        // Get the current location of the device and set the position of the map.
        getDeviceLocation()
    }

    fun moveCamera(lat: Double, long: Double, zoom: Int) {
        val location = CameraUpdateFactory.newLatLngZoom(
            LatLng(lat, long), zoom.toFloat()
        )
        map?.animateCamera(location)

    }

    override fun onHrItemClick(roomModel: RoomModel, pos: Int) {
        val lat = roomModel.location_lat.toString().toDouble()
        val long = roomModel.location_long.toString().toDouble()
        moveCamera(lat, long, 15)
    }

    override fun setMarkOnMap(roomModel: RoomModel, pos: Int) {
        val title = roomModel.title
        val address = roomModel.city + ", " + roomModel.state

        //generate the icon using the view
        val iconGenerator = IconGenerator(v.context)
        iconGenerator.setBackground(v.context.getDrawable(R.drawable.bg_custom_marker))
        val inflatedView = View.inflate(context, R.layout.marker_custom, null)
        val textView = inflatedView.findViewById<TextView>(R.id.roomPrice)

        val price: String = if (roomModel.price!! > 999) {
            (roomModel.price!! / 1000).toInt().toString() + "," + (roomModel.price!! % 1000)
        } else
            roomModel.price.toString()
        textView.text = "₹$price"
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
    }

    override fun onMarkerClick(marker: Marker): Boolean {

        return true
    }


    companion object {
        private const val KEY_CAMERA_POSITION = "camera_position"
        private const val DEFAULT_ZOOM = 13
        private const val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1
    }

    override fun onSearchItemClick(roomModel: RoomModel, pos: Int) {

    }

    override fun getLagLang(): LatLng {
        return LatLng(
            PreferenceHelper.readStringFromPreference(LocalKeys.KEY_USER_CITY_LAT).toDouble(),
            PreferenceHelper.readStringFromPreference(LocalKeys.KEY_USER_CITY_LONG).toDouble()
        )
    }


}

