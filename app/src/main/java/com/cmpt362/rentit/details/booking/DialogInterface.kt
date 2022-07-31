package com.cmpt362.rentit.details.booking

interface DialogInterface {
    fun saveDateDialog(year:Int, month:Int, day:Int, type:String)
    fun saveTimeDialog(hour:Int, min:Int, type:String)
}