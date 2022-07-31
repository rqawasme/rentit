package com.cmpt362.rentit.details

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.Gravity
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.DialogFragment

class DialogContact(val email:String, val phone:String):DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        var alertDialog=AlertDialog.Builder(requireContext())
        alertDialog.setTitle("Contact Info")

        var linearLayout= LinearLayout(requireContext())
        linearLayout.orientation=LinearLayout.VERTICAL

        var emailTextView= TextView(requireContext())
        emailTextView.autoLinkMask=2
        emailTextView.setText(email)
        emailTextView.textSize=20F


        var phoneTextView= TextView(requireContext())
        phoneTextView.autoLinkMask=4
        phoneTextView.setText(phone)
        phoneTextView.textSize=20F

        linearLayout.addView(emailTextView)
        linearLayout.addView(phoneTextView)
        linearLayout.setPadding(50,10,10,0)

        alertDialog.setView(linearLayout)
        alertDialog.setNegativeButton("OK",null)

        return alertDialog.create()
    }

    companion object {
        const val TAG = "DialogContact"
    }
}