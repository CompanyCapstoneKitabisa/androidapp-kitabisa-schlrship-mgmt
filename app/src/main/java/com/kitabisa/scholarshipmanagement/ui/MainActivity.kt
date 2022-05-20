package com.kitabisa.scholarshipmanagement.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.kitabisa.scholarshipmanagement.R
import com.kitabisa.scholarshipmanagement.ui.detailapplicant.DetailApplicantActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()

        intent = Intent(this, DetailApplicantActivity::class.java)
        Handler(mainLooper).postDelayed({
            startActivity(intent)
            finish()
        }, DELAY_TIME)
    }

    companion object {
        const val DELAY_TIME = 3000L
    }
}