package com.cmpt362.rentit.ui.rentals


import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.cmpt362.rentit.Constants
import com.cmpt362.rentit.R
import com.cmpt362.rentit.Utils.getImage

internal class GridAdapter(
    private val list: List<GridViewModel>, // get from db
    private val context: Context
) :
    BaseAdapter() {
    private var layoutInflater: LayoutInflater? = null
    private lateinit var nameTextView: TextView
    private lateinit var priceTextView: TextView
    private lateinit var availabilityTextView: TextView
    private lateinit var distanceTextView: TextView
    private lateinit var snippetTextView: TextView
    private lateinit var imageView: ImageView

    override fun getCount(): Int {
        return list.size
    }

    override fun getItem(position: Int): Any {
        return list[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()// should be using db stuff so we return that here
    }

    @SuppressLint("SetTextI18n", "InflateParams")
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
        nameTextView = convertView.findViewById(R.id.gridViewNameTextView)
        priceTextView = convertView.findViewById(R.id.gridViewPriceTextView)
        availabilityTextView = convertView.findViewById(R.id.gridViewAvailabilityTextView)
        distanceTextView = convertView.findViewById(R.id.gridViewDistanceTextView)
        snippetTextView = convertView.findViewById(R.id.gridViewSnippetTextView)
        getImage(list[position].id, imageView)
        nameTextView.text = list[position].listing.name
        val availability = if (list[position].listing.available) "Available" else "Unavailable"
        val howFar = if (list[position].distance > 25000F) ">25000" else  java.text.DecimalFormat("#,##0.00").format(list[position].distance)
        priceTextView.text = "${Constants.DOLLAR_SIGN}${java.text.DecimalFormat("#,##0.00").format(list[position].listing.price)}"
        availabilityTextView.text = availability
        distanceTextView.text = howFar
        snippetTextView.text = list[position].listing.description
        return convertView
    }
}