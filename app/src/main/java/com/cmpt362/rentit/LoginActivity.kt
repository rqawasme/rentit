package com.cmpt362.rentit

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.cmpt362.rentit.Constants.INVALID_EMAIL_FORMAT_ERROR
import com.cmpt362.rentit.Constants.NO_PASSWORD_ENTERED_ERROR
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {

    private lateinit var editTextEmail: EditText
    private lateinit var editTextPassword: EditText
    private lateinit var db: FirebaseDatabase
    private lateinit var firebaseAuth: FirebaseAuth
    private var email = ""
    private var password = ""
    companion object{
        val USERS_TABLE_NAME = "Users"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        db = Firebase.database
        editTextEmail = findViewById(R.id.loginActivity_editText_email)
        editTextPassword = findViewById(R.id.loginActivity_editText_password)
        firebaseAuth = FirebaseAuth.getInstance()
        checkUser()

    }

    fun userLogin(view: View){

        email = editTextEmail.text.toString().trim()
        password = editTextPassword.text.toString().trim()

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            editTextEmail.setError(INVALID_EMAIL_FORMAT_ERROR)
        }
        else if (password.isEmpty()){
            editTextPassword.setError(NO_PASSWORD_ENTERED_ERROR)
        }
        else{
            firebaseLogin()
        }

    }

    private fun firebaseLogin() {
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                val firebaseUser = firebaseAuth.currentUser
                val email = firebaseUser!!.email
                Toast.makeText(this, "Logged in as ${email}", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener{
                Toast.makeText(this, "Login failed due to ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    fun checkUser(){
        val firebaseUser = firebaseAuth.currentUser

        if (firebaseUser != null){
            Toast.makeText(this, "Already logged in as ${firebaseUser.email}", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    fun userSignUp(view: View){
        val intent = Intent(this, SignUpActivity::class.java)
        startActivity(intent)
        this.finish()
    }

}