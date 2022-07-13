package com.cmpt362.rentit.db

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Listing(
    val id:Int= -1,
    val type: String? = null,
    val name: String? = null,
    val price: Double? = null,
    val description: String? = null,
    val postUserID: Int = -1,
    val renterUserID: Int = -1,
    val available:  Boolean = false) {
    // Null default values create a no-argument default constructor, which is needed
    // for deserialization from a DataSnapshot.
}