package com.cmpt362.rentit

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Build
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.cmpt362.rentit.Constants.ONE_MEGABYTE
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

object Utils {
    fun getImage(listingId: Long, imageView: ImageView){
        val reference = Firebase.storage.reference
        Thread(){
            reference.child("listings/$listingId/").list(1).addOnSuccessListener {
                if (it.items.size > 0) {
                    it.items[0].getBytes(ONE_MEGABYTE*50).addOnSuccessListener { pic ->
                        imageView.setImageBitmap(byteArrToBitMap(pic))
                    }
                }
                else{
                    imageView.setImageResource(R.drawable.spinner)
                }
            }
        }.start()
    }

    fun byteArrToBitMap(byteArr:ByteArray): Bitmap {
        return BitmapFactory.decodeByteArray(byteArr, 0, byteArr.size)
    }

    fun getDescriptionSnippet(listingId: Long, textView: TextView){
        Thread(){
            val myRefListings=Firebase.database.getReference("Listings").child(listingId.toString())
            textView.movementMethod = ScrollingMovementMethod()

            myRefListings.get().addOnCompleteListener{ task->
                if(task.isSuccessful){
                    textView.text=task.result.child("description").getValue(String::class.java)
                }
                else{
                    Log.d("TAG", task.exception!!.message!!)
                }
            }
        }.start()
    }

    fun checkPermissions(activity: Activity?) {
        if (Build.VERSION.SDK_INT < 23) return
        if (ContextCompat.checkSelfPermission(activity!!, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
            || ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                activity, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA),
                0)
        }
    }

    fun getBitmap(context: Context, imgUri: Uri): Bitmap {
        var bitmap = BitmapFactory.decodeStream(context.contentResolver.openInputStream(imgUri))
        val matrix = Matrix()
        matrix.setRotate(90f)
        var ret = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        return ret
    }
}