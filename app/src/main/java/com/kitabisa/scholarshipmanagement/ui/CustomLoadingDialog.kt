package com.kitabisa.scholarshipmanagement.ui

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import com.kitabisa.scholarshipmanagement.R

class CustomLoadingDialog(context: Context) : Dialog(context){

    init {
        setContentView(R.layout.custom_loading)
        this.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

}