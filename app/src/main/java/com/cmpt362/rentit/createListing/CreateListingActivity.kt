package com.cmpt362.rentit.createListing

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cmpt362.rentit.Constants
import com.cmpt362.rentit.R
import com.cmpt362.rentit.Utils
import com.cmpt362.rentit.db.Listing
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class CreateListingActivity : AppCompatActivity() {

    private lateinit var recyclerViewPhotos: RecyclerView
    private lateinit var buttonAddPhotos: Button
    private lateinit var uriArrayList: ArrayList<Uri>
    private lateinit var recyclerAdapter: CreateListingPhotosRecyclerAdapter
    private lateinit var galleryResult: ActivityResultLauncher<Intent>

    private lateinit var textViewUsername: TextView
    private lateinit var textInputEditTextTitle: TextInputEditText
    private lateinit var textInputEditTextPrice: TextInputEditText
    private lateinit var textInputEditTextDescription: TextInputEditText
    private lateinit var spinnerListingTypes: Spinner

    private lateinit var db: FirebaseDatabase
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var storageReference: StorageReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_listing)
        Utils.checkPermissions(this)
        initializeElements()
        displayUsername()
        setupAddPhotoButtonOnClick()
    }

    private fun setupAddPhotoButtonOnClick(){
        buttonAddPhotos.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            intent.type = "image/*"
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            galleryResult.launch(intent)
        }
    }

    private fun initializeElements() {
        recyclerViewPhotos = findViewById(R.id.createListingActivity_recyclerView_photos)
        buttonAddPhotos = findViewById(R.id.createListingActivity_button_add_photos)
        uriArrayList = ArrayList()
        recyclerAdapter = CreateListingPhotosRecyclerAdapter(uriArrayList)
        recyclerViewPhotos.layoutManager = GridLayoutManager(this, Constants.PHOTOS_PER_ROW)
        recyclerViewPhotos.adapter = recyclerAdapter

        textViewUsername = findViewById(R.id.createListingActivity_textView_username)
        textInputEditTextTitle = findViewById(R.id.createListingActivity_textInputEditText_title)
        textInputEditTextPrice = findViewById(R.id.createListingActivity_textInputEditText_price)
        textInputEditTextDescription = findViewById(R.id.createListingActivity_textInputEditText_description)
        spinnerListingTypes = findViewById(R.id.createListingActivity_spinner_listing_type)

        db = Firebase.database
        firebaseAuth = FirebaseAuth.getInstance()

        galleryResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult())
        {
                result: ActivityResult ->

            if (result.resultCode == Activity.RESULT_OK){

                if (result.data!!.clipData != null){
                    val count = result.data!!.clipData!!.itemCount

                    for (i in 0 .. count - 1){
                        val imageUri = result.data!!.clipData!!.getItemAt(i).uri
                        uriArrayList.add(imageUri)
                    }
                    recyclerAdapter.notifyDataSetChanged()
                }
                else{
                    //Selected single image
                    val imageURI = result.data?.data!!
                    uriArrayList.add(imageURI)
                    recyclerAdapter.notifyDataSetChanged()
                }
            }
        }
    }

    private fun displayUsername(){
        val userId = firebaseAuth.currentUser!!.uid
        val myRefUsers = db.getReference(Constants.USERS_TABLE_NAME)

        val userDataSnapshot = myRefUsers.child(userId).get()

        userDataSnapshot.addOnSuccessListener {
            if (it.exists()){
                textViewUsername.text = it.child(Constants.USERNAME_PATH).getValue(String::class.java).toString()
            }
        }
    }


    fun publishListing(view: View){
        val listingTitle = textInputEditTextTitle.text.toString()
        val listingPrice = textInputEditTextPrice.text.toString()
        val listingDescription = textInputEditTextDescription.text.toString()
        val listingType = spinnerListingTypes.selectedItemPosition

        if (TextUtils.isEmpty(listingTitle)){
            textInputEditTextTitle.error = Constants.NO_LISTING_TITLE_ERROR
            textInputEditTextTitle.requestFocus()
        }
        else if (TextUtils.isEmpty(listingPrice)){
            textInputEditTextPrice.error = Constants.NO_LISTING_PRICE_ERROR
            textInputEditTextPrice.requestFocus()
        }
        else if (uriArrayList.isEmpty()){
            Toast.makeText(this, "Please select at least 1 photo", Toast.LENGTH_SHORT).show()
        }
        else{

            val userId = firebaseAuth.currentUser!!.uid
            val myRefListings = db.getReference(Constants.LISTINGS_TABLE_NAME)
            val listingId = myRefListings.push().key.toString()

            val listingTypeArray = resources.getStringArray(R.array.createListingActivity_array_types)
            var listingTypeString:String = listingTypeArray[listingType]

            if (!uriArrayList.isEmpty()){

                for (i in 0 .. uriArrayList.size - 1){
                    storageReference = FirebaseStorage.getInstance().getReference("${Constants. LISTINGS_PATH}/${listingId}/${i}")
                    storageReference.putFile(uriArrayList[i])
                }

            }


            val newListing = Listing(listingId,listingTypeString, listingTitle,
                listingPrice.toDouble(), listingDescription, userId, null, true, null)

            myRefListings.child(listingId).setValue(newListing)
            finish()
        }


    }

}