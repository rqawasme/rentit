package com.cmpt362.rentit

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class DetailActivity:AppCompatActivity() {
    private lateinit var db: FirebaseDatabase
    private var name:String?=""
    private var price:Float? =0f
    private var description:String? =""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)

        //Todo: Get the id for the firebase entry from RentalsFragment, for now it just defaults to 1
        //val listingID= intent.getLongExtra("id",-1)
        val listingID=1

        //Todo: Move into thread
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
                priceTextView.text= price.toString()
                descriptionTextView.text=description
            }
            else{
                Log.d("TAG", task.exception!!.message!!)
            }
        }

        //Todo: Create viewmodel for image, eventually get the image from Firebase
        val imageView=findViewById<ImageView>(R.id.details_image)
        imageView.setImageResource(R.drawable.duck)
    }

    fun book(view: View){
        println("Book")
    }

    fun contact(view: View){
        println("Contact")
    }

}