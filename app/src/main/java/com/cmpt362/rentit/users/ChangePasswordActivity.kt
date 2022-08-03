package com.cmpt362.rentit.users

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.cmpt362.rentit.Constants
import com.cmpt362.rentit.R
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import java.lang.Exception

class ChangePasswordActivity : AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var editTextCurrentPassword: EditText
    private lateinit var editTextNewPassword1: EditText
    private lateinit var editTextNewPassword2: EditText
    private lateinit var textViewIsAuthenticated: TextView
    private lateinit var buttonAuthenticate: Button
    private lateinit var buttonChangePassword: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_password)

        initializeElements()

        val user = firebaseAuth.currentUser

        if (user != null){
            reAuthenticate()
        }
        else{
            Toast.makeText(this, "Failed to retrieve user data", Toast.LENGTH_LONG).show()
        }

    }

    private fun initializeElements() {
        firebaseAuth = FirebaseAuth.getInstance()

        editTextCurrentPassword = findViewById(R.id.changePasswordActivity_editText_current_password)
        editTextNewPassword1 = findViewById(R.id.changePasswordActivity_editText_new_password_1)
        editTextNewPassword2 = findViewById(R.id.changePasswordActivity_editText_new_password_2)
        textViewIsAuthenticated = findViewById(R.id.changePasswordActivity_textView_isAuthenticated)
        buttonAuthenticate = findViewById(R.id.changePasswordActivity_button_authenticate)
        buttonChangePassword = findViewById(R.id.changePasswordActivity_button_change_password)

        editTextNewPassword1.isEnabled = false
        editTextNewPassword2.isEnabled = false
        buttonChangePassword.isEnabled = false

    }

    private fun reAuthenticate() {
        val user = firebaseAuth.currentUser
        val email = user!!.email.toString()

        buttonAuthenticate.setOnClickListener {
            val userPassword = editTextCurrentPassword.text.toString()

            if (TextUtils.isEmpty(userPassword)){
                editTextCurrentPassword.error = Constants.NO_PASSWORD_ENTERED_ERROR
                editTextCurrentPassword.requestFocus()
            }
            else{
                val credential = EmailAuthProvider.getCredential(email, userPassword)
                firebaseAuth.currentUser!!.reauthenticate(credential).addOnCompleteListener{
                    if (it.isSuccessful){
                        Toast.makeText(this,"Account has been authenticated. You can update your email now.", Toast.LENGTH_LONG).show()
                        textViewIsAuthenticated.text = "Account has been authenticated. You can update your email now."

                        editTextCurrentPassword.isEnabled = false
                        buttonAuthenticate.isEnabled = false
                        buttonChangePassword.isEnabled = true
                        editTextNewPassword1.isEnabled = true
                        editTextNewPassword2.isEnabled = true

                        buttonChangePassword.backgroundTintList = ContextCompat.getColorStateList(this, R.color.dark_green)
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

    fun changePassword(view: View){
        val user = firebaseAuth.currentUser
        val email = user!!.email.toString()

        val currentPassword = editTextCurrentPassword.text.toString()
        val newPassword1 = editTextNewPassword1.text.toString()
        val newPassword2 = editTextNewPassword2.text.toString()

        if (TextUtils.isEmpty(newPassword1)){
            editTextNewPassword1.error = Constants.NO_PASSWORD_ENTERED_ERROR
            editTextNewPassword1.requestFocus()
        }
        else if (TextUtils.isEmpty(newPassword2)){
            editTextNewPassword2.error = "Please re-enter new password"
            editTextNewPassword2.requestFocus()
        }
        else if (newPassword1.length < Constants.MIN_PASSWORD_LENGTH){
            editTextNewPassword1.error = Constants.PASSWORD_LENGTH_ERROR
            editTextNewPassword1.requestFocus()
        }
        else if (newPassword1 != newPassword2){
            editTextNewPassword2.error = "Please re-enter the same password"
            editTextNewPassword2.requestFocus()
        }
        else if (currentPassword == newPassword1){
            editTextNewPassword1.error = "Please enter a new password"
            editTextNewPassword1.requestFocus()
        }
        else{
            user.updatePassword(newPassword1).addOnCompleteListener{
                if (it.isSuccessful){
                    Toast.makeText(this, "Password has been changed", Toast.LENGTH_SHORT).show()
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