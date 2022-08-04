package com.cmpt362.rentit.details

import android.content.Intent
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
import com.cmpt362.rentit.Constants
import com.cmpt362.rentit.Constants.ONE_MEGABYTE
import com.cmpt362.rentit.R
import com.cmpt362.rentit.details.booking.BookingActivity
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class DetailActivity:AppCompatActivity() {
    private lateinit var db: FirebaseDatabase
    private var name:String?=""
    private var listingID:String = ""
    private var posterID:String? = null
    private var price:Float? =0f
    private var description:String? =""
    private lateinit var viewPager: ViewPager
    private var viewPagerAdapter: PagerAdapter? =null

    private var phoneNumber:String? =""
    private var email:String? =""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)

        //Todo: Will most likely need to change listingID to String when we use pushIDs for listings instead
        listingID= intent.getStringExtra("id").toString()
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
            reference.child("${Constants.LISTINGS_PATH}/" + listingID + "/").listAll().addOnSuccessListener {
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
    private fun readThread(listingID:String){
        Thread(){
            val myRefListings=db.getReference(Constants.LISTINGS_TABLE_NAME).child(listingID)

            myRefListings.get().addOnCompleteListener{ task->
                if(task.isSuccessful){
                    val snapshot= task.result
                    name=snapshot.child("name").getValue(String::class.java)
                    price=snapshot.child("price").getValue(Float::class.java)
                    description=snapshot.child("description").getValue(String::class.java)
                    posterID=snapshot.child("postUserID").getValue(String::class.java)


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

    private fun getContactInfo(userID:String){
        Thread(){
            val myRefListings=db.getReference("Users").child(userID)
            myRefListings.get().addOnCompleteListener { task ->
                if(task.isSuccessful){
                    val snapshot= task.result
                    phoneNumber=snapshot.child("phone").getValue(String::class.java)
                    email=snapshot.child("email").getValue(String::class.java)
                }

            }

            //Leave for now as reference on how to query Firebase with value.
//            val myRefListings=db.getReference("Users").orderByChild("id").equalTo(userID.toDouble()).limitToFirst(1)
//
//            //Maybe a more smarter way to do this.
//            myRefListings.get().addOnCompleteListener{ task->
//                val snapshot= task.result.children
//                for (i in snapshot){
//                    phoneNumber= i.child("phone").getValue(String::class.java)
//                    email= i.child("email").getValue(String::class.java)
//                }
//            }
        }.start()

    }

    fun book(view: View){
        println("DEBUG: Book activity open")
        val intent = Intent(this, BookingActivity::class.java)
        intent.putExtra("listingID",listingID)
        intent.putExtra("price",price)
        intent.putExtra("listingName",name)
        startActivity(intent)
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