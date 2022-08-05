package com.cmpt362.rentit.userListings

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import com.cmpt362.rentit.Constants
import com.cmpt362.rentit.R
import com.cmpt362.rentit.createListing.CreateListingActivity
import com.cmpt362.rentit.databinding.ActivityMainBinding
import com.cmpt362.rentit.db.Listing
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import java.util.*
import kotlin.collections.ArrayList

class UserListingsListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var listViewListings: ListView
    private lateinit var userListingsList: ArrayList<Listing>
    private lateinit var db: FirebaseDatabase
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var userListingsListAdapter: UserListingsListAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_listings_list)

        initializeElements()
        getUserListings()

    }

    private fun initializeElements(){
        listViewListings = findViewById(R.id.userListingsActivity_listView_listings)
        userListingsList = ArrayList()
        db = Firebase.database
        firebaseAuth = FirebaseAuth.getInstance()
        userListingsListAdapter = UserListingsListAdapter(this, userListingsList)
        listViewListings.adapter = userListingsListAdapter

    }

    private fun getUserListings(){
        val user = firebaseAuth.currentUser

        if (user != null){
            val userId = user.uid
            val listingsReference = db.reference.child(Constants.LISTINGS_TABLE_NAME)

            listingsReference.addListenerForSingleValueEvent(object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    var arrayList: ArrayList<Listing> = ArrayList()
                    for (dataSnapShot in snapshot.children){
                        val postUserID = dataSnapShot.child(Constants.POSTER_USER_ID_TAG).value.toString()

                        if (postUserID == userId){
                            val listingId = dataSnapShot.child(Constants.LISTING_ID_TAG).value.toString()
                            val name = dataSnapShot.child(Constants.NAME_TAG).value.toString()
                            val type = dataSnapShot.child(Constants.TYPE_TAG).value.toString()
                            val price = dataSnapShot.child(Constants.PRICE_TAG).value.toString().toDouble()
                            val description = dataSnapShot.child(Constants.DESCRIPTION_TAG).value.toString()
                            val renterUserID = dataSnapShot.child(Constants.RENTER_USER_ID_TAG).value.toString()
                            val available = dataSnapShot.child(Constants.AVAILABLE_TAG).value.toString().toBoolean()
                            val location = dataSnapShot.child(Constants.LOCATION_TAG).value.toString()
                            val userListing = Listing(listingId, type, name, price, description,
                                postUserID, renterUserID, available, location)
                            userListingsList.add(userListing)
                        }
                    }

                    userListingsListAdapter.notifyDataSetChanged()

                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d("UserListingsActivity", error.toString())
                }

            })

        }
        else{
            Toast.makeText(this, Constants.PLEASE_LOGIN_MSG, Toast.LENGTH_SHORT).show()
        }

    }

    fun createNewListing(view: View){
        val intent = Intent(this, CreateListingActivity::class.java)
        startActivity(intent)
    }

}