package com.cmpt362.rentit.ui.bookingList

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.media.Image
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.cmpt362.rentit.R
import com.cmpt362.rentit.Utils
import com.cmpt362.rentit.db.Booking
import com.cmpt362.rentit.db.Listing
import com.cmpt362.rentit.details.DetailActivity
import com.cmpt362.rentit.details.DialogContact
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class BookingListAdapter(private val context: Activity, private val bookingArrayList: ArrayList<Pair<Booking,String>>): BaseAdapter() {
    private var layoutInflater: LayoutInflater? = null
    lateinit var titleView: TextView

    override fun getCount(): Int {
        return bookingArrayList.size
    }

    override fun getItem(position: Int): Any {
        return bookingArrayList[position]
    }

    override fun getItemId(position: Int): Long {
        return 1.toLong()
    }


    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var convertView=convertView

        if (layoutInflater == null) {
            layoutInflater =
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        }

        if (convertView == null) {
            convertView = layoutInflater!!.inflate(R.layout.user_booking, null)
        }

        //Get ListingID, then get listing name
        var timePeriod= convertView!!.findViewById<TextView>(R.id.user_booking_time)
        timePeriod.setText(bookingArrayList[position].first.startTime + " to " + bookingArrayList[position].first.endTime)

        titleView= convertView!!.findViewById<TextView>(R.id.user_booking_title)
        titleView.setText(bookingArrayList[position].second)

        //Add onclickListener to buttons
        var seeListingButton= convertView!!.findViewById<Button>(R.id.user_booking_see_listing)
        seeListingButton.setOnClickListener{
            val intent = Intent(convertView.context, DetailActivity::class.java)
            intent.putExtra("id",bookingArrayList[position].first.listingID)
            context.startActivity(intent)
        }

        var activeIndicator=convertView!!.findViewById<TextView>(R.id.booking_active)

        //Check if active
        var bookingStartMilli= SimpleDateFormat("HH:mm MMMM dd yyyy").parse(bookingArrayList[position].first.startTime ).time
        var bookingEndMilli= SimpleDateFormat("HH:mm MMMM dd yyyy").parse(bookingArrayList[position].first.endTime).time

        var currentMilli= Date().time

        if((currentMilli<=bookingEndMilli) && (currentMilli>=bookingStartMilli)){
            activeIndicator.setText("Active")
            activeIndicator.setBackgroundResource(R.drawable.green_rounded_background)
        }
        else{
            activeIndicator.setText("Inactive")
            activeIndicator.setBackgroundResource(R.drawable.red_rounded_background)
        }

        return convertView
    }
}