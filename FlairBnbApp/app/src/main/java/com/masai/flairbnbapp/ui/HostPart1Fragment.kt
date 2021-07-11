package com.masai.flairbnbapp.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest
import com.masai.flairbnbapp.R
import com.masai.flairbnbapp.models.RoomModel
import com.masai.flairbnbapp.viewmodels.HostPlaceViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_host_part1.*
import java.util.*


@AndroidEntryPoint
class HostPart1Fragment : Fragment(), OnMapReadyCallback {


    private var location: String = "123"

    private val placeViewModel by viewModels<HostPlaceViewModel>()

    private var map: GoogleMap? = null
    private var cameraPosition: CameraPosition? = null
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private val defaultLocation = LatLng(-33.8523341, 151.2106085)
    private var locationPermissionGranted = false


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_host_part1, container, false)
    }

    lateinit var v: View
    val roomModel = initModel()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        v = view
        val navController = Navigation.findNavController(view)
        getLocationPermission()
        if (savedInstanceState != null) {
            cameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION)
        }
        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(activity?.applicationContext!!)
        val mapFragment: SupportMapFragment? = childFragmentManager
            .findFragmentById(R.id.pickLocationMap) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
        showCurrentPlace()

        view.findViewById<Button>(R.id.btnNext2).setOnClickListener {
            if (location != "123" && location != "") {
                placeViewModel.setRoomObject(roomModel)
                navController.navigate(R.id.action_hostPart1Fragment_to_hostPart2Fragment)
                Log.d("TAG", "SetRoomObject Done" + roomModel.toString())
            } else {
                Toast.makeText(view.context, "Please Select the Location", Toast.LENGTH_SHORT)
                    .show()
            }
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

    private fun initModel(): RoomModel {
        return RoomModel(
            id = System.currentTimeMillis().toString() + (Math.random() * 2231).toInt().toString(),
            title = "",
            description = "",
            category = "",
            subCategory = "",
            roomSpaceType = "",
            price = 0,
            priceForWhat = "",
            location_lat = "",
            location_long = "",
            host_id = "",
            services = null,
            rating = 0,
            rooms = 0,
            total_capacity = 0,
            total_bathrooms = 0,
            beds = 0,
            maxMembers = 0,
            contactNo = "",
            city = "",
            state = "",
            country = "",
            images = null,
        )
    }

    companion object {
        private const val KEY_CAMERA_POSITION = "camera_position"
        private const val DEFAULT_ZOOM = 15

        private const val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1
    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onMapReady(map: GoogleMap) {
        getLocationPermission()
        this.map = map
        map.setMapStyle(MapStyleOptions.loadRawResourceStyle(v.context, R.raw.map_styles));

        map.setOnMapClickListener(GoogleMap.OnMapClickListener { latLng -> // Creating a marker
            val markerOptions = MarkerOptions()

            // Setting the position for the marker
            markerOptions.position(latLng)
            location = latLng.latitude.toString() + ":" + latLng.longitude.toString()
            roomModel.location_lat = latLng.latitude.toString()
            roomModel.location_long = latLng.longitude.toString()
            // Setting the title for the marker.
            // This will be displayed on taping the marker
            markerOptions.title(latLng.latitude.toString() + " : " + latLng.longitude)

            // Clears the previously touched position
            map.clear()

            // Animating to the touched position
            map.animateCamera(CameraUpdateFactory.newLatLng(latLng))

            // Placing a marker on the touched position
            map.addMarker(markerOptions)

        })

        updateLocationUI()
        // Get the current location of the device and set the position of the map.
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
            val placeFields = listOf(Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG)

            val request = FindCurrentPlaceRequest.newInstance(placeFields)

        } else {
            Log.i("TAG", "The user did not grant location permission.")

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
}