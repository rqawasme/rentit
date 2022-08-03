package com.cmpt362.rentit.db

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Listing(
    val id: Long = -1,
    val pushId: String? = null,
    val type: String? = null,
    val name: String? = null,
    val price: Double? = null,
    val description: String? = null,
    val postUserID:  String? = null,
    val renterUserID: String? = null,
    val available:  Boolean = false) {
    // Null default values create a no-argument default constructor, which is needed
    // for deserialization from a DataSnapshot.
}