package com.cmpt362.rentit

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
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

    private val USERNAME_ERROR = "Username is required"
    private val PASSWORD_ERROR = "Password is required"
    private val MIN_PASSWORD_LENGTH = 6
    private val PASSWORD_LENGTH_ERROR = "Minimum length of password is ${MIN_PASSWORD_LENGTH}"
    private val REGISTERED_SUCCESSFUL_MSG = "Registered"

    private lateinit var auth: FirebaseAuth;
    private lateinit var db: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        editTextUsername = findViewById(R.id.signUpActivity_editText_username)
        editTextPassword = findViewById(R.id.signUpActivity_editText_password)
        editTextEmail = findViewById(R.id.signUpActivity_editText_email)
        editTextPhone = findViewById(R.id.signUpActivity_editText_phone)
        editTextAddress = findViewById(R.id.signUpActivity_editText_address)
        auth = Firebase.auth
        db= Firebase.database


    }

    fun registerUser(view: View){
        val username = editTextUsername.text
        val password = editTextPassword.text
        val email = editTextEmail.text
        val phone = editTextPhone.text
        val address = editTextAddress.text

        if (username.isEmpty()){
            editTextUsername.error = USERNAME_ERROR
            editTextUsername.requestFocus()
            return
        }
        else if (password.isEmpty()){
            editTextPassword.error = PASSWORD_ERROR
            editTextPassword.requestFocus()
            return
        }

        if (password.length < MIN_PASSWORD_LENGTH){
            editTextPassword.error = PASSWORD_LENGTH_ERROR
            editTextPassword.requestFocus()
            return
        }

        val myRefUsers = db.getReference(LoginActivity.USERS_TABLE_NAME)

        val user = User(1,username.toString(), email.toString(),phone.toString(),address.toString())
        myRefUsers.child(username.toString()).setValue(user)
        Toast.makeText(this, REGISTERED_SUCCESSFUL_MSG, Toast.LENGTH_SHORT).show()
        finish()
    }


    fun userLogin(view: View){
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        this.finish()
    }
}