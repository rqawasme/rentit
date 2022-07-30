package com.cmpt362.rentit.userListings

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ListView
import com.cmpt362.rentit.R

class UserListingsListActivity : AppCompatActivity() {

    private lateinit var listViewListings: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_listings_list)
        listViewListings = findViewById(R.id.userListingsActivity_listView_listings)
    }
}