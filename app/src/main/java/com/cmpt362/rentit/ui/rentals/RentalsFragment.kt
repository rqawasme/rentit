package com.cmpt362.rentit.ui.rentals

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.GridView
import androidx.fragment.app.Fragment
import com.cmpt362.rentit.DetailActivity
import com.cmpt362.rentit.R

class RentalsFragment : Fragment() {
    private lateinit var gridView: GridView
    private lateinit var list: List<GridViewModel>
    private lateinit var gridViewAdapter: GridAdapter

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
//        gridview stuff
        gridView = requireView().findViewById(R.id.grid_view)
        list = ArrayList() // get from db eventually
        list = list + GridViewModel(1, R.drawable.duck, "Duck 1 | $69")
        list = list + GridViewModel(2, R.drawable.duck, "Duck 2 | $4.20")
        list = list + GridViewModel(3, R.drawable.duck, "Duck 3 | $6.90")
        list = list + GridViewModel(4, R.drawable.duck, "Duck 4 | $0.01")
        list = list + GridViewModel(5, R.drawable.duck, "Duck 5 | FREE")

        gridViewAdapter = GridAdapter(list, requireActivity())
        gridView.adapter = gridViewAdapter

        // click listener for our grid view.
        gridView.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            println("DEBUG: We will open the detail view now for ${list[position].name}")
            val intent = Intent(requireActivity(),DetailActivity::class.java)
            intent.putExtra("id",list[position].id)
            startActivity(intent)
        }

    }
}