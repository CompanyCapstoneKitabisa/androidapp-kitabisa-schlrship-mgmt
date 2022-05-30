package com.kitabisa.scholarshipmanagement.utils

import android.widget.ImageView
import com.bumptech.glide.Glide

object Utils {
    fun ImageView.loadImage(url: String?, placeholder: Int) {
        Glide.with(this.context)
            .load(url)
            .centerCrop()
            .placeholder(placeholder)
            .into(this)
    }
}