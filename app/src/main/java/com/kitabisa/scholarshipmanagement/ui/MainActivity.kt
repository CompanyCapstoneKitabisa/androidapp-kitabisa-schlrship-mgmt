package com.kitabisa.scholarshipmanagement.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.kitabisa.scholarshipmanagement.databinding.ActivityMainBinding
import com.kitabisa.scholarshipmanagement.ui.detailapplicant.DetailApplicantActivity
import com.kitabisa.scholarshipmanagement.ui.home.HomeActivity

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val intent = Intent(this, LoginActivity::class.java)

        Handler(mainLooper).postDelayed({
            startActivity(intent)
            finish()
        }, DELAY_TIME)
    }

    companion object {
        const val DELAY_TIME = 3000L
    }
}