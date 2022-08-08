package com.cmpt362.rentit

import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import com.cmpt362.rentit.databinding.ActivityMainBinding
import com.cmpt362.rentit.db.Booking
import com.cmpt362.rentit.ui.bookingList.BookingListAdapter
import com.cmpt362.rentit.users.LoginActivity
import com.cmpt362.rentit.users.UserProfileActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var db: FirebaseDatabase
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_rentals, R.id.nav_booking_list
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        firebaseAuth = FirebaseAuth.getInstance()

        db= Firebase.database

        bookingEndNotification()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)

        for (i in 0 .. menu.size() - 1){
            val menuItem = menu.getItem(i)
            val spannable = SpannableString(menuItem.title)
            spannable.setSpan(ForegroundColorSpan(resources.getColor(R.color.customAccent)), 0, spannable.length, 0)
            menuItem.title = spannable
        }

        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.action_user_login){
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
        else if (id == R.id.action_user_logout){
            logoutUser()
        }
        else if (id == R.id.action_settings){

            //Check if logged in
            val firebaseUser = firebaseAuth.currentUser
            if (firebaseUser != null){
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
            }
            else{
                Toast.makeText(this, Constants.PLEASE_LOGIN_MSG, Toast.LENGTH_SHORT).show()
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            }

        }
        return super.onOptionsItemSelected(item)
    }

    fun logoutUser(){
        val firebaseUser = firebaseAuth.currentUser
        if (firebaseUser != null){
            firebaseAuth.signOut()
            Toast.makeText(this, "User ${firebaseUser.email} is logged out", Toast.LENGTH_SHORT).show()
        }
        else{
            Toast.makeText(this, Constants.YOU_ARE_NOT_LOGGED_IN_MSG, Toast.LENGTH_SHORT).show()
        }
    }

    fun bookingEndNotification(){
        val firebaseUser = firebaseAuth.currentUser

        if (firebaseUser != null){
            var userID=firebaseUser.uid
            val myRefBookings=
                db.getReference(Constants.BOOKINGS_PATH).orderByChild("bookerID").equalTo(userID)
            var currentDate= Date()

            //Find expiring booking, open dialog reminding user
            myRefBookings.get().addOnCompleteListener { task ->
                val snapshot = task.result.children
                for (i in snapshot) {
                    var bookingEndDate= SimpleDateFormat(Constants.DATE_TIME_FORMAT).parse(i.child("endTime").getValue(String::class.java))
                    if((bookingEndDate.day==currentDate.day)
                        &&(bookingEndDate.month==currentDate.month)
                        && (bookingEndDate.year==currentDate.year)
                        && (bookingEndDate.time>currentDate.time)){
                        var listingID= i.child("listingID").getValue(String::class.java)
                        val myRefListings= Firebase.database.getReference(Constants.LISTINGS_TABLE_NAME).child(listingID!!)

                        myRefListings.get().addOnCompleteListener { task ->
                            val snapshot= task.result
                            var expiringBooking=snapshot.child("name").getValue(String::class.java)

                            //Open dialog
                            var alertDialogBuilder= AlertDialog.Builder(this)
                            alertDialogBuilder.setTitle("Reminder")
                            alertDialogBuilder.setMessage("Your booking for $expiringBooking will end at $bookingEndDate")
                            alertDialogBuilder.setPositiveButton("Ok",null)
                            alertDialogBuilder.create().show()
                        }
                    }
                }
            }
        }
    }

}