package com.cmpt362.rentit.ui.rentals

import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.GridView
import android.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.cmpt362.rentit.Constants
import com.cmpt362.rentit.details.DetailActivity
import com.cmpt362.rentit.R
import com.cmpt362.rentit.Utils.getCurrentLocation
import com.cmpt362.rentit.db.Listing
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class RentalsFragment : Fragment() {
    private lateinit var gridView: GridView
    private lateinit var list: List<GridViewModel>
    private lateinit var gridViewAdapter: GridAdapter
    private lateinit var database: DatabaseReference
    private lateinit var searchBar: SearchView
    private var listings = ArrayList<Listing>()
    private lateinit var currentLocation: Location
    private val locationType = object : TypeToken<LatLng>() {}.type

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_rentals, container, false)
    }

    override fun onStart() {
        super.onStart()
        currentLocation = getCurrentLocation(requireActivity(), requireContext())
        searchBar = requireView().findViewById(R.id.search_bar)
        database = Firebase.database.getReference(Constants.LISTINGS_TABLE_NAME)
        gridView = requireView().findViewById(R.id.grid_view)
        list = ArrayList() // get from db eventually
        database.get().addOnSuccessListener {
            listings.clear()
            if (it.hasChildren()){
                it.children.forEach{ _listing ->
//                    TODO: don't include your listings
                    val id = _listing.child("listingID").getValue(String::class.java)!!
                    val type = _listing.child("type").getValue(String::class.java)
                    val name = _listing.child("name").getValue(String::class.java)
                    val price = _listing.child("price").getValue(Double::class.java)
                    val description = _listing.child("description").getValue(String::class.java)
                    val postUserID = _listing.child("postUserID").getValue(String::class.java)
                    val renterUserID = _listing.child("renterUserID").getValue(String::class.java)
                    val available = _listing.child("available").getValue(Boolean::class.java)?: false
                    val locationString = _listing.child("location").getValue(String::class.java)
                    val location: LatLng = Gson().fromJson(locationString, locationType)
                    val results = FloatArray(1)
                    Location.distanceBetween(currentLocation.latitude, currentLocation.longitude, location.latitude, location.longitude, results)
                    val distance = results[0]
                    println("DEBUG: $distance ")
                    val listing = Listing(id, type, name, price, description, postUserID, renterUserID, available, locationString)
                    listings.add(listing)
                    if (listing.available) {
                        list = list + GridViewModel(id, listing, distance)
                    }
                }
//        gridview stuff
                list = list.sortedWith(compareBy { l ->
                    println("DEbug: ${l.listing.name}")
                    l.distance
                })
                gridViewAdapter = GridAdapter(list, requireActivity())
                gridView.adapter = gridViewAdapter
            }
        }.addOnFailureListener {
            println("DEBUG: failure loading data: $it")
        }

        // click listener for our grid view.
        gridView.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            println("DEBUG: We will open the detail view now for ${list[position].listing.name}")
            val intent = Intent(requireActivity(), DetailActivity::class.java)
            intent.putExtra("id",list[position].id)
            startActivity(intent)
        }

        searchBar.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String): Boolean {
                if (listContains(query)){
                    var newList = ArrayList<GridViewModel>()
                    for( item in list){
                        if (item.listing.name?.contains(query) == true && item.listing.available) {
                            val location: LatLng = Gson().fromJson(item.listing.location, locationType)
                            val results = FloatArray(1)
                            Location.distanceBetween(currentLocation.latitude, currentLocation.longitude, location.latitude, location.longitude, results)
                            val distance = results[0]
                            newList += GridViewModel(
                                item.listing.listingID!!,
                                item.listing,
                                distance
                            )
                        }
                    }
                    newList = newList.sortedWith(compareBy { l ->
                        l.distance
                    }) as ArrayList<GridViewModel>
                    gridViewAdapter = GridAdapter(newList, requireActivity())
                    gridView.adapter = gridViewAdapter
                } else {
                    Toast.makeText(requireContext(), "No rentals found", Toast.LENGTH_LONG)
                        .show()
                }

                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText == null || newText == ""){
                    gridViewAdapter = GridAdapter(list, requireActivity())
                    gridView.adapter = gridViewAdapter
                }
                return false
            }
        })

    }

    fun listContains(substring: String): Boolean {
        for (entry in list) {
            if (entry.listing.name?.contains(substring) == true){
                return true
            }
        }
        return false
    }
}