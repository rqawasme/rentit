package com.cmpt362.rentit.users

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.cmpt362.rentit.Constants
import com.cmpt362.rentit.R
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class UserProfileActivity : AppCompatActivity() {
    private lateinit var textViewUsername: TextView
    private lateinit var textInputEditTextUsername: TextInputEditText
    private lateinit var textInputEditTextEmail: TextInputEditText
    private lateinit var textInputEditTextPhone: TextInputEditText
    private lateinit var textInputEditTextPostalCode: TextInputEditText
    private lateinit var textInputEditTextPassword: TextInputEditText

    private lateinit var db: FirebaseDatabase
    private lateinit var firebaseAuth: FirebaseAuth



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)

        initializeElements()
        populateUserProfile()

    }

    private fun populateUserProfile() {
        val user = firebaseAuth.currentUser
        if (user != null){
            val userId = user.uid
            val myRefUsers = db.getReference(Constants.USERS_TABLE_NAME)

            val userDataSnapshot = myRefUsers.child(userId).get()

            userDataSnapshot.addOnSuccessListener {
                if (it.exists()){
                    val username = it.child(Constants.USERNAME_PATH).getValue(String::class.java)
                    textViewUsername.text = username
                    textInputEditTextUsername.setText(username)
                    textInputEditTextPhone.setText(it.child(Constants.PHONE_PATH).getValue(String::class.java))
                    textInputEditTextPostalCode.setText(it.child(Constants.POSTAL_CODE_PATH).getValue(String::class.java))
                }
            }

            textInputEditTextEmail.setText(user.email.toString())
        }
    }

    private fun initializeElements() {
        textViewUsername = findViewById(R.id.userProfileActivity_textView_username)
        textInputEditTextUsername = findViewById(R.id.userProfileActivity_textInputEditText_username)
        textInputEditTextEmail = findViewById(R.id.userProfileActivity_textInputEditText_email)
        textInputEditTextPhone = findViewById(R.id.userProfileActivity_textInputEditText_phone)
        textInputEditTextPostalCode = findViewById(R.id.userProfileActivity_textInputEditText_postalCode)
        textInputEditTextPassword = findViewById(R.id.userProfileActivity_textInputEditText_password)

        db = Firebase.database
        firebaseAuth = FirebaseAuth.getInstance()
    }

    fun exit(view: View){
        finish()
    }
}