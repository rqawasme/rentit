package com.cmpt362.rentit.ui.rentals

import android.content.Intent
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
import com.cmpt362.rentit.db.Listing
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class RentalsFragment : Fragment() {
    private lateinit var gridView: GridView
    private lateinit var list: List<GridViewModel>
    private lateinit var gridViewAdapter: GridAdapter
    private lateinit var database: DatabaseReference
    private lateinit var searchBar: SearchView
    private var listings = ArrayList<Listing>()

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
        searchBar = requireView().findViewById(R.id.search_bar)
        database = Firebase.database.getReference(Constants.LISTINGS_TABLE_NAME)
        gridView = requireView().findViewById(R.id.grid_view)
        list = ArrayList() // get from db eventually
        database.get().addOnSuccessListener {
            listings.clear()
            if (it.hasChildren()){
                it.children.forEach{ _listing ->
                    val id = _listing.child("listingID").getValue(String::class.java)!!
                    val type = _listing.child("type").getValue(String::class.java)
                    val name = _listing.child("name").getValue(String::class.java)
                    val price = _listing.child("price").getValue(Double::class.java)
                    val description = _listing.child("description").getValue(String::class.java)
                    val postUserID = _listing.child("postUserID").getValue(String::class.java)
                    val renterUserID = _listing.child("renterUserID").getValue(String::class.java)
                    val available = _listing.child("available").getValue(Boolean::class.java)?: false
                    val locationString = _listing.child("location").getValue(String::class.java)
                    val listing = Listing(id, type, name, price, description, postUserID, renterUserID, available, locationString)
                    listings.add(listing)
                    list = list + GridViewModel(id, listing)
//                    uncomment to test more listings
//                    list = list + GridViewModel(id, listing)
//                    list = list + GridViewModel(id, listing)
//                    list = list + GridViewModel(id, listing)
                }
//        gridview stuff
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
                    val newList = ArrayList<GridViewModel>()
                    for( item in list){
                        if (item.listing.name?.contains(query) == true) {
                            newList += GridViewModel(
                                item.listing.listingID!!,
                                item.listing
                            )
                        }
                    }
                    gridViewAdapter = GridAdapter(newList, requireActivity())
                    gridView.adapter = gridViewAdapter
                } else {
//                    TODO: Should we edit the toast or make something else happen?
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