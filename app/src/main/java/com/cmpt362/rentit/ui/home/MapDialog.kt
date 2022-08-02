package com.cmpt362.rentit.ui.home

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.cmpt362.rentit.R
import com.cmpt362.rentit.details.DetailActivity
import com.cmpt362.rentit.details.DetailViewPagerAdapter
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlin.properties.Delegates

class MapDialog: DialogFragment() {
    private lateinit var name: String
    private var key by Delegates.notNull<Long>()
    private lateinit var description: String
    private lateinit var imageView: ImageView
    private lateinit var textView: TextView

    companion object {
        const val MAP_DIALOG_NAME_KEY = "MAP_DIALOG_NAME_KEY"
        const val MAP_DIALOG_DESCRIPTION_KEY = "MAP_DIALOG_DESCRIPTION_KEY"
        const val MAP_DIALOG_ID_KEY = "MAP_DIALOG_ID_KEY"
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        lateinit var dialog: Dialog
        val bundle = arguments
        if (bundle != null) {
            name = bundle.getString(MAP_DIALOG_NAME_KEY, "")
            key = bundle.getString(MAP_DIALOG_ID_KEY, "-1").toLong()
            description = bundle.getString(MAP_DIALOG_DESCRIPTION_KEY, "")
        }
        val view = requireActivity().layoutInflater.inflate(R.layout.dialog_map, null)
        imageView = view.findViewById(R.id.mapDialogImage)
        textView = view.findViewById(R.id.mapDialogText)
        val builder = AlertDialog.Builder(requireActivity())
        builder.setView(view)
        builder.setTitle(name)
        textView.text = name
        builder.setPositiveButton(getString(R.string.view_more)) { dialog, id ->
            println("DEBUG: We will open the detail view now for $name")
            val intent = Intent(requireActivity(), DetailActivity::class.java)
            intent.putExtra("id", key)
            startActivity(intent)
        }
        builder.setNegativeButton(getString(R.string.go_back)) {dialog, id ->
            println("DEBUG: BACK TO MAP")
        }
        dialog = builder.create()
        return dialog
    }

//
//    //Load images from Firebase to ViewPager
//    private fun loadImages(imageList: ArrayList<ByteArray>){
//        var reference = Firebase.storage.reference
//        viewPager = findViewById(R.id.detail_viewpager)
//        Thread(){
//            reference.child("listings/" + listingID + "/").listAll().addOnSuccessListener {
//                for(entry in it.items){
//                    entry.getBytes(ONE_MEGABYTE*50).addOnSuccessListener {
//                        imageList.add(it)
//                        viewPagerAdapter = DetailViewPagerAdapter(this, imageList)
//                        viewPager.adapter=viewPagerAdapter
//                    }
//                }
//            }
//        }.start()
//    }
//
//    //Read info from Firebase
//    private fun readThread(listingID:Long){
//        Thread(){
//            val myRefListings=db.getReference("Listings").child(listingID.toString())
//
//            myRefListings.get().addOnCompleteListener{ task->
//                if(task.isSuccessful){
//                    val snapshot= task.result
//                    name=snapshot.child("name").getValue(String::class.java)
//                    price=snapshot.child("price").getValue(Float::class.java)
//                    description=snapshot.child("description").getValue(String::class.java)
//                    posterID=snapshot.child("postUserID").getValue(String::class.java)
//
//                    val nameTextView=findViewById<TextView>(R.id.details_name)
//                    val priceTextView=findViewById<TextView>(R.id.details_price)
//                    val descriptionTextView=findViewById<TextView>(R.id.details_description)
//
//                    descriptionTextView.movementMethod= ScrollingMovementMethod()
//
//                    nameTextView.text = name
//                    priceTextView.text= "$" + String.format("%.2f",price)
//                    descriptionTextView.text=description
//
//                    //Get contact info from Firebase, after we get the posterID.
//                    getContactInfo(posterID!!)
//                }
//                else{
//                    Log.d("TAG", task.exception!!.message!!)
//                }
//            }
//        }.start()
//    }
}