package com.kitabisa.scholarshipmanagement.ui

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.kitabisa.scholarshipmanagement.R
import com.kitabisa.scholarshipmanagement.databinding.ActivityLoginBinding
import com.kitabisa.scholarshipmanagement.ui.detailapplicant.DetailApplicantActivity
import com.kitabisa.scholarshipmanagement.ui.home.HomeActivity
import com.kitabisa.scholarshipmanagement.utils.isValidEmail


class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var auth: FirebaseAuth
    private lateinit var email: String
    private lateinit var password: String
    val Req_Code: Int = 123

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupView()

        // Configure Google Sign In
        val gso = GoogleSignInOptions
            .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.webclient_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)
        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        //google sign-in
        binding.googleButton.setOnClickListener {
            signInGoogle()
        }

        //submit button onclick
        binding.btnSubmit.setOnClickListener {

            val noErrorResult = inputFieldFilled()
            if (noErrorResult) {
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            Log.d(TAG, "signInWithEmail:success")
                            val user = auth.currentUser
//                            updateUI(user)
                        } else {
                            Log.w(TAG, "signInWithEmail:failure", task.exception)
                            Toast.makeText(
                                baseContext, "Authentication failed.",
                                Toast.LENGTH_SHORT
                            ).show()
//                            updateUI(null)
                        }
                    }
            } else {
                Toast.makeText(this, "Some data is invalid", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun inputFieldFilled(): Boolean {
        var noError = true
        val emailInput = binding.emailInput.editText
        val passwordInput = binding.passwordInput


        //cek email
        if (!isValidEmail(emailInput?.text.toString())) {
            binding.emailInput.error = "invalid email"
            noError = false
        } else {
            val splitted = emailInput?.text?.split("@")
            if (splitted?.get(1).toString().lowercase() != "kitabisa.com") {
                binding.emailInput.error = "only email issued by kitabisa can be used"
                noError = false
            } else {
                email = emailInput?.text.toString()
                binding.emailInput.isErrorEnabled = false
            }
        }

        //cek password
        if (passwordInput.editText?.text?.isEmpty() == true) {
            passwordInput.error = "Password cannot be empty"
            noError = false
        } else {
            password = passwordInput.editText?.text.toString()
            passwordInput.isErrorEnabled = false
        }
        return noError
    }

    private fun signInGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        resultLauncher.launch(signInIntent)
//        startActivityForResult(signInIntent, Req_Code)
    }

    private var resultLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        Log.d(TAG, "${result.resultCode}")
        if (result.resultCode == Activity.RESULT_OK) {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account: GoogleSignInAccount = task.getResult(ApiException::class.java)!!
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
                firebaseAuthWithGoogle(account.idToken!!)

            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e)
            }
        }
    }

//    @Deprecated("Deprecated in Java")
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (requestCode == Req_Code) {
//            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
//            handleResult(task)
//        }
//    }

//    private fun handleResult(completedTask: Task<GoogleSignInAccount>) {
//        try {
//            val account: GoogleSignInAccount? = completedTask.getResult(ApiException::class.java)
//            if (account != null) {
//                UpdateUI(account)
//            }
//        } catch (e: ApiException) {
//            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show()
//        }
//    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    updateUI(null)
                }
            }
    }

    private fun updateUI(currentUser: FirebaseUser?) {
        if (currentUser != null) {
            startActivity(Intent(this@LoginActivity, DetailApplicantActivity::class.java))
            finish()
        }
    }

//    private fun UpdateUI(account: GoogleSignInAccount) {
//        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
//            auth.signInWithCredential(credential).addOnCompleteListener { task ->
//            if (task.isSuccessful) {
//                val intent = Intent(this, HomeActivity::class.java)
//                startActivity(intent)
//                finish()
//            }
//        }
//    }

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

//    override fun onStart() {
//        super.onStart()
//        val currentUser = auth.currentUser
//        updateUI(currentUser)
//    }

    companion object {
        private const val TAG = "LoginActivity"
    }

}