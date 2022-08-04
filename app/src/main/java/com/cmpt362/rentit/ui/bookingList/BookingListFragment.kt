package com.cmpt362.rentit.ui.bookingList

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.cmpt362.rentit.R
import com.cmpt362.rentit.db.Booking
import com.cmpt362.rentit.db.Listing
import com.cmpt362.rentit.details.booking.ConfirmationFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat

class BookingListFragment : Fragment() {
    var userUID=""
    lateinit var bookingsListView:ListView
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view: View = inflater.inflate(R.layout.fragment_booking_list, container, false)

        //Get current user.
        var firebaseAuth = FirebaseAuth.getInstance()
        val firebaseUser = firebaseAuth.currentUser
        if (firebaseUser != null) {
            userUID=firebaseUser.uid
        }

        bookingsListView= view.findViewById<ListView>(R.id.user_booking_listView)
        getBookings(userUID)



        return view
    }

    fun getBookings(userID:String){
        val myRefBookings= Firebase.database.getReference("Bookings").orderByChild("bookerID").equalTo(userID)
        var bookingList= ArrayList<Pair<Booking,String>>()

        myRefBookings.get().addOnCompleteListener{ task->
            val snapshot=task.result.children
            for (i in snapshot){
                var bookingID= i.child("id").getValue(String::class.java)
                var bookingStart= i.child("startTime").getValue(String::class.java)
                var bookingEnd= i.child("endTime").getValue(String::class.java)
                var listingID= i.child("listingID").getValue(String::class.java)


                var booking= Booking(bookingID,bookingStart,bookingEnd,listingID)

                val myRefListings= Firebase.database.getReference("Listings").child(listingID!!)

                myRefListings.get().addOnCompleteListener { task ->
                    if(task.isSuccessful){
                        val snapshot= task.result
                        var title=snapshot.child("name").getValue(String::class.java)
                        bookingList.add(Pair(booking,title) as Pair<Booking, String>)
                    }
                    var bookingsAdapter= BookingListAdapter(requireActivity(), bookingList)
                    bookingsListView.adapter=bookingsAdapter
                }
            }
        }
    }

}