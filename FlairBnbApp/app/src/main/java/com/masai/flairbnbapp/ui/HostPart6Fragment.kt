package com.masai.flairbnbapp.ui

import android.content.Intent
import android.graphics.drawable.AnimationDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.FirebaseApp
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.masai.flairbnbapp.R
import com.masai.flairbnbapp.models.RoomModel
import com.masai.flairbnbapp.recyclerviews.ImageListingAdapter
import com.masai.flairbnbapp.viewmodels.HostPlaceViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import kotlin.collections.ArrayList

@AndroidEntryPoint
class HostPart6Fragment : Fragment() {

    private val pvm by viewModels<HostPlaceViewModel>()
    var anim: AnimationDrawable? = null
    lateinit var roomModel: RoomModel


    private val imageList = ArrayList<Uri>()
    private val downloadImageList = ArrayList<Uri>()
    var unique = System.currentTimeMillis().toString()
    private var app: FirebaseApp? = null
    private var storage: FirebaseStorage? = null
    val adapter = ImageListingAdapter(downloadImageList)

    private lateinit var rvPart6: RecyclerView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_host_part6, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val navController = Navigation.findNavController(view)
        roomModel = pvm.getTheRoomModel()

        initRecyclerview(view)

        app = FirebaseApp.getInstance();
        storage = FirebaseStorage.getInstance(app!!);

        val constraintLayout = view.findViewById<ConstraintLayout>(R.id.part6Container)
        anim = constraintLayout.background as AnimationDrawable
        anim?.setEnterFadeDuration(5000)
        anim?.setExitFadeDuration(2000)

        view.findViewById<TextView>(R.id.uploadPics).setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            startActivityForResult(intent, 20)
        }

        view.findViewById<Button>(R.id.btnNext7).setOnClickListener {
            navController.navigate(R.id.action_hostPart6Fragment_to_hostPart7Fragment)
        }

    }

    private fun initRecyclerview(view: View) {
        rvPart6 = view.findViewById(R.id.rvPart6)
        rvPart6.layoutManager = GridLayoutManager(view.context, 3)
        rvPart6.adapter = adapter
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 20) {
            if (resultCode == AppCompatActivity.RESULT_OK) {
                if (data?.clipData != null) {

                    imageList.clear()
                    val countClipData = data.clipData!!.itemCount
                    var currentImageSlect = 0
                    while (currentImageSlect < countClipData) {
                        val ImageUri: Uri = data.clipData!!.getItemAt(currentImageSlect).uri
                        imageList.add(ImageUri)
                        currentImageSlect += 1
                    }
                    if (imageList.size != 0) {
                        uploadImages()
                    }
                } else {
                    Toast.makeText(
                        view?.context,
                        "Please Select Multiple Images",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun uploadImages() {
        downloadImageList.clear()
        for (i in 0 until imageList.size) {
            val uri: Uri = imageList[i]

            uploadTask(uri, roomModel.id)
        }
    }

    private fun uploadTask(uri: Uri, shopId: String?) {
        val storageReference = FirebaseStorage.getInstance().getReference("placepics").child(shopId!!)
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
                    adapter.notifyDataSetChanged()
                    uploadToRealTimeDatabase(downloadUri)
                }
            }
    }

    private fun uploadToRealTimeDatabase(uri: Uri) {
        val ref = FirebaseDatabase.getInstance().getReference("places")
            .child(roomModel.id!!)
        unique = (System.currentTimeMillis() + (Math.random() * 257).toInt()).toString()
        ref.child("Images").child(unique).setValue(uri.toString()).addOnCompleteListener {
            if (it.isSuccessful) {
                Log.d("TAG", "uploadToRealTimeDatabase: Success")
            } else Log.d("TAG", "uploadToRealTimeDatabase: Failed")
        }
    }

    private fun getFileExt(uri: Uri): String? {
        val mimeTypeMap = MimeTypeMap.getSingleton()
        return mimeTypeMap.getExtensionFromMimeType(activity?.contentResolver?.getType(uri))
    }

}