package com.kitabisa.scholarshipmanagement.ui

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowManager
import com.kitabisa.scholarshipmanagement.R
import com.kitabisa.scholarshipmanagement.databinding.ActivityBoardPageBinding


private lateinit var binding: ActivityBoardPageBinding


class BoardPage : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityBoardPageBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupView()
        init()
    }


    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    private fun init(){
        val loginButton = binding.btnLogin
        val registerButton = binding.btnRegister

        loginButton.setOnClickListener {
            val intent = Intent(this@BoardPage, LoginActivity::class.java)
            startActivity(intent)
        }

        registerButton.setOnClickListener {
            val intent = Intent(this@BoardPage, LoginActivity::class.java)
            startActivity(intent)
        }
    }
}