package com.cmpt362.rentit.details.booking

import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import java.util.*

class TimeDialogFragment(type:String): DialogFragment() {
    val c = Calendar.getInstance()
    val hour = c.get(Calendar.HOUR_OF_DAY)
    val minute = c.get(Calendar.MINUTE)
    val type=type

    lateinit var dialogInterface: DialogInterface
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        TimePickerDialog(requireActivity(), TimePickerDialog.OnTimeSetListener() { view, hourOfDay, minute ->
            dialogInterface= context as DialogInterface
            dialogInterface.saveTimeDialog(hourOfDay,minute,type)
        }, hour,minute,true)

    companion object {
        const val TAG = "TimeDialogFragment"
    }
}