package com.cmpt362.rentit

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.location.Criteria
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.cmpt362.rentit.Constants.ONE_MEGABYTE
import com.cmpt362.rentit.db.Listing
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.imageview.ShapeableImageView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import java.io.File

object Utils {
    fun getImage(listingId: String, imageView: ImageView){
        val reference = Firebase.storage.reference
        Thread(){
            reference.child("${Constants.LISTINGS_PATH}/$listingId/").list(1).addOnSuccessListener {
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

    fun getDescriptionSnippet(listingId: String, textView: TextView){
        Thread(){
            val myRefListings=Firebase.database.getReference(Constants.LISTINGS_TABLE_NAME).child(listingId.toString())
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
        matrix.setRotate(0f)
        var ret = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        return ret
    }

    fun getCurrentLocation(activity: Activity, context: Context): Location {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 0)
        }
        val locationManager = activity.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val criteria = Criteria()
        criteria.accuracy = Criteria.ACCURACY_FINE
        val provider = locationManager.getBestProvider(criteria, true)
        return locationManager.getLastKnownLocation(provider!!)!!
    }

    fun displayUserProfilePicture(context: Context, imageViewProfilePicture: ShapeableImageView) {
        val firebaseAuth = FirebaseAuth.getInstance()
        val user = firebaseAuth.currentUser
        val storageReference = FirebaseStorage.getInstance().reference.child(Constants.USERS_FOLDER + user!!.uid)

        val localFile = File.createTempFile(Constants.USER_PROFILE_PIC_PREFIX, Constants.USER_PROFILE_PIC_SUFFIX)
        storageReference.getFile(localFile).addOnSuccessListener{
            val bitmap = BitmapFactory.decodeFile(localFile.absolutePath)
            imageViewProfilePicture.setImageBitmap(bitmap)
        }.addOnFailureListener{
            Toast.makeText(context, Constants.FAILED_TO_RETRIEVE_PROFILE_PICTURE_ERROR, Toast.LENGTH_SHORT).show()
        }
    }

}