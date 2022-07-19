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

        val listingID= intent.getLongExtra("id",-1)

        readThread(listingID)

        //Todo: Create viewmodel for image, eventually get the image from Firebase, and change it from one picture to a "slideshow"
        val imageView=findViewById<ImageView>(R.id.details_image)
        imageView.setImageResource(R.drawable.duck)
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