package com.cmpt362.rentit.createListing

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.view.menu.MenuView
import androidx.recyclerview.widget.RecyclerView
import com.cmpt362.rentit.R

class CreateListingPhotosRecyclerAdapter(private val uriArrayList: ArrayList<Uri>): RecyclerView.Adapter<CreateListingPhotosRecyclerAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CreateListingPhotosRecyclerAdapter.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.custom_single_image, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: CreateListingPhotosRecyclerAdapter.ViewHolder, position: Int) {
        holder.singleImageView.setImageURI(uriArrayList[position])
    }

    override fun getItemCount(): Int {
        return uriArrayList.size
    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        var singleImageView: ImageView = itemView.findViewById(R.id.customSingleImage_imageView)

    }

}