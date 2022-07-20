package com.cmpt362.rentit

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.cmpt362.rentit.db.User
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {

    private lateinit var editTextUsername: EditText
    private lateinit var editTextPassword: EditText
    private lateinit var db: FirebaseDatabase
    companion object{
        val USERS_TABLE_NAME = "Users"
    }
    private val PASSWORD_PATH_STRING = "password"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        db = Firebase.database
        editTextUsername = findViewById(R.id.loginActivity_editText_username)
        editTextPassword = findViewById(R.id.loginActivity_editText_password)

    }

    fun userLogin(view: View){
        val username = editTextUsername.text
        val enteredPassword = editTextPassword.text
        val myRefUsers = db.getReference(USERS_TABLE_NAME)

        val userDataSnapshot = myRefUsers.child(username.toString()).get()

        userDataSnapshot.addOnSuccessListener {
            if (it.exists()){
                Toast.makeText(this, "Successfully logged in as ${username}", Toast.LENGTH_SHORT).show()

//                TODO: add user password in Users table
//                val userPassword = it.child(PASSWORD_PATH_STRING).value
//                if (enteredPassword == userPassword){
//                    Toast.makeText(this, "Successfully logged in as ${username}", Toast.LENGTH_SHORT).show()
//                }
            }
            else{
                Toast.makeText(this, "Username ${username} does not exist", Toast.LENGTH_SHORT).show()
            }
        }

        userDataSnapshot.addOnFailureListener{
            Toast.makeText(this, "Failed to login", Toast.LENGTH_SHORT).show()
        }

        finish()
    }

    fun userSignUp(view: View){
        val intent = Intent(this, SignUpActivity::class.java)
        startActivity(intent)
        this.finish()
    }

}