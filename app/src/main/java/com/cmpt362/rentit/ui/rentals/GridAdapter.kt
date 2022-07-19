package com.cmpt362.rentit.ui.rentals


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.cmpt362.rentit.R

internal class GridAdapter(
    private val list: List<GridViewModel>, // get from db
    private val context: Context
) :
    BaseAdapter() {
    private var layoutInflater: LayoutInflater? = null
    private lateinit var textView: TextView
    private lateinit var imageView: ImageView

    override fun getCount(): Int {
        return list.size
    }

    override fun getItem(position: Int): Any {
        return list[position]
    }

    override fun getItemId(position: Int): Long {
        return list[position].id // should be using db stuff so we return that here
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
        var convertView = convertView
        if (layoutInflater == null) {
            layoutInflater =
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        }
        if (convertView == null) {
            convertView = layoutInflater!!.inflate(R.layout.gridview_item, null)
        }
        imageView = convertView!!.findViewById(R.id.gridViewImageView)
        textView = convertView!!.findViewById(R.id.gridViewTextView)
        imageView.setImageResource(list[position].image)
        textView.setText(list[position].listing.name)
        return convertView
    }
}