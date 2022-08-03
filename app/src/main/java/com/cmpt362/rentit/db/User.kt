package com.cmpt362.rentit.db

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class User(
    val id: String? = null,
    val email: String? = null,
    val username: String? = null,
    val phone: String? = null,
    val postalCode: String? = null) {
    // Null default values create a no-argument default constructor, which is needed
    // for deserialization from a DataSnapshot.
}