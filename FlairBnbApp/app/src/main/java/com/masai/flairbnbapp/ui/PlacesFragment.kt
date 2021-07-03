package com.masai.flairbnbapp.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest
import com.masai.flairbnbapp.R
import com.masai.flairbnbapp.localdatabases.LocalKeys
import com.masai.flairbnbapp.localdatabases.PreferenceHelper
import com.masai.flairbnbapp.models.RoomModel
import com.masai.flairbnbapp.recyclerviews.PlacesListAdapter
import com.masai.flairbnbapp.recyclerviews.PlacesListInterface
import com.masai.flairbnbapp.viewmodels.PlacesViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

@AndroidEntryPoint
class PlacesFragment : Fragment(), OnMapReadyCallback, PlacesListInterface {

    val placesViewModel by viewModels<PlacesViewModel>()

    private var map: GoogleMap? = null
    private var cameraPosition: CameraPosition? = null
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private val defaultLocation = LatLng(-33.8523341, 151.2106085)
    private var locationPermissionGranted = false
    private var list = ArrayList<RoomModel>()
    lateinit var rvPlacesFragment: RecyclerView
    private var adapter = PlacesListAdapter(list, this)


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_places, container, false)
    }

    companion object {
        private const val KEY_CAMERA_POSITION = "camera_position"
        private const val DEFAULT_ZOOM = 15
        private const val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1
    }

    val hashCriteria = HashMap<String, String>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        PreferenceHelper.getSharedPreferences(view.context)
        val navController = Navigation.findNavController(view)
        getLocationPermission()
        initViews(view)


        if (savedInstanceState != null) {
            cameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION)
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
                    placesViewModel.getListOfPlaces(hashCriteria).observe(viewLifecycleOwner, {
                        if (it != null) {
                            list = it
                            adapter = PlacesListAdapter(list, this)
                            Log.d("TAG", "onViewCreatedasd: " + list.toString())
                            rvPlacesFragment.adapter = adapter
                            adapter.notifyDataSetChanged()
                        }
                    })
                }
            }
        }
    }

    private fun setRecyclerview(view: View) {
        rvPlacesFragment.layoutManager = LinearLayoutManager(view.context)
        rvPlacesFragment.adapter = adapter
        adapter.notifyDataSetChanged()
    }

    private fun initViews(view: View) {
        rvPlacesFragment = view.findViewById(R.id.rvPlacesFragment)

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
        updateLocationUI()
        // Get the current location of the device and set the position of the map.
        getDeviceLocation()
    }

    fun moveCamera() {

        val ll = ""

        map?.moveCamera(
            CameraUpdateFactory
                .newLatLngZoom(LatLng(ll[0].toDouble(), ll[1].toDouble()), DEFAULT_ZOOM.toFloat())
        )
    }

    override fun onItemClick(roomModel: RoomModel, pos: Int) {

    }

    override fun setMarkOnMap(roomModel: RoomModel, pos: Int) {

    }

}