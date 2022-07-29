package com.cmpt362.rentit.details

import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.View
import android.widget.Adapter
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.cmpt362.rentit.R
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class DetailActivity:AppCompatActivity() {
    private lateinit var db: FirebaseDatabase
    private var name:String?=""
    private var listingID:Long =-1
    private var posterID:Int? =-1
    private var price:Float? =0f
    private var description:String? =""
    private lateinit var viewPager: ViewPager
    private var viewPagerAdapter: PagerAdapter? =null

    private var phoneNumber:String? =""
    private var email:String? =""

    val ONE_MEGABYTE: Long = 1024 * 1024

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)

        listingID= intent.getLongExtra("id",-1)
        var imageList = ArrayList<ByteArray>()
        db= Firebase.database

        readThread(listingID)
        loadImages(imageList)
    }

    //Load images from Firebase to ViewPager
    private fun loadImages(imageList: ArrayList<ByteArray>){
        var reference = Firebase.storage.reference
        viewPager = findViewById(R.id.detail_viewpager)
        Thread(){
            reference.child("listings/" + listingID + "/").listAll().addOnSuccessListener {
                for(entry in it.items){
                    entry.getBytes(ONE_MEGABYTE*50).addOnSuccessListener {
                        imageList.add(it)
                        viewPagerAdapter = DetailViewPagerAdapter(this, imageList)
                        viewPager.adapter=viewPagerAdapter
                    }
                }
            }
        }.start()
    }

    //Read info from Firebase
    private fun readThread(listingID:Long){
        Thread(){
            val myRefListings=db.getReference("Listings").child(listingID.toString())

            myRefListings.get().addOnCompleteListener{ task->
                if(task.isSuccessful){
                    val snapshot= task.result
                    name=snapshot.child("name").getValue(String::class.java)
                    price=snapshot.child("price").getValue(Float::class.java)
                    description=snapshot.child("description").getValue(String::class.java)
                    posterID=snapshot.child("postUserID").getValue(Int::class.java)

                    val nameTextView=findViewById<TextView>(R.id.details_name)
                    val priceTextView=findViewById<TextView>(R.id.details_price)
                    val descriptionTextView=findViewById<TextView>(R.id.details_description)

                    descriptionTextView.movementMethod= ScrollingMovementMethod()

                    nameTextView.text = name
                    priceTextView.text= "$" + String.format("%.2f",price)
                    descriptionTextView.text=description

                    //Get contact info from Firebase, after we get the posterID.
                    getContactInfo(posterID!!)
                }
                else{
                    Log.d("TAG", task.exception!!.message!!)
                }
            }
        }.start()
    }

    private fun getContactInfo(userID:Int){
        Thread(){
            val myRefListings=db.getReference("Users").orderByChild("id").equalTo(userID.toDouble()).limitToFirst(1)

            //Maybe a more smarter way to do this.
            myRefListings.get().addOnCompleteListener{ task->
                val snapshot= task.result.children
                for (i in snapshot){
                    phoneNumber= i.child("phone").getValue(String::class.java)
                    email= i.child("email").getValue(String::class.java)
                }
            }
        }.start()

    }

    fun book(view: View){
        println("DEBUG: Book dialog open")
    }

    //Open a dialog with contact info of user posting.
    fun contact(view: View){
        if(phoneNumber==""){
            val duration = Toast.LENGTH_SHORT
            val toast = Toast.makeText(applicationContext, "Fetching information from Firebase, please wait!", duration)
            toast.show()
        }
        else{
            DialogContact(email!!,phoneNumber!!).show(supportFragmentManager,DialogContact.TAG)
        }
        println("DEBUG: Contact dialog open")
    }

}