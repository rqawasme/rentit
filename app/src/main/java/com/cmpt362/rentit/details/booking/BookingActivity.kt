package com.cmpt362.rentit.details.booking

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.cmpt362.rentit.Constants
import com.cmpt362.rentit.R
import com.cmpt362.rentit.db.Booking
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*

class BookingActivity: AppCompatActivity(), DialogInterface {
    private var listingID:String= ""
    private var name:String? = ""
    private var price:Float? = 0f

    var currentdate:String? = ""
    var currentTime: String? =""


    lateinit var buttonPickStartDate: Button
    lateinit var buttonPickStartTime: Button
    lateinit var buttonPickEndDate: Button
    lateinit var buttonPickEndTime: Button
    lateinit var textViewBookingStartDate: TextView
    lateinit var textViewBookingEndDate: TextView
    lateinit var textViewBookingStartTime: TextView
    lateinit var textViewBookingEndTime: TextView

    var startDate:String? = ""
    var endDate:String? = ""

    var startTime:String? = ""
    var endTime:String? = ""

    var userUID=""
    var db= Firebase.database

    var startString=""
    var endString=""

    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_booking)

        //Get current user.
        var firebaseAuth = FirebaseAuth.getInstance()
        val firebaseUser = firebaseAuth.currentUser
        if (firebaseUser != null) {
            userUID=firebaseUser.uid
        }

        //Todo: Will most likely need to change listingID to String when we use pushIDs for listings instead
        listingID= intent.getStringExtra("listingID").toString()
        name= intent.getStringExtra("listingName")
        price = intent.getFloatExtra("price",-1F)

        var listingNameTextView=findViewById<TextView>(R.id.booking_name)
        var priceTextView=findViewById<TextView>(R.id.bookingActivity_textView_listingPrice)
        listingNameTextView.text = name
        priceTextView.text = "$" + String.format("%.2f",price)

        //Set time/date on open to current time/date as default.
        val sdf = SimpleDateFormat("MMMM dd yyyy")
        val currentDate = sdf.format(Date())
        val currentTime=SimpleDateFormat("HH:mm").format(Date())

        startDate=currentDate
        endDate=currentDate

        startTime=currentTime
        endTime=currentTime

        textViewBookingStartDate = findViewById(R.id.bookingActivity_textView_booking_start_date)
        textViewBookingEndDate = findViewById(R.id.bookingActivity_textView_booking_end_date)
        textViewBookingStartTime = findViewById(R.id.bookingActivity_textView_booking_start_time)
        textViewBookingEndTime = findViewById(R.id.bookingActivity_textView_booking_end_time)
        buttonPickStartDate = findViewById(R.id.bookingActivity_button_pick_booking_startDate)
        buttonPickStartTime = findViewById(R.id.bookingActivity_button_pick_booking_startTime)
        buttonPickEndDate = findViewById(R.id.bookingActivity_button_pick_booking_endDate)
        buttonPickEndTime = findViewById(R.id.bookingActivity_button_pick_booking_endTime)

        textViewBookingStartDate.text = currentDate
        textViewBookingEndDate.text = currentDate
        textViewBookingStartTime.text = currentTime
        textViewBookingEndTime.text = currentTime

        super.onCreate(savedInstanceState)
    }

    //Open DateDialogFragment
    fun setDate(view:View){
        var id = view.id
        if(id == R.id.bookingActivity_button_pick_booking_startDate){
            DateDialogFragment("start").show(supportFragmentManager,DateDialogFragment.TAG)
        }
        else if(id == R.id.bookingActivity_button_pick_booking_endDate) {
            DateDialogFragment("end").show(supportFragmentManager,DateDialogFragment.TAG)
        }
    }

    //Open TimeDialog Fragment
    fun setTime(view:View){
        var id= view.id
        if(id==R.id.bookingActivity_button_pick_booking_startTime){
            TimeDialogFragment("start").show(supportFragmentManager,TimeDialogFragment.TAG)
        }
        else if(id== R.id.bookingActivity_button_pick_booking_endTime) {
            TimeDialogFragment("end").show(supportFragmentManager,TimeDialogFragment.TAG)
        }
    }

    //Confirm button
    fun confirm(view: View){
        startString= "$startTime $startDate"
        endString= "$endTime $endDate"

        //First convert into milliseconds representation for comparisons
        var startMilli=SimpleDateFormat(Constants.DATE_TIME_FORMAT).parse(startString).time
        var endMilli=SimpleDateFormat(Constants.DATE_TIME_FORMAT).parse(endString).time

        checkTimePeriod(startMilli,endMilli,listingID)
    }

    fun cancel(view: View){
        this.finish()
    }

    override fun saveDateDialog(year: Int, month: Int, day: Int, type:String) {
        var newDate= Date()
        newDate.year=year-1900 //Due to Date weirdness, need to subtract 1900.
        newDate.month=month
        newDate.date=day
        val sdf = SimpleDateFormat("MMMM dd yyyy")
        val date = sdf.format(newDate)

        if(type=="start"){
            textViewBookingStartDate.text = date
            startDate=date
        }
        else if(type=="end"){
            textViewBookingEndDate.text = date
            endDate=date
        }
    }

    override fun saveTimeDialog(hour: Int, min: Int, type:String) {
        var newTime= Date()
        newTime.hours=hour
        newTime.minutes=min
        val sdf = SimpleDateFormat("HH:mm")
        val time = sdf.format(newTime)

        if(type=="start"){
            textViewBookingStartTime.text = time
            startTime=time
        }
        else if(type=="end"){
            textViewBookingEndTime.text = time
            endTime=time
        }
    }

    //Checks if entered time period doesn't overlap any existing bookings.
    //If valid, opens Confirmation fragment
    fun checkTimePeriod(startMilli:Long , endMilli:Long, listingID:String ){
        var overlap=false
        val myRefListings=db.getReference(Constants.BOOKINGS_PATH).orderByChild("listingID").equalTo(listingID)
        var bookingStart:String?=""
        var bookingEnd:String?=""

        myRefListings.get().addOnCompleteListener{ task->
            val snapshot=task.result.children
            //Check if current time periods "overlaps" any previous bookings time period.
            for (i in snapshot){
                bookingStart= i.child("startTime").getValue(String::class.java)
                bookingEnd= i.child("endTime").getValue(String::class.java)

                var bookingStartMilli=SimpleDateFormat(Constants.DATE_TIME_FORMAT).parse(bookingStart).time
                var bookingEndMilli=SimpleDateFormat(Constants.DATE_TIME_FORMAT).parse(bookingEnd).time

                if((startMilli<= bookingEndMilli) &&(endMilli>=bookingStartMilli)){
                    overlap=true
                    break
                }
            }

            val duration = Toast.LENGTH_LONG
            if(endMilli<=startMilli){
                val toast = Toast.makeText(applicationContext, "Please enter an end date that occurs after the start date.", duration)
                toast.show()
            }
            else if(overlap){
                val toast = Toast.makeText(applicationContext,
                    "Your entry is overlapped by an existing booking from $bookingStart to $bookingEnd", duration)
                toast.show()
            }
            else{
                //Open confirmation dialog
                ConfirmationFragment(name, price,startString,endString,listingID,userUID).show(supportFragmentManager,ConfirmationFragment.TAG)
            }
        }
    }
}