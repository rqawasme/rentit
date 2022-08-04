package com.cmpt362.rentit.createListing

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cmpt362.rentit.Constants
import com.cmpt362.rentit.R
import com.cmpt362.rentit.Utils
import java.io.File

class CreateListingActivity : AppCompatActivity() {

    private val READ_PERMISSION = 101

    private lateinit var recyclerViewPhotos: RecyclerView
    private lateinit var buttonAddPhotos: Button
    private lateinit var uriArrayList: ArrayList<Uri>
    private lateinit var recyclerAdapter: CreateListingPhotosRecyclerAdapter
    private lateinit var galleryResult: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_listing)
        Utils.checkPermissions(this)
        initializeElements()

        buttonAddPhotos.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            intent.type = "image/*"
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            galleryResult.launch(intent)
        }
    }

    private fun initializeElements() {
        recyclerViewPhotos = findViewById(R.id.createListingActivity_recyclerView_photos)
        buttonAddPhotos = findViewById(R.id.createListingActivity_button_add_photos)
        uriArrayList = ArrayList()
        recyclerAdapter = CreateListingPhotosRecyclerAdapter(uriArrayList)
        recyclerViewPhotos.layoutManager = GridLayoutManager(this, Constants.PHOTOS_PER_ROW)
        recyclerViewPhotos.adapter = recyclerAdapter

        galleryResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult())
        {
                result: ActivityResult ->

            if (result.resultCode == Activity.RESULT_OK){

                if (result.data!!.clipData != null){
                    val count = result.data!!.clipData!!.itemCount

                    for (i in 0 .. count - 1){
                        val imageUri = result.data!!.clipData!!.getItemAt(i).uri
                        uriArrayList.add(imageUri)
                    }
                    recyclerAdapter.notifyDataSetChanged()
                }
                else{
                    //Selected single image
                    val imageURI = result.data?.data!!
                    uriArrayList.add(imageURI)
                    recyclerAdapter.notifyDataSetChanged()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1 && requestCode == Activity.RESULT_OK){
            if (data!!.clipData != null){
                val x = data.clipData!!.itemCount

                for (i in 0 .. x){
                    uriArrayList.add(data.clipData!!.getItemAt(i).uri)
                }
                recyclerAdapter.notifyDataSetChanged()
            }
            else if (data.data != null){
                val imageURL = data.data!!.path
                uriArrayList.add(Uri.parse(imageURL))
            }

        }
    }

}