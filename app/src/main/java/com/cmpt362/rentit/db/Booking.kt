package com.cmpt362.rentit.db

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Booking(
    val id:Int= -1,
    val startTime: String? = null, //Note: Time stored in format yyyy-MM-dd'T'HH:mm:ss.SSS'Z' (ISO 9601 format)
    val endTime: String? = null,
    val listingID: Int=-1,
    val bookerName:String? = null ) {
    // Null default values create a no-argument default constructor, which is needed
    // for deserialization from a DataSnapshot.
}