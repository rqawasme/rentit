package com.cmpt362.rentit.userListings

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ListView
import com.cmpt362.rentit.R
import com.cmpt362.rentit.databinding.ActivityMainBinding
import com.cmpt362.rentit.db.Listing
import com.google.gson.Gson

class UserListingsListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var listViewListings: ListView
    private lateinit var userListingsList: ArrayList<Listing>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_listings_list)
        listViewListings = findViewById(R.id.userListingsActivity_listView_listings)

        userListingsList = ArrayList()
//        userListingsList.add(Listing(1,"type","Nissian GTR Nismo",5337.00,"test listing","1","1",true))
//        userListingsList.add(Listing(1,"type","Mercedes C63",2337.00,"test listing","1","1",true))
        listViewListings.adapter = UserListingsListAdapter(this, userListingsList)


//        TODO: save location as
//            Gson().toJson(location)
    }
}