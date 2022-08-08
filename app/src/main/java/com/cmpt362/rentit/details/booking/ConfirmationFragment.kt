package com.cmpt362.rentit.details.booking

import android.app.AlertDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import android.text.InputType
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.cmpt362.rentit.Constants
import com.cmpt362.rentit.db.Booking
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.*

class ConfirmationFragment(val name:String?, val price:Float?, val startDate:String?, val endDate:String?,val listingID: String , val userUID:String?): DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        var formattedPrice= "$" + String.format("%.2f",price)

        return AlertDialog.Builder(requireContext())
            .setMessage("Booking $name for $formattedPrice per day from $startDate to $endDate, are you sure?")
            .setPositiveButton("CONFIRM") { _,_ ->
                if (startDate != null && endDate!=null) {
                    saveBooking(startDate, endDate )
                    activity?.finish()
                }
            }
            .setNegativeButton("CANCEL") { _,_ ->

            }
            .create()
    }
    companion object {
        const val TAG = "ConfirmationFragment"
    }

    //Push new booking to Firebase
    fun saveBooking(startString:String, endString:String){
        Thread(){
            val myRefBookings = Firebase.database.getReference(Constants.BOOKINGS_PATH)
            var newKey=myRefBookings.push().key

            val booking= Booking(newKey,startString,endString,listingID, name, userUID)

            if (newKey != null) {
                myRefBookings.child(newKey).setValue(booking)
            }
        }.start()
    }

}