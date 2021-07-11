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

    var adapter = ImageListingAdapter(imageList)

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
            pvm.imageList = imageList
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
                        Log.d("TAG", "onActivityResult: " + ImageUri.toString())
                        currentImageSlect += 1
                    }
                    updateRecyclerView()
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

    private fun updateRecyclerView() {
        pvm.setImageListObject(imageList)
        adapter = ImageListingAdapter(imageList)
        rvPart6.adapter = adapter
        adapter.notifyDataSetChanged()
    }

    override fun onResume() {
        super.onResume()
        if (anim != null && !anim!!.isRunning) anim!!.start()
    }

    override fun onPause() {
        super.onPause()
        if (anim != null && anim!!.isRunning) anim!!.stop()
    }


}