package com.cmpt362.rentit.db

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Booking(
    val id:String?= null,
    val startTime: String? = null, //Note: Saved in format HH:mm MMMM dd yyyy
    val endTime: String? = null,
    val listingID: String?= null,
    val bookerID:String? = null ) {
    // Null default values create a no-argument default constructor, which is needed
    // for deserialization from a DataSnapshot.
}