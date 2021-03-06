package com.kitabisa.scholarshipmanagement.ui.login

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
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
import com.kitabisa.scholarshipmanagement.R
import com.kitabisa.scholarshipmanagement.databinding.ActivityLoginBinding
import com.kitabisa.scholarshipmanagement.ui.CustomLoadingDialog
import com.kitabisa.scholarshipmanagement.ui.home.HomeActivity
import com.kitabisa.scholarshipmanagement.ui.superadmin.AdminActivity
import com.kitabisa.scholarshipmanagement.utils.isValidEmail


class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var auth: FirebaseAuth
    private lateinit var email: String
    private lateinit var password: String
    private lateinit var customLoadingDialog: CustomLoadingDialog


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        binding.errorMessage.visibility = View.GONE
        setContentView(binding.root)
        customLoadingDialog = CustomLoadingDialog(this)
        supportActionBar?.hide()

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
                renderLoading(true)
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            renderLoading(false)
                            Log.d(TAG, "signInWithEmail:success")
                            val user = auth.currentUser
                            updateUI(user)
                            Toast.makeText(
                                baseContext, "Login Success.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }.addOnFailureListener(this) {
                        Log.w(TAG, "signInWithEmail:failure", it)
                        Toast.makeText(
                            baseContext, "Authentication failed.",
                            Toast.LENGTH_SHORT
                        ).show()
                        renderLoading(false)
                        binding.errorMessage.visibility = View.VISIBLE
                        updateUI(null)
                    }
            } else {
                Toast.makeText(this, "Some data are invalid", Toast.LENGTH_SHORT).show()
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
                binding.emailInput.error = "Only email issued by kitabisa can be used"
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
    }

    private var resultLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        Log.d(TAG, "${result.resultCode}")
        if (result.resultCode == Activity.RESULT_OK) {
            val task: Task<GoogleSignInAccount> =
                GoogleSignIn.getSignedInAccountFromIntent(result.data)

            if(task.result.email?.contains("@kitabisa.com")!!) {
                try {
                    Toast.makeText(this, task.result.email, Toast.LENGTH_LONG).show()
                    // Google Sign In was successful, authenticate with Firebase
                    val account: GoogleSignInAccount = task.getResult(ApiException::class.java)!!
                    Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
                    renderLoading(true)
                    firebaseAuthWithGoogle(account.idToken!!)

                } catch (e: ApiException) {
                    // Google Sign In failed, update UI appropriately
                    Log.w(TAG, "Google sign in failed", e)
                    Toast.makeText(this, e.message, Toast.LENGTH_LONG).show()
                }
            }else{
                googleSignInClient.signOut()
                Toast.makeText(this, "Who are you ?", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    renderLoading(false)
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    renderLoading(false)
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    updateUI(null)
                }
            }
    }

    private fun updateUI(currentUser: FirebaseUser?) {
        if (currentUser != null) {
            if (currentUser.email.toString() == "superadmin@kitabisa.com") {
                Log.d("emailuser", currentUser.email.toString())
                startActivity(Intent(this@LoginActivity, AdminActivity::class.java))
                finish()
            } else {
                startActivity(Intent(this@LoginActivity, HomeActivity::class.java))
                finish()
            }
        }
    }


    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        updateUI(currentUser)
    }

    private fun renderLoading(state: Boolean) {
        if (state) {
            customLoadingDialog.show()
            customLoadingDialog.setCancelable(false)
        } else {
            customLoadingDialog.dismiss()
        }
    }

    companion object {
        private const val TAG = "LoginActivity"
    }

}