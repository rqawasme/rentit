package com.cmpt362.rentit.ui.rentals

import com.cmpt362.rentit.db.Listing

data class GridViewModel(
    val id: String,
    val listing: Listing,
    val distance: Float,
)