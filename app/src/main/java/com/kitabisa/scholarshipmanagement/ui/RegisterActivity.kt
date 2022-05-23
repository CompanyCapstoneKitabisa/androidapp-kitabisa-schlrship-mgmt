package com.kitabisa.scholarshipmanagement.ui

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import com.kitabisa.scholarshipmanagement.R
import com.kitabisa.scholarshipmanagement.databinding.ActivityMainBinding
import com.kitabisa.scholarshipmanagement.databinding.ActivityRegisterBinding
import com.kitabisa.scholarshipmanagement.utils.isValidEmail

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupView()

        binding.btnSubmit.setOnClickListener {
            val noErrorResult =  inputFieldFilled()
            if(noErrorResult){
                Toast.makeText(this, "All data is good", Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(this, "Some data is invalid", Toast.LENGTH_SHORT).show()

            }
        }
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





    private fun inputFieldFilled(): Boolean{
        var noError = true
        val emailInput = binding.emailInput.editText
        val firstnameInput = binding.firstNameInput
        val lastnameInput = binding.lastNameInput
        val passwordInput = binding.passwordInput


        //cek email
        if(!isValidEmail(emailInput?.text.toString()))
        {
            binding.emailInput.error = "invalid email"
            noError = false
        }else{
            binding.emailInput.isErrorEnabled = false
        }

        //cek nama depan
        if(firstnameInput.editText?.text?.isEmpty() == true){
            firstnameInput.error = "First name cannot be empty"
            noError = false
        }else{
            firstnameInput.isErrorEnabled = false
        }

        //cek nama belakang
        if(lastnameInput.editText?.text?.isEmpty() == true){
            lastnameInput.error = "Last name cannot be empty"
            noError = false
        }else{
            lastnameInput.isErrorEnabled = false
        }

        //cek password
        if(passwordInput.editText?.text?.isEmpty() == true){
            passwordInput.error = "Password cannot be empty"
            noError = false
        }else{
            passwordInput.isErrorEnabled = false
        }

        return noError
    }
}