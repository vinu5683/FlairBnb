package com.masai.flairbnbapp.ui

import android.graphics.drawable.AnimationDrawable
import android.location.Address
import android.location.Geocoder
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import android.widget.Button
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.FirebaseApp
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.masai.flairbnbapp.R
import com.masai.flairbnbapp.localdatabases.LocalKeys
import com.masai.flairbnbapp.localdatabases.PreferenceHelper
import com.masai.flairbnbapp.models.RoomModel
import com.masai.flairbnbapp.viewmodels.HostPlaceViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_host_part1.*
import kotlinx.android.synthetic.main.fragment_host_part7.*
import java.util.*
import kotlin.collections.ArrayList


@AndroidEntryPoint
class HostPart7Fragment : Fragment() {

    private val pvm by viewModels<HostPlaceViewModel>()
    var anim: AnimationDrawable? = null
    lateinit var roomModel: RoomModel

    private val downloadImageList = ArrayList<Uri>()
    var unique = System.currentTimeMillis().toString()
    private var app: FirebaseApp? = null
    private var storage: FirebaseStorage? = null
    private var imageList = ArrayList<Uri>()

    lateinit var navController: NavController

    //views
    lateinit var etPlaceName: TextInputLayout
    lateinit var etPlaceDescription: TextInputLayout
    lateinit var etPlacePrice: TextInputLayout
    lateinit var etPlaceContact: TextInputLayout
    lateinit var etPlaceCity: TextInputLayout
    lateinit var etPlaceState: TextInputLayout
    lateinit var etPlaceCountry: TextInputLayout
    lateinit var part6Card: CardView
    lateinit var rtyhgt: TextView
    lateinit var progressbar: CircularProgressIndicator

    private fun initViews(view: View) {
        view.apply {
            etPlaceName = findViewById(R.id.etPlaceName)
            etPlaceDescription = findViewById(R.id.etPlaceDescription)
            etPlacePrice = findViewById(R.id.etPlacePrice)
            etPlaceContact = findViewById(R.id.etPlaceContact)
            etPlaceCity = findViewById(R.id.etPlaceCity)
            etPlaceState = findViewById(R.id.etPlaceState)
            etPlaceCountry = findViewById(R.id.etPlaceCountry)
            part6Card = findViewById(R.id.part6Card)
            rtyhgt = findViewById(R.id.rtyhgt)
            progressbar = findViewById(R.id.progress_circular_hosting)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_host_part7, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        PreferenceHelper.getSharedPreferences(view.context)

        navController = Navigation.findNavController(view)
        initViews(view)
        roomModel = pvm.roomModel!!

        setViews(view)

        val constraintLayout = view.findViewById<ConstraintLayout>(R.id.part7Container)
        anim = constraintLayout.background as AnimationDrawable
        anim?.setEnterFadeDuration(5000)
        anim?.setExitFadeDuration(2000)

        app = FirebaseApp.getInstance();
        storage = FirebaseStorage.getInstance(app!!);
        imageList = pvm.imageList

        view.findViewById<Button>(R.id.btnDone).setOnClickListener {

            roomModel.city = etPlaceCity.editText?.text.toString()
            roomModel.state = etPlaceState.editText?.text.toString()
            roomModel.country = etPlaceState.editText?.text.toString()
            roomModel.contactNo = etPlaceContact.editText?.text.toString()
            roomModel.title = etPlaceName.editText?.text.toString()
            roomModel.description = etPlaceDescription.editText?.text.toString()
            roomModel.host_id = "room model"
//                PreferenceHelper.readStringFromPreference(LocalKeys.KEY_USER_GOOGLE_ID)
            roomModel.price = etPlacePrice.editText?.text.toString().toLong()
            roomModel.priceForWhat = "Night stay"
            pvm.setRoomObject(roomModel)

            part6Card.visibility = View.GONE
            rtyhgt.text = "Please wait..."
            progressbar.visibility = View.VISIBLE

            pvm.addPlace(roomModel)
            uploadImages()

        }
    }

    private fun setViews(view: View) {
        val geoCoder = Geocoder(view.context, Locale.getDefault())
        val addresses: List<Address> =
            geoCoder.getFromLocation(
                roomModel.location_lat?.toDouble()!!,
                roomModel.location_long?.toDouble()!!,
                1
            )
        if (addresses.isNotEmpty()) {
            etPlaceCity.editText?.setText(addresses[0].locality.toString() ?: "")
            etPlaceState.editText?.setText(addresses[0].adminArea.toString() ?: "")
            etPlaceCountry.editText?.setText(addresses[0].countryName.toString() ?: "")
        }
    }


    private fun uploadImages() {
        downloadImageList.clear()
        for (i in 0 until imageList.size) {
            val uri: Uri = imageList[i]
            uploadTask(uri, roomModel.id)
        }
        uploadToRealTimeDatabase(downloadImageList)
    }

    private fun uploadTask(uri: Uri, shopId: String?) {
        val storageReference =
            FirebaseStorage.getInstance().getReference("placepics").child(shopId!!)
                .child(UUID.randomUUID().toString() + getFileExt(uri))
        Log.d("TAG", "uploadTask: ongoinig")
        storageReference
            .putFile(uri)
            .continueWithTask { task ->
                if (!task.isSuccessful) {
                    throw task.exception!!
                }
                Log.d("TAG", "uploadTask: " + storageReference.downloadUrl)
                storageReference.downloadUrl
            }.addOnCompleteListener {
                if (it.isSuccessful) {
                    val downloadUri = it.result
                    downloadImageList.add(downloadUri)
//                    uploadToRealTimeDatabase(downloadUri)
                }
            }
    }

    private fun uploadToRealTimeDatabase(uri: ArrayList<Uri>) {
        val ref = FirebaseDatabase.getInstance().getReference("places")
            .child(roomModel.id!!)
        unique = (System.currentTimeMillis() + (Math.random() * 257).toInt()).toString()
        ref.child("Images").child(unique).setValue(uri.toString()).addOnCompleteListener {
            if (it.isSuccessful) {
                Log.d("TAG", "uploadToRealTimeDatabase: Success")
                Handler().postDelayed({
                    navController.navigate(R.id.action_hostPart7Fragment_to_myHostPlacesFragment)
                }, 1000)
            } else Log.d("TAG", "uploadToRealTimeDatabase: Failed")
        }
    }

    private fun getFileExt(uri: Uri): String? {
        val mimeTypeMap = MimeTypeMap.getSingleton()
        return mimeTypeMap.getExtensionFromMimeType(activity?.contentResolver?.getType(uri))
    }


}