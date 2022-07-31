package com.cmpt362.rentit.details.booking

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.cmpt362.rentit.R
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*

//Need:
//Price ,name, id
//User but that can be fetched

class BookingActivity: AppCompatActivity(), DialogInterface {
    private var listingID:Long= -1
    private var name:String? = ""
    private var price:Float? = 0f

    var currentdate:String? = ""
    var currentTime: String? =""

    lateinit var startDate: TextView
    lateinit var endDate: TextView
    lateinit var startTime: TextView
    lateinit var endTime: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_booking)

        listingID= intent.getLongExtra("listingID",-1)
        name= intent.getStringExtra("listingName")
        price = intent.getFloatExtra("price",-1F)

        var listingNameTextView=findViewById<TextView>(R.id.booking_name)
        var priceTextView=findViewById<TextView>(R.id.booking_price)
        listingNameTextView.setText(name)
        priceTextView.setText("$" + String.format("%.2f",price))

        val sdf = SimpleDateFormat("MMMM dd yyyy")
        val currentDate = sdf.format(Date())
        val currentTime=SimpleDateFormat("HH:mm").format(Date())

        startDate=findViewById<TextView>(R.id.booking_start_date)
        startTime=findViewById<TextView>(R.id.booking_start_time)

        startDate.setText(currentDate)
        startTime.setText(currentTime)

        endDate=findViewById<TextView>(R.id.booking_end_date)
        endTime=findViewById<TextView>(R.id.booking_end_time)

        endDate.setText(currentDate)
        endTime.setText(currentTime)

        super.onCreate(savedInstanceState)
    }

    fun setDate(view:View){
        var id= view.id
        if(id==R.id.booking_start_date){
            DateDialogFragment("start").show(supportFragmentManager,DateDialogFragment.TAG)
        }
        else if(id== R.id.booking_end_date) {
            DateDialogFragment("end").show(supportFragmentManager,DateDialogFragment.TAG)
        }

        println("SetDate")
    }

    fun setTime(view:View){
        println("setTime")
    }

    fun confirm(view: View){
        println("Confirm clicked ")
    }

    fun cancel(view: View){
        println("cancel clicked ")
    }

    override fun saveDateDialog(year: Int, month: Int, day: Int, type:String) {

        var newDate= Date()
        newDate.year=year-1900 //Due to Date weirdness, need to subtract 1900.
        newDate.month=month
        newDate.date=day
        val sdf = SimpleDateFormat("MMMM dd yyyy")
        val currentDate = sdf.format(newDate)

        if(type=="start"){
            startDate.text = currentDate
        }
        else if(type=="end"){
            endDate.text = currentDate
        }
    }

    override fun saveTimeDialog(hour: Int, min: Int, type:String) {

    }
}