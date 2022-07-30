package com.cmpt362.rentit.ui.rentals


import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.cmpt362.rentit.R
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

internal class GridAdapter(
    private val list: List<GridViewModel>, // get from db
    private val context: Context
) :
    BaseAdapter() {
    private var layoutInflater: LayoutInflater? = null
    private lateinit var textView: TextView
    private lateinit var imageView: ImageView
    val ONE_MEGABYTE: Long = 1024 * 1024

    override fun getCount(): Int {
        return list.size
    }

    override fun getItem(position: Int): Any {
        return list[position]
    }

    override fun getItemId(position: Int): Long {
        return list[position].id // should be using db stuff so we return that here
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var convertView = convertView
        if (layoutInflater == null) {
            layoutInflater =
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        }
        if (convertView == null) {
            convertView = layoutInflater!!.inflate(R.layout.gridview_item, null)
        }
        imageView = convertView!!.findViewById(R.id.gridViewImageView)
        textView = convertView.findViewById(R.id.gridViewTextView)
        getImage(list[position].id, imageView)
        textView.text = list[position].listing.name
        return convertView
    }

    private fun getImage(listingId: Long, imageView: ImageView){
        val reference = Firebase.storage.reference
        Thread(){
            reference.child("listings/$listingId/").list(1).addOnSuccessListener {
                if (it.items.size > 0) {
                    it.items[0].getBytes(ONE_MEGABYTE*50).addOnSuccessListener {pic ->
                        imageView.setImageBitmap(byteArrToBitMap(pic))
                    }
                }
                else{
                    imageView.setImageResource(R.drawable.spinner)
                }
            }
        }.start()
    }

    private fun byteArrToBitMap(byteArr:ByteArray): Bitmap {
        return BitmapFactory.decodeByteArray(byteArr, 0, byteArr.size)
    }

}