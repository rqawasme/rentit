package com.cmpt362.rentit.ui.rentals

import com.cmpt362.rentit.db.Listing

data class GridViewModel(
    val id: Long,
    val listing: Listing
)