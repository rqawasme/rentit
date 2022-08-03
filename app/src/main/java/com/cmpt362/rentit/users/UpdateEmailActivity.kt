package com.cmpt362.rentit.users

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.cmpt362.rentit.Constants
import com.cmpt362.rentit.R
import com.google.firebase.auth.EmailAuthCredential
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import java.lang.Exception

class UpdateEmailActivity : AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var textViewCurrentEmail: TextView
    private lateinit var textViewIsAuthenticated: TextView
    private lateinit var buttonAuthenticate: Button
    private lateinit var buttonUpdateEmail: Button
    private lateinit var editTextPassword: EditText
    private lateinit var editTextNewEmail: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_email)

        initializeElements()
    }

    private fun initializeElements() {
        textViewCurrentEmail = findViewById(R.id.updateEmailActivity_textView_user_current_email)
        textViewIsAuthenticated = findViewById(R.id.updateEmailActivity_textView_isAuthenticated)
        buttonAuthenticate = findViewById(R.id.updateEmailActivity_button_authenticate)
        buttonUpdateEmail = findViewById(R.id.updateEmailActivity_button_update_email)
        editTextPassword = findViewById(R.id.updateEmailActivity_editText_verify_password)
        editTextNewEmail = findViewById(R.id.updateEmailActivity_editText_new_email)

        buttonUpdateEmail.isEnabled = false
        editTextNewEmail.isEnabled = false

        firebaseAuth = FirebaseAuth.getInstance()
        val user = firebaseAuth.currentUser

        if (user != null){
            textViewCurrentEmail.text = user.email
            reAuthenticate()
        }
        else{
            Toast.makeText(this, "Failed to retrieve user data", Toast.LENGTH_LONG).show()
        }


    }

    private fun reAuthenticate() {
        val email = firebaseAuth.currentUser!!.email.toString()
        buttonAuthenticate.setOnClickListener{
            val userPassword = editTextPassword.text.toString()

            if (TextUtils.isEmpty(userPassword)){
                editTextPassword.error = Constants.NO_PASSWORD_ENTERED_ERROR
                editTextPassword.requestFocus()
            }
            else{
                val credential = EmailAuthProvider.getCredential(email, userPassword)
                firebaseAuth.currentUser!!.reauthenticate(credential).addOnCompleteListener{
                    if (it.isSuccessful){
                        Toast.makeText(this,"Account has been authenticated. You can update your email now.", Toast.LENGTH_LONG).show()
                        textViewIsAuthenticated.text = "Account has been authenticated. You can update your email now."

                        editTextPassword.isEnabled = false
                        buttonAuthenticate.isEnabled = false
                        buttonUpdateEmail.isEnabled = true
                        editTextNewEmail.isEnabled = true

                        buttonUpdateEmail.backgroundTintList = ContextCompat.getColorStateList(this, R.color.dark_green)
                    }
                    else{
                        try {
                            throw it.exception!!
                        }
                        catch (e: Exception){
                            Toast.makeText(this,e.message, Toast.LENGTH_LONG).show()
                        }
                    }

                }
            }

        }

    }

    fun updateEmail(view: View){
        val newEmail = editTextNewEmail.text.toString()
        val user = firebaseAuth.currentUser
        val currentEmail = user!!.email.toString()

        if (TextUtils.isEmpty(newEmail)){
            editTextNewEmail.error = "Please enter an email address"
            editTextNewEmail.requestFocus()
        }
        else if (newEmail == currentEmail){
            editTextNewEmail.error = "New email is the same as the current email. Please enter an new email address."
            editTextNewEmail.requestFocus()
        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(newEmail).matches()){
            editTextNewEmail.error = Constants.INVALID_EMAIL_FORMAT_ERROR
            editTextNewEmail.requestFocus()
        }
        else{

            user.updateEmail(newEmail).addOnCompleteListener{
                if (it.isComplete){
                    Toast.makeText(this, "Email has been updated. Please verify your new email", Toast.LENGTH_SHORT).show()
                    finish()
                }
                else{
                    try {
                        throw it.exception!!
                    }
                    catch (e: Exception){
                        Toast.makeText(this,e.message, Toast.LENGTH_LONG).show()
                    }
                }
            }

        }

    }

}