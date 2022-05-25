package com.kitabisa.scholarshipmanagement.utils

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.kitabisa.scholarshipmanagement.R

object Utils {
    fun ImageView.loadImage(url: String?) {
        Glide.with(this.context)
            .load(url)
            .centerCrop()
            .placeholder(R.drawable.ic_image)
            .into(this)
    }
}