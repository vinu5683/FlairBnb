package com.masai.flairbnbapp.ui

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.IntentSender.SendIntentException
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.PendingResult
import com.google.android.gms.common.api.ResultCallback
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import com.masai.flairbnbapp.R
import com.masai.flairbnbapp.localdatabases.LocalKeys
import com.masai.flairbnbapp.models.CityModel
import com.masai.flairbnbapp.models.LiveAnywhereModel
import com.masai.flairbnbapp.recyclerviews.CityItemInterface
import com.masai.flairbnbapp.recyclerviews.ExploreNearByAdapter
import com.masai.flairbnbapp.recyclerviews.LiveAnywhereAdapter
import com.masai.flairbnbapp.recyclerviews.LiveAnywhereInterface
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ExploreFragment : Fragment(), CityItemInterface, LiveAnywhereInterface {


    lateinit var cardViewImFlexible: CardView
    lateinit var getInspiredCardView: CardView
    lateinit var tryHostingCardView: CardView
    lateinit var search_bar_home: LinearLayout
    lateinit var exploreFragmentContainer: ConstraintLayout
    lateinit var rvExploreNearByHome: RecyclerView
    lateinit var rvLiveAnywhere: RecyclerView

    lateinit var navController: NavController

    private fun initViews(view: View) {
        view.apply {
            cardViewImFlexible = findViewById(R.id.cardViewImFlexible)
            getInspiredCardView = findViewById(R.id.getInspiredCardView)
            tryHostingCardView = findViewById(R.id.tryHostingCardView)
            exploreFragmentContainer = findViewById(R.id.exploreFragmentContainer)
            search_bar_home = findViewById(R.id.search_bar_home)
            rvExploreNearByHome = findViewById(R.id.rvExploreNearByHome)
            rvLiveAnywhere = findViewById(R.id.rvLiveAnywhere)
        }
    }

    private fun initRecyclerViews(view: View) {
        //for city's
        rvExploreNearByHome.layoutManager =
            GridLayoutManager(view.context, 2, GridLayoutManager.HORIZONTAL, false);
        val adapter = ExploreNearByAdapter(LocalKeys.getAllCityModels(), this)
        rvExploreNearByHome.adapter = adapter
        adapter.notifyDataSetChanged()
        val snapHelper = LinearSnapHelper()
        snapHelper.attachToRecyclerView(rvExploreNearByHome)


        rvLiveAnywhere.layoutManager =
            LinearLayoutManager(view.context, LinearLayoutManager.HORIZONTAL, false)
        val liveAnywhereAdapter = LiveAnywhereAdapter(LocalKeys.getAllLiveAnywhereModels(), this)
        rvLiveAnywhere.adapter = liveAnywhereAdapter
        val snapHelperLiveAnywhere = LinearSnapHelper()
        snapHelperLiveAnywhere.attachToRecyclerView(rvLiveAnywhere)
    }


    lateinit var v: View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        LocalKeys.my_location = LatLng(12.962931, 77.576304)
        return inflater.inflate(R.layout.fragment_explore, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        v = view
        MainActivity.navToggleInterface.setNavigation(true)
        navController = Navigation.findNavController(view)
        initViews(view)
        initRecyclerViews(view)
        fusedLocationProvider = LocationServices.getFusedLocationProviderClient(v.context)
        val tvImFlexible: TextView = view.findViewById(R.id.tv_im_flexible)
        setGradientColor(tvImFlexible)
        tryHostingCardView.setOnClickListener {
            navController.navigate(R.id.action_exploreFragment_to_hostPart1Fragment)
        }
        view.findViewById<Button>(R.id.btnSearch).setOnClickListener {
            checkLocationPermission()
            navController.navigate(R.id.action_exploreFragment_to_placesFragment)
        }
    }


    private fun setGradientColor(tvImFlexible: TextView) {
        val shader = LinearGradient(
            0f,
            0f,
            0f,
            tvImFlexible.textSize,
            Color.RED,
            Color.BLUE,
            Shader.TileMode.CLAMP
        )
        tvImFlexible.paint.shader = shader
    }

    private var fusedLocationProvider: FusedLocationProviderClient? = null
    private val locationRequest: LocationRequest = LocationRequest.create().apply {
        interval = 30
        fastestInterval = 10
        priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
        maxWaitTime = 60
    }
    var location: Location? = null

    private var locationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val locationList = locationResult.locations
            if (locationList.isNotEmpty()) {
                //The last location in the list is the newest
                location = locationList.last()
                try {
                    LocalKeys.my_location = LatLng(location!!.latitude, location!!.longitude)
                } catch (e: Exception) {
                    //default set to Bengaluru
                    LocalKeys.my_location = LatLng(12.962931, 77.576304)
                }
            }
        }
    }


    override fun onResume() {
        super.onResume()
        if (ContextCompat.checkSelfPermission(v.context, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED
        ) {

            fusedLocationProvider?.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
            displayLocationSettingsRequest(requireActivity());

        }
    }

    override fun onPause() {
        super.onPause()
        if (ContextCompat.checkSelfPermission(
                v.context,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationProvider?.removeLocationUpdates(locationCallback)
        }
    }

    private fun checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    requireActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                AlertDialog.Builder(activity)
                    .setTitle("Location Permission Needed")
                    .setMessage("This app needs the Location permission, please accept to use location functionality")
                    .setPositiveButton(
                        "OK"
                    ) { _, _ ->
                        //Prompt the user once explanation has been shown
                        requestLocationPermission()
                    }
                    .create()
                    .show()
            } else {
                // No explanation needed, we can request the permission.
                requestLocationPermission()
            }
        }
    }

    private fun requestLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
                ),
                MY_PERMISSIONS_REQUEST_LOCATION
            )
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                MY_PERMISSIONS_REQUEST_LOCATION
            )
        }
    }


    private fun displayLocationSettingsRequest(context: Context) {
        val googleApiClient = GoogleApiClient.Builder(context)
            .addApi(LocationServices.API).build()
        googleApiClient.connect()
        val locationRequest = LocationRequest.create()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 10000
        locationRequest.fastestInterval = (10000 / 2).toLong()
        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        builder.setAlwaysShow(true)
        val result: PendingResult<LocationSettingsResult> =
            LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build())
        result.setResultCallback {
            val status: Status = it.status
            when (status.statusCode) {
                LocationSettingsStatusCodes.SUCCESS -> Log.i(
                    "TAG",
                    "All location settings are satisfied."
                )
                LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> {
                    Log.i(
                        "TAG",
                        "Location settings are not satisfied. Show the user a dialog to upgrade location settings "
                    )
                    try {
                        // Show the dialog by calling startResolutionForResult(), and check the result
                        // in onActivityResult().
                        status.startResolutionForResult(
                            requireActivity(),
                            MY_PERMISSIONS_REQUEST_LOCATION
                        )
                    } catch (e: SendIntentException) {
                        Log.i("TAG", "PendingIntent unable to execute request.")
                    }
                }
                LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> Log.i(
                    "TAG",
                    "Location settings are inadequate, and cannot be fixed here. Dialog not created."
                )
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_LOCATION -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(
                            requireActivity(),
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        fusedLocationProvider?.requestLocationUpdates(
                            locationRequest,
                            locationCallback,
                            Looper.getMainLooper()
                        )
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(v.context, "permission denied", Toast.LENGTH_LONG)
                        .show()
                }
                return
            }
        }
    }

    companion object {
        private const val MY_PERMISSIONS_REQUEST_LOCATION = 99
        private val kochi: LatLng = LatLng(9.925020, 76.271220)
        val bengaluru: LatLng = LatLng(12.962931, 77.576304)
        private val benaulim: LatLng = LatLng(15.250590, 73.939477)
        private val majorda: LatLng = LatLng(15.315933, 73.926759)
        private val canacona: LatLng = LatLng(15.007156, 74.042584)
        private val puducherry: LatLng = LatLng(11.945359, 79.821427)

        val placesCriteria: HashMap<String, String>? = HashMap();
    }

    override fun onCityItemClick(cityModel: CityModel) {
        placesCriteria?.clear()
        placesCriteria?.put("city", cityModel.name!!)
        checkLocationPermission()
        navController.navigate(R.id.action_exploreFragment_to_placesFragment)
    }

    override fun getTheDriveTime(cityModel: CityModel): String {
        val results = FloatArray(1)
        var latLng: LatLng = LatLng(1.1, 1.1)

        when (cityModel.name) {
            "Bengaluru" -> latLng = bengaluru
            "Puducherry" -> latLng = puducherry
            "Canacona" -> latLng = canacona
            "Kochi" -> latLng = kochi
            "Benaulim" -> latLng = benaulim
            "Majorda" -> latLng = majorda
        }

        try {
            Location.distanceBetween(
                location?.latitude!!,
                location?.longitude!!,
                latLng.latitude,
                latLng.longitude,
                results
            )
        } catch (e: Exception) {
            Location.distanceBetween(
                bengaluru.latitude,
                bengaluru.longitude,
                latLng.latitude,
                latLng.longitude,
                results
            )
        }

        results[0] = results[0] / 100
        // 6min = 1km

        val x = results[0] / 6;
        return if (x / 60 > 0) {
            (x / 60).toInt().toString() + "." + (x % 60).toInt().toString() + " hour drive"
        } else
            "$x minute drive"

        return "turn on location"
    }

    override fun onItemClick(liveAnywhereModel: LiveAnywhereModel) {

    }
}
