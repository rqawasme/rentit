package com.cmpt362.rentit.userListings

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.cmpt362.rentit.Constants
import com.cmpt362.rentit.R
import com.cmpt362.rentit.Utils
import com.cmpt362.rentit.createListing.CreateListingActivity
import com.cmpt362.rentit.db.Listing
import com.cmpt362.rentit.editListing.EditListingActivity
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage

class UserListingsListAdapter(private val context: Activity, private val listingArrayList: ArrayList<Listing>): BaseAdapter() {
    private var layoutInflater: LayoutInflater? = null
    private lateinit var imageViewListingImage: ImageView
    private lateinit var textViewListingTitle: TextView
    private lateinit var textViewListingPrice: TextView
    private lateinit var textViewAvailability: TextView
    private lateinit var buttonEditListing: Button
    private lateinit var buttonAvailability: Button
    private lateinit var buttonDelete: Button

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
        buttonDelete = convertView.findViewById(R.id.userListings_button_delete)

        textViewListingTitle.text = listingArrayList[position].name
        textViewListingPrice.text = Constants.DOLLAR_SIGN + listingArrayList[position].price.toString()

        if (!listingArrayList[position].available){
           markUnavailable(convertView)
        }
        else{
            markAvailable(convertView)
        }

        buttonAvailability.setOnClickListener {
            if (listingArrayList[position].available){
                listingArrayList[position].available = false
                changeListingAvailability(position, false)
                this.notifyDataSetChanged()
            }
            else {
                listingArrayList[position].available = true
                changeListingAvailability(position, true)
                this.notifyDataSetChanged()
            }
        }

        buttonEditListing.setOnClickListener {
            val intent = Intent(context, EditListingActivity::class.java)
            intent.putExtra(Constants.EDIT_LISTING_EXTRA_STRING, listingArrayList[position].listingID)
            context.startActivity(intent)
        }

        buttonDelete.setOnClickListener {

            deleteListing(position)

        }

        Utils.getImage(listingArrayList[position].listingID!!, imageViewListingImage)

        return convertView
    }

    private fun deleteListing(position: Int) {
        var db = Firebase.database
        val listingReference = db.getReference(Constants.LISTINGS_TABLE_NAME)
        val listingToDelete = listingArrayList[position].listingID!!
        listingReference.child(listingToDelete).removeValue().addOnCompleteListener {
            if (it.isSuccessful) {
                Toast.makeText(context, "Listing Deleted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Failed to delete listing", Toast.LENGTH_SHORT).show()
            }
        }
        val storageReference = Firebase.storage.reference

        storageReference.child("${Constants. LISTINGS_PATH}/${listingToDelete}").listAll().addOnSuccessListener {
            for (images in it.items){
                images.delete()
            }
        }.addOnFailureListener {
            println(it)
        }

        var newListingArrayList: ArrayList<Listing> = listingArrayList

        val listIterator = listingArrayList.listIterator()

        while (listIterator.hasNext()){
            if (listIterator.next().listingID == listingToDelete){
                listIterator.remove()
            }
        }

        this.notifyDataSetChanged()
    }

    private fun markAvailable(view: View){
        textViewAvailability.text = Constants.AVAILABLE_TEXT
        buttonAvailability.text = Constants.MARK_UNAVAILABLE_TEXT
        val drawableLeft = view.resources.getDrawable(R.drawable.ic_baseline_not_interested_24)
        buttonAvailability.setCompoundDrawablesWithIntrinsicBounds(drawableLeft, null, null, null)
    }

    private fun markUnavailable(view: View){
        textViewAvailability.text = Constants.UNAVAILABLE_TEXT
        buttonAvailability.text = Constants.MARK_AVAILABLE_TEXT
        val drawableLeft = view.resources.getDrawable(R.drawable.ic_baseline_event_available_24)
        buttonAvailability.setCompoundDrawablesWithIntrinsicBounds(drawableLeft, null, null, null)
    }

    private fun changeListingAvailability(position: Int, availability: Boolean){
        var db = Firebase.database
        val listingReference = db.getReference(Constants.LISTINGS_TABLE_NAME)
        val listing = mapOf<String, Any>(
            "available" to availability,
        )
        listingReference.child(listingArrayList[position].listingID!!).updateChildren(listing).addOnSuccessListener {
            Toast.makeText(context, "Availability updated", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener{
            println(it)
        }
    }

}