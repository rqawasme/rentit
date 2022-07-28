package com.cmpt362.rentit.details

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.cmpt362.rentit.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class DetailActivity:AppCompatActivity() {
    private lateinit var db: FirebaseDatabase
    private var name:String?=""
    private var price:Float? =0f
    private var description:String? =""
    lateinit var viewPager: ViewPager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)

        var reference = Firebase.storage.reference


        viewPager = findViewById(R.id.detail_viewpager)


        val listingID= intent.getLongExtra("id",-1)


        var imageList = ArrayList<Int>()
        imageList.add(R.drawable.book)
        imageList.add(R.drawable.duck)
        imageList.add(R.drawable.car)
        imageList.add(R.drawable.guitar)

        var viewPagerAdapter = DetailViewPagerAdapter(this, imageList)

        viewPager.adapter=viewPagerAdapter

        readThread(listingID)


        val ONE_MEGABYTE: Long = 1024 * 1024
        reference.child("listings/1/car.jpg").getBytes(ONE_MEGABYTE).addOnSuccessListener {
            Glide.with(this)  //this is the current Activity
                .load(it)
                //.into(imageView);
        }.addOnFailureListener {
            // Handle any errors
            throw it
        }




        //imageView.setImageResource(R.drawable.duck)
    }

    //Read info from Firebase
    private fun readThread(listingID:Long){
        Thread(){
            db= Firebase.database
            val myRefListings=db.getReference("Listings").child(listingID.toString())

            myRefListings.get().addOnCompleteListener{ task->
                if(task.isSuccessful){
                    val snapshot= task.result
                    name=snapshot.child("name").getValue(String::class.java)
                    price=snapshot.child("price").getValue(Float::class.java)
                    description=snapshot.child("description").getValue(String::class.java)

                    val nameTextView=findViewById<TextView>(R.id.details_name)
                    val priceTextView=findViewById<TextView>(R.id.details_price)
                    val descriptionTextView=findViewById<TextView>(R.id.details_description)

                    nameTextView.text = name
                    priceTextView.text= "$" + String.format("%.2f",price)
                    descriptionTextView.text=description
                }
                else{
                    Log.d("TAG", task.exception!!.message!!)
                }
            }
        }.start()
    }

    fun book(view: View){
        println("DEBUG: Book dialog open")
    }

    fun contact(view: View){
        println("DEBUG: Contact dialog open")
    }

}