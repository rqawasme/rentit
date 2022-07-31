package com.cmpt362.rentit.details.booking

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import java.util.*

class DateDialogFragment(type:String): DialogFragment() {
    val c = Calendar.getInstance()
    val year = c.get(Calendar.YEAR)
    val month = c.get(Calendar.MONTH)
    val day = c.get(Calendar.DAY_OF_MONTH)
    var type=type
    lateinit var dialogInterface: DialogInterface
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        DatePickerDialog(requireActivity(), DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            dialogInterface= context as DialogInterface
            c.set(year,monthOfYear,day)

            dialogInterface.saveDateDialog(year,monthOfYear,dayOfMonth,type)
        }, year, month, day)

    companion object {
        const val TAG = "DateDialogFragment"
    }
}