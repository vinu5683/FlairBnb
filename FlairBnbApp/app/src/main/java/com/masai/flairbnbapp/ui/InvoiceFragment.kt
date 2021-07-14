package com.masai.flairbnbapp.ui

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.itextpdf.text.Document
import com.itextpdf.text.Image
import com.itextpdf.text.pdf.PdfWriter
import com.masai.flairbnbapp.R
import com.masai.flairbnbapp.localdatabases.LocalKeys
import com.masai.flairbnbapp.localdatabases.PreferenceHelper
import com.masai.flairbnbapp.models.BookPlaceModel
import com.masai.flairbnbapp.viewmodels.PlacesViewModel
import com.masai.flairbnbapp.viewmodels.UsersViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_invoice.*
import java.io.File
import java.io.FileOutputStream
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
    lateinit var receiptLL: LinearLayout
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
            receiptLL = findViewById(R.id.receiptLL)

            tvPrintReceipt.setOnClickListener {
                receiptLL.visibility = View.GONE
                takeScreenShot()
                receiptLL.visibility = View.VISIBLE
            }
            tvEmailReceipt.setOnClickListener {
                Toast.makeText(v.context, "We are adding this feature soon", Toast.LENGTH_SHORT)
                    .show()
            }
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

    var b: Bitmap? = null
    var path: String = ""
    private fun takeScreenShot() {
        val folder = File(Environment.getExternalStorageDirectory().absolutePath + "/Signature/")
        if (!folder.exists()) {
            val success: Boolean = folder.mkdir()
        }
        path = folder.absolutePath
        path += "/signature_pdf_${System.currentTimeMillis()}.pdf"
        Log.d("TAG", "takeScreenShot: $path")
        val u: View = v.findViewById(R.id.scroll)
        val z = v.findViewById(R.id.scroll) as ScrollView // parent view
        val totalHeight = z.getChildAt(0).height // parent view height
        val totalWidth = z.getChildAt(0).width // parent view width

        //Save bitmap to  below path
        val extr = Environment.getExternalStorageDirectory().toString() + "/Signature/"
        val file = File(extr)
        if (!file.exists())
            file.mkdir()
        val fileName: String = "signature_img_" + ".jpg"
        val myPath = File(extr, fileName)
        val imagesUri = myPath.path
        Log.d("TAG", "takeScreenShot: $imagesUri")
        var fos: FileOutputStream? = null

        b = getBitmapFromView(u, totalHeight, totalWidth)
//        Glide.with(v.context).load(b).into(someimage)
        try {
            fos = FileOutputStream(myPath)
            b?.compress(Bitmap.CompressFormat.JPEG, 100, fos)
            fos.flush()
            fos.close()
        } catch (e: Exception) {
            Log.d("TAG", "takeScreenShot: $e")
            e.printStackTrace()
        }
        createPdf() // create pdf after creating bitmap and saving
    }

    fun getBitmapFromView(view: View, totalHeight: Int, totalWidth: Int): Bitmap? {
        val bitmap =
            Bitmap.createBitmap(totalWidth, totalHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        view.draw(canvas)
        return bitmap
    }

    private fun createPdf() {
        val document = Document()

        val directoryPath = Environment.getExternalStorageDirectory().toString()
        val extr = Environment.getExternalStorageDirectory().toString() + "/Signature/"
        path = "${extr}signature_img_.jpg"
        PdfWriter.getInstance(
            document,
            FileOutputStream("$directoryPath/Signature/bill.pdf")
        ) //  Change pdf's name.


        document.open()

        val image: Image =
            Image.getInstance(path) // Change image's name and extension.


        val scaler: Float = 34f  // 0 means you have no indentation. If you have any, change it.

        image.scalePercent(scaler)
        image.setAlignment(Image.ALIGN_CENTER or Image.ALIGN_TOP)

        document.add(image)
        document.close()
        Handler().postDelayed({
            openPdf(path)
        }, 1000)
    }

    fun openPdf(path: String) {
        val directoryPath = Environment.getExternalStorageDirectory().toString()
        val selectedUri = Uri.parse("$directoryPath/Signature/");
        val intent = Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(selectedUri, "resource/folder");

        if (intent.resolveActivityInfo(activity?.packageManager!!, 0) != null) {
            startActivity(intent);
        } else {
            Toast.makeText(
                v.context,
                "something went wrong while opening activity",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}