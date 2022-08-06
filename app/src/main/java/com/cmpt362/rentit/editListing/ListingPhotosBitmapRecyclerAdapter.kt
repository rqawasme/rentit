package com.cmpt362.rentit.editListing

import android.graphics.Bitmap
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.cmpt362.rentit.R
import com.cmpt362.rentit.createListing.ListingPhotosRecyclerAdapter

class ListingPhotosBitmapRecyclerAdapter(private val bitmapArrayList: ArrayList<Bitmap>): RecyclerView.Adapter<ListingPhotosBitmapRecyclerAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListingPhotosBitmapRecyclerAdapter.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.custom_single_image, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListingPhotosBitmapRecyclerAdapter.ViewHolder, position: Int) {
        holder.singleImageView.setImageBitmap(bitmapArrayList[position])
    }

    override fun getItemCount(): Int {
        return bitmapArrayList.size
    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        var singleImageView: ImageView = itemView.findViewById(R.id.customSingleImage_imageView)

    }

}