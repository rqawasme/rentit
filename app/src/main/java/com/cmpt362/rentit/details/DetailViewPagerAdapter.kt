package com.cmpt362.rentit.details

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.viewpager.widget.PagerAdapter
import com.cmpt362.rentit.R
import java.util.*

class DetailViewPagerAdapter(val context: Context, val imageList: List<ByteArray>): PagerAdapter(){
    override fun getCount(): Int {
        return imageList.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val mLayoutInflater =  context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val itemView: View = mLayoutInflater.inflate(R.layout.details_image, container, false)
        val imageView: ImageView = itemView.findViewById<View>(R.id.details_imageView) as ImageView

        imageView.setImageBitmap(byteArrToBitMap(imageList.get(position)))
        Objects.requireNonNull(container).addView(itemView)
        return itemView
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        // on below line we are removing view
        container.removeView(`object` as LinearLayout)
    }

    fun byteArrToBitMap(byteArr:ByteArray): Bitmap {
        return BitmapFactory.decodeByteArray(byteArr, 0, byteArr.size)
    }
}