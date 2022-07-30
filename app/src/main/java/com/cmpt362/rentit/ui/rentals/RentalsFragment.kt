package com.cmpt362.rentit.ui.rentals

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.GridView
import android.widget.SearchView
import androidx.fragment.app.Fragment
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
        database = Firebase.database.getReference("Listings")
        gridView = requireView().findViewById(R.id.grid_view)
        list = ArrayList() // get from db eventually
        database.get().addOnSuccessListener {
            listings.clear()
            if (it.hasChildren()){
                println("DEBUG: ${it.children}")
                it.children.forEach{ _listing ->
                    val key = _listing.key?.toInt() ?: -1
                    val type = _listing.child("type").getValue(String::class.java)
                    val name = _listing.child("name").getValue(String::class.java)
                    val price = _listing.child("price").getValue(Double::class.java)
                    val description = _listing.child("description").getValue(String::class.java)
                    val postUserID = _listing.child("postUserID").getValue(Int::class.java)?: -1
                    val renterUserID = _listing.child("renterUserID").getValue(Int::class.java)?: -1
                    val available = _listing.child("available").getValue(Boolean::class.java)?: false
                    val listing = Listing(key, type, name, price, description, postUserID, renterUserID, available)
                    listings.add(listing)
                    list = list + GridViewModel(listing.id.toLong(), R.drawable.duck, listing)
                    list = list + GridViewModel(listing.id.toLong(), R.drawable.car, listing)
                    list = list + GridViewModel(listing.id.toLong(), R.drawable.guitar, listing)
                    list = list + GridViewModel(listing.id.toLong(), R.drawable.book, listing)
                }
//        gridview stuff
                gridViewAdapter = GridAdapter(list, requireActivity())
                gridView.adapter = gridViewAdapter
            }
        }.addOnFailureListener {
            println("DEBUG: failure loading data: $it")
        }

        searchBar = requireView().findViewById(R.id.search_bar)

        // click listener for our grid view.
        gridView.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            println("DEBUG: We will open the detail view now for ${list[position].listing.name}")
            val intent = Intent(requireActivity(), DetailActivity::class.java)
            intent.putExtra("id",list[position].id)
            startActivity(intent)
        }

    }

}