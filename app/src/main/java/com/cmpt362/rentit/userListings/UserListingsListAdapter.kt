package com.cmpt362.rentit.userListings

import android.app.Activity
import android.content.Context
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.cmpt362.rentit.Constants
import com.cmpt362.rentit.R
import com.cmpt362.rentit.Utils
import com.cmpt362.rentit.db.Listing
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import java.io.File

class UserListingsListAdapter(private val context: Activity, private val listingArrayList: ArrayList<Listing>): BaseAdapter() {
    private var layoutInflater: LayoutInflater? = null
    private lateinit var imageViewListingImage: ImageView
    private lateinit var textViewListingTitle: TextView
    private lateinit var textViewListingPrice: TextView
    private lateinit var textViewAvailability: TextView
    private lateinit var buttonEditListing: Button
    private lateinit var buttonAvailability: Button

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
        textViewAvailability = convertView.findViewById(R.id.userListings_textView_listingAvailability)
        buttonEditListing = convertView.findViewById(R.id.userListings_button_editListing)
        buttonAvailability = convertView.findViewById(R.id.userListings_button_availability)

        textViewListingTitle.text = listingArrayList[position].name
        textViewListingPrice.text = listingArrayList[position].price.toString()

        if (!listingArrayList[position].available){
           markUnavailable(convertView)
        }
        else{
            markAvailable(convertView)
        }

        buttonAvailability.setOnClickListener {
            if (textViewAvailability.text == Constants.AVAILABLE_TEXT){
                markUnavailable(convertView)
            }
            else if (textViewAvailability.text == Constants.UNAVAILABLE_TEXT){
                markUnavailable(convertView)
            }
        }

        Utils.getImage(listingArrayList[position].listingID!!, imageViewListingImage)

        return convertView
    }

    fun markAvailable(view: View){
        textViewAvailability.text = Constants.AVAILABLE_TEXT
        buttonAvailability.text = Constants.MARK_UNAVAILABLE_TEXT
        val drawableLeft = view.resources.getDrawable(R.drawable.ic_baseline_not_interested_24)
        buttonAvailability.setCompoundDrawables(drawableLeft, null, null, null)
    }

    fun markUnavailable(view: View){
        textViewAvailability.text = Constants.UNAVAILABLE_TEXT
        buttonAvailability.text = Constants.MARK_AVAILABLE_TEXT
        val drawableLeft = view.resources.getDrawable(R.drawable.ic_baseline_event_available_24)
        buttonAvailability.setCompoundDrawables(drawableLeft, null, null, null)
    }

}