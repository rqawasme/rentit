package com.cmpt362.rentit

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.cmpt362.rentit.db.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


class SignUpActivity : AppCompatActivity() {

    private lateinit var editTextUsername: EditText
    private lateinit var editTextPassword: EditText
    private lateinit var editTextEmail: EditText
    private lateinit var editTextPhone: EditText
    private lateinit var editTextAddress: EditText

    private var email = ""
    private var password = ""

    private val USERNAME_ERROR = "Username is required"
    private val PASSWORD_ERROR = "Password is required"
    private val MIN_PASSWORD_LENGTH = 6
    private val PASSWORD_LENGTH_ERROR = "Minimum length of password is ${MIN_PASSWORD_LENGTH}"
    private val REGISTERED_SUCCESSFUL_MSG = "Registered"

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var db: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        editTextUsername = findViewById(R.id.signUpActivity_editText_username)
        editTextPassword = findViewById(R.id.signUpActivity_editText_password)
        editTextEmail = findViewById(R.id.signUpActivity_editText_email)
        editTextPhone = findViewById(R.id.signUpActivity_editText_phone)
        editTextAddress = findViewById(R.id.signUpActivity_editText_address)
        db= Firebase.database
        firebaseAuth = FirebaseAuth.getInstance()

    }

    fun registerUser(view: View){
        email = editTextEmail.text.toString().trim()
        password = editTextPassword.text.toString().trim()

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            editTextEmail.setError(Constants.INVALID_EMAIL_FORMAT_ERROR)
        }
        else if (password.isEmpty()){
            editTextPassword.setError(Constants.NO_PASSWORD_ENTERED_ERROR)
        }
        else if (password.length < MIN_PASSWORD_LENGTH){
            editTextPassword.setError(Constants.PASSWORD_LENGTH_ERROR)
        }
        else{
            firebaseRegister()
        }
    }

    private fun firebaseRegister() {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                val firebaseUser = firebaseAuth.currentUser
                val email = firebaseUser!!.email
                Toast.makeText(this, "Account created with email ${email}", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener{
                Toast.makeText(this, "Failed to register due to ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }


    fun userLogin(view: View){
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        this.finish()
    }
}