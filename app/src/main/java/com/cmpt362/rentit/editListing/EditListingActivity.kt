package com.cmpt362.rentit.editListing

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.MediaStore.Images
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cmpt362.rentit.Constants
import com.cmpt362.rentit.R
import com.cmpt362.rentit.Utils
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import java.io.ByteArrayOutputStream


class EditListingActivity : AppCompatActivity() {

    private lateinit var recyclerViewPhotos: RecyclerView
    private lateinit var buttonAddPhotos: Button
    private lateinit var bitmapArrayList: ArrayList<Bitmap>
    private lateinit var recyclerAdapter: ListingPhotosBitmapRecyclerAdapter
    private lateinit var galleryResult: ActivityResultLauncher<Intent>

    private lateinit var textViewUsername: TextView
    private lateinit var textInputEditTextTitle: TextInputEditText
    private lateinit var textInputEditTextPrice: TextInputEditText
    private lateinit var textInputEditTextDescription: TextInputEditText
    private lateinit var spinnerListingTypes: Spinner
    private lateinit var spinnerListingAvailability: Spinner

    private lateinit var db: FirebaseDatabase
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var storageReference: StorageReference

    private lateinit var listingID: String
    private lateinit var addedPhotosUriList: ArrayList<Uri>

    private var listingType: String = ""
    private var listingTitle: String = ""
    private var listingPrice: Double = 0.0
    private var listingDescription: String = ""
    private var listingAvailability: Boolean = true


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_listing)

        initializeElements()
        displayUsername()
        setupAddPhotoButtonOnClick()
        populateListingInformation()
    }


    private fun populateListingInformation() {
        val listingReference = db.getReference(Constants.LISTINGS_TABLE_NAME)

        val listingDataSnapshot = listingReference.child(listingID).get()

        listingDataSnapshot.addOnSuccessListener {
            if (it.exists()){
                listingType = it.child(Constants.TYPE_TAG).getValue(String::class.java)!!
                listingTitle = it.child(Constants.NAME_TAG).getValue(String::class.java)!!
                listingPrice = it.child(Constants.PRICE_TAG).getValue(Double::class.java)!!
                listingDescription = it.child(Constants.DESCRIPTION_TAG).getValue(String::class.java)!!
                listingAvailability = it.child(Constants.AVAILABLE_TAG).getValue(Boolean::class.java)!!

                textInputEditTextTitle.setText(listingTitle)
                textInputEditTextPrice.setText(listingPrice.toString())
                textInputEditTextDescription.setText(listingDescription)

                val listingTypeList = resources.getStringArray(R.array.array_listing_types)
                val listingAvailabilityList = resources.getStringArray(R.array.array_listing_availabilities)

                for (i in 0 .. listingTypeList.size - 1){
                    if (listingType == listingTypeList[i]){
                        spinnerListingTypes.setSelection(i)
                    }
                }

                for (i in 0 .. listingAvailabilityList.size - 1){
                    if (listingAvailability == true && listingAvailabilityList[i] == Constants.AVAILABLE_TEXT){
                        spinnerListingAvailability.setSelection(i)
                    }
                    else if (listingAvailability == false && listingAvailabilityList[i] == Constants.UNAVAILABLE_TEXT){
                        spinnerListingAvailability.setSelection(i)
                    }
                }

            }
        }

        Thread(){
            storageReference.child("${Constants.LISTINGS_PATH}/${listingID}").listAll().addOnSuccessListener { listResult ->

                for(image in listResult.items){
                    image.getBytes(Constants.ONE_MEGABYTE * 50).addOnSuccessListener {
                        val bitmap = BitmapFactory.decodeByteArray(it, 0, it.size)

                        bitmapArrayList.add(bitmap)
                        recyclerAdapter.notifyDataSetChanged()
                    }

                }

            }
        }.start()
    }

    fun getImageUri(bitmap: Bitmap, title: String): Uri{
        val bytes = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = Images.Media.insertImage(this.contentResolver, bitmap, title, null)
        return Uri.parse(path)
    }


    private fun initializeElements() {
        recyclerViewPhotos = findViewById(R.id.editListingActivity_recyclerView_photos)
        buttonAddPhotos = findViewById(R.id.editListingActivity_button_add_photos)
        bitmapArrayList = ArrayList()
        addedPhotosUriList = ArrayList()
        recyclerAdapter = ListingPhotosBitmapRecyclerAdapter(bitmapArrayList)
        recyclerViewPhotos.layoutManager = GridLayoutManager(this, Constants.PHOTOS_PER_ROW)
        recyclerViewPhotos.adapter = recyclerAdapter

        textViewUsername = findViewById(R.id.editListingActivity_textView_username)
        textInputEditTextTitle = findViewById(R.id.editListingActivity_textInputEditText_title)
        textInputEditTextPrice = findViewById(R.id.editListingActivity_textInputEditText_price)
        textInputEditTextDescription = findViewById(R.id.editListingActivity_textInputEditText_description)
        spinnerListingTypes = findViewById(R.id.editListingActivity_spinner_listing_type)
        spinnerListingAvailability = findViewById(R.id.editListingActivity_spinner_listing_availability)

        listingID = intent.getStringExtra(Constants.EDIT_LISTING_EXTRA_STRING)!!

        db = Firebase.database
        firebaseAuth = FirebaseAuth.getInstance()
        storageReference = Firebase.storage.reference

        galleryResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult())
        {
                result: ActivityResult ->

            if (result.resultCode == Activity.RESULT_OK){

                if (result.data!!.clipData != null){
                    val count = result.data!!.clipData!!.itemCount

                    for (i in 0 .. count - 1){
                        val imageUri = result.data!!.clipData!!.getItemAt(i).uri
                        val bitmap = Utils.getBitmap(this, imageUri)
                        bitmapArrayList.add(bitmap)
                        addedPhotosUriList.add(imageUri)
                    }
                    recyclerAdapter.notifyDataSetChanged()
                }
                else{
                    //Selected single image
                    val imageUri = result.data?.data!!
                    val bitmap = Utils.getBitmap(this, imageUri)
                    bitmapArrayList.add(bitmap)
                    addedPhotosUriList.add(imageUri)
                    recyclerAdapter.notifyDataSetChanged()
                }
            }
        }

    }


    private fun displayUsername(){
        //Check if logged in

        if (firebaseAuth.currentUser != null) {
            val userId = firebaseAuth.currentUser!!.uid
            val myRefUsers = db.getReference(Constants.USERS_TABLE_NAME)

            val userDataSnapshot = myRefUsers.child(userId).get()

            userDataSnapshot.addOnSuccessListener {
                if (it.exists()){
                    textViewUsername.text = it.child(Constants.USERNAME_PATH).getValue(String::class.java).toString()
                }
            }
        }
        else{
            Toast.makeText(this, Constants.PLEASE_LOGIN_MSG, Toast.LENGTH_SHORT).show()
            this.finish()
        }
    }

    private fun setupAddPhotoButtonOnClick(){
        buttonAddPhotos.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            intent.type = "image/*"
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            galleryResult.launch(intent)
        }
    }

    fun saveEditedListing(view: View){
        val listingTypeList = resources.getStringArray(R.array.array_listing_types)
        val listingAvailabilityList = resources.getStringArray(R.array.array_listing_availabilities)

        val title = textInputEditTextTitle.text.toString()
        val price = textInputEditTextPrice.text.toString()
        val description = textInputEditTextDescription.text.toString()
        val type = listingTypeList[spinnerListingTypes.selectedItemPosition]
        val availability = spinnerListingAvailability.selectedItemPosition
        var isAvailable = true

        for (i in 0 .. listingAvailabilityList.size - 1){
            if (listingAvailabilityList[availability] == Constants.AVAILABLE_TEXT){
                isAvailable = true
            }
            else if (listingAvailabilityList[availability] == Constants.UNAVAILABLE_TEXT){
                isAvailable = false
            }
        }

        if (TextUtils.isEmpty(title)){
            textInputEditTextTitle.error = Constants.NO_LISTING_TITLE_ERROR
            textInputEditTextTitle.requestFocus()
        }
        else if (TextUtils.isEmpty(price)){
            textInputEditTextPrice.error = Constants.NO_LISTING_PRICE_ERROR
            textInputEditTextPrice.requestFocus()
        }
        else{
            if (!addedPhotosUriList.isEmpty() || title != listingTitle ||
                price != listingPrice.toString() || description != listingDescription ||
                type != listingType || isAvailable != listingAvailability){

                val listingReference = db.getReference(Constants.LISTINGS_TABLE_NAME)

                val newListingInfo = mapOf<String, Any>(
                    "listingID" to listingID,
                    "type" to type,
                    "name" to title,
                    "price" to price.toDouble(),
                    "description" to description,
                    "available" to isAvailable,
                )

                listingReference.child(listingID).updateChildren(newListingInfo).addOnSuccessListener {
                    Toast.makeText(this, "Listing updated", Toast.LENGTH_SHORT).show()
                }.addOnFailureListener {
                    Toast.makeText(this, "Failed to update listing", Toast.LENGTH_SHORT).show()
                }

            }

            if (!addedPhotosUriList.isEmpty()){
                for (i in 0 .. addedPhotosUriList.size - 1){
                    storageReference = FirebaseStorage.getInstance().getReference("${Constants. LISTINGS_PATH}/${listingID}/${i+ (bitmapArrayList.size - addedPhotosUriList.size)}")
                    storageReference.putFile(addedPhotosUriList[i])
                }
            }
            finish()
        }
    }

    fun cancelEditListing(view: View){
        Toast.makeText(this, "Changes discarded", Toast.LENGTH_SHORT).show()
        finish()
    }
}