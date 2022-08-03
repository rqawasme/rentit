package com.cmpt362.rentit

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
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
import com.cmpt362.rentit.users.LoginActivity
import com.cmpt362.rentit.users.UserProfileActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

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

        binding.appBarMain.fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_rentals, R.id.nav_slideshow
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        firebaseAuth = FirebaseAuth.getInstance()
//        //Testing firebase connection, just for reference
//        //Gets FireBaseInstance, behind the scenes FireBase managed a single connection and dedupes appropriately if needed, so you can just do in multiple places.
//        db= Firebase.database
//
//        //Get reference, create object and set value.
//        //For below, username is the "key", if you change the other values but do .setvalue() with the same username, it'll just update the value for the username.
//        //For different username, it will create a new User object and insert.
//        val myRefUsers = db.getReference("Users")
//        val username="keiN"
//        val user= User(1,username,"knakano@sfu.ca","1231111111","testAddr")
//        myRefUsers.child(username).setValue(user)
//
//        val myRefListings=db.getReference("Listings")
//        val listingID=1337
//        val listing= Listing(listingID,"type","ListingName",1337.00,"test listing",1,1,true)
//        myRefListings.child(listingID.toString()).setValue(listing)
//        //Todo: Probably need to create a helper function to autoincrement ID for new entries
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
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
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
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
            Toast.makeText(this, Constants.PLEASE_LOGIN_MSG, Toast.LENGTH_SHORT).show()
        }
    }

}