package com.cmpt362.rentit.userListings

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.cmpt362.rentit.R
import com.cmpt362.rentit.db.Listing

class UserListingsListAdapter(private val context: Activity, private val listingArrayList: ArrayList<Listing>): BaseAdapter() {
    private var layoutInflater: LayoutInflater? = null
    private lateinit var imageViewListingImage: ImageView
    private lateinit var textViewListingTitle: TextView
    private lateinit var textViewListingPrice: TextView

    override fun getCount(): Int {
        return listingArrayList.size
    }

    override fun getItem(position: Int): Any {
        return listingArrayList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }


    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            var convertView = convertView
            if (layoutInflater == null) {
                layoutInflater =
                    context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            }
            if (convertView == null) {
                convertView = layoutInflater!!.inflate(R.layout.user_listing_item, null)
            }
            imageViewListingImage = convertView!!.findViewById(R.id.userListing_imageView_listingImage)
            textViewListingTitle = convertView.findViewById(R.id.userListings_textView_listingTitle)
            textViewListingPrice = convertView.findViewById(R.id.userListings_textView_listingPrice)


            textViewListingTitle.text = listingArrayList[position].name
            textViewListingPrice.text = listingArrayList[position].price.toString()


        return convertView
    }

}