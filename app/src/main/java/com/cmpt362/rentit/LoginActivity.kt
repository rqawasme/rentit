package com.cmpt362.rentit

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {

    private lateinit var editTextUsername: EditText
    private lateinit var editTextPassword: EditText
    private lateinit var db: FirebaseDatabase


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        db = Firebase.database
        editTextUsername = findViewById(R.id.loginActivity_editText_username)
        editTextPassword = findViewById(R.id.loginActivity_editText_password)

    }

    fun userLogin(view: View){
        val username = editTextUsername.text
        val password = editTextPassword.text
        val myRefUsers = db.getReference("Users")
        finish()
    }

    fun userSignUp(view: View){
        val intent = Intent(this, SignUpActivity::class.java)
        startActivity(intent)
        this.finish()
    }

}