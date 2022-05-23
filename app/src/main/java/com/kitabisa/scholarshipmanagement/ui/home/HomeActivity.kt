package com.kitabisa.scholarshipmanagement.ui.home

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.kitabisa.scholarshipmanagement.R
import com.kitabisa.scholarshipmanagement.databinding.ActivityHomeBinding
import com.kitabisa.scholarshipmanagement.ui.LoginActivity
import com.kitabisa.scholarshipmanagement.ui.MainActivity

class HomeActivity : AppCompatActivity() {
//    private lateinit var auth: FirebaseAuth
    private lateinit var homeBinding: ActivityHomeBinding
    lateinit var mGoogleSignInClient: GoogleSignInClient

    private val auth by lazy {
        FirebaseAuth.getInstance()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        homeBinding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(homeBinding.root)

        val firebaseUser = auth.currentUser

        firebaseUser?.getIdToken(true)?.addOnSuccessListener { res ->
            Log.d("TOKEN", res.token.toString())
        }

        Log.d("EMAIL", firebaseUser?.email.toString())

//        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//            .requestIdToken(getString(R.string.default_web_client_id))
//            .requestEmail()
//            .build()
//        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        homeBinding.logout.setOnClickListener {
            signOut()
        }

    }

    private fun signOut() {
        GoogleSignIn.getClient(this, GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()).signOut()
        auth.signOut()
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
//        mGoogleSignInClient.signOut().addOnCompleteListener {
//            val intent = Intent(this, LoginActivity::class.java)
//            Toast.makeText(this, "Logging Out", Toast.LENGTH_SHORT).show()
//            startActivity(intent)
//            finish()
//        }
    }
}
