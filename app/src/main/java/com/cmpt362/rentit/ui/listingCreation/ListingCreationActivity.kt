package com.cmpt362.rentit.ui.listingCreation

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.RadioGroup
import android.widget.TextView
import com.cmpt362.rentit.R
import com.cmpt362.rentit.db.Listing
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class ListingCreationActivity : AppCompatActivity() {
    private val tempImgName = "temp_img.jpg"
    private lateinit var tempImgUri: Uri
    private val profileImgName = "profile_img.jpg"
    private lateinit var profileImgUri: Uri

    private lateinit var photoView: ImageView
    private lateinit var nameView: EditText
    private lateinit var listingID:String
    private lateinit var typeView: EditText
    private lateinit var priceView: EditText
    private lateinit var availableView: RadioGroup
    private lateinit var descriptionView: EditText
    private var postUserID:String? = null
    private var renterUserID:String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_listing_creation)


    }
    fun onSaveClicked(view: View) {
        var firebaseAuth = FirebaseAuth.getInstance()
        val firebaseUser = firebaseAuth.currentUser
        if (firebaseUser != null) {
            postUserID=firebaseUser.uid
        }

        photoView = findViewById(R.id.profile_photo)
        nameView = findViewById(R.id.listing_name)
        typeView = findViewById(R.id.listing_type)
        priceView = findViewById(R.id.listing_price)
        availableView = findViewById(R.id.available_buttons)
        descriptionView = findViewById(R.id.listing_description)

        Thread() {
            val myRefListing = Firebase.database.getReference("Listing")
            listingID = myRefListing.push().key.toString()

            val listing = Listing(
                listingID,
                typeView.text.toString(),
                nameView.text.toString(),
                priceView.text.toString().toDouble(),
                descriptionView.text.toString(),
                postUserID,
                renterUserID,
                true
            )

            if (listingID != null) {
                myRefListing.child(listingID).setValue(listing)
            }
        }.start()
    }


}