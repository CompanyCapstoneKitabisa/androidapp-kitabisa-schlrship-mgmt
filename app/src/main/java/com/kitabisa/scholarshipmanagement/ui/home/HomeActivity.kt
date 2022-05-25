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
import android.util.Log
import android.view.KeyEvent
import com.kitabisa.scholarshipmanagement.R
import com.kitabisa.scholarshipmanagement.databinding.ActivityHomeBinding
import com.kitabisa.scholarshipmanagement.ui.LoginActivity
import com.kitabisa.scholarshipmanagement.ui.MainActivity
import com.kitabisa.scholarshipmanagement.ui.DataViewModelFactory
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.kitabisa.scholarshipmanagement.data.Campaign
import com.kitabisa.scholarshipmanagement.data.Resource
import com.kitabisa.scholarshipmanagement.databinding.ActivityHomeBinding
import com.kitabisa.scholarshipmanagement.ui.detailcampaign.DetailCampaignActivity

class HomeActivity : AppCompatActivity(), CampaignAdapter.CampaignCallback {

class HomeActivity : AppCompatActivity() {
//    private lateinit var auth: FirebaseAuth
    private lateinit var homeBinding: ActivityHomeBinding
    lateinit var mGoogleSignInClient: GoogleSignInClient

    private val auth by lazy {
        FirebaseAuth.getInstance()
    }

    private lateinit var binding: ActivityHomeBinding
    private val campaignAdapter = CampaignAdapter(this)

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

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        val factory: DataViewModelFactory = DataViewModelFactory.getInstance()
        val homeViewModel: HomeViewModel by viewModels {
            factory
        }

        binding.apply {
            val layoutManager = LinearLayoutManager(this@HomeActivity)
            rvCampaign.layoutManager = layoutManager
            rvCampaign.setHasFixedSize(true)
            rvCampaign.adapter = campaignAdapter
        }

        Log.v("YOYYYY", "Berhasil Mendapatkan Data Campaign\"")

        homeViewModel.getCampaign().observe(this) { result ->
            Log.v("YOYYYY2", "Berhasil Mendapatkan Data Campaign\"")
            if (result != null) {
                when (result) {
                    is Resource.Success -> {
//                        result.data?.listCampaign?.let { campaignAdapter.setData(it) }
                        campaignAdapter.setData(result.data?.listCampaign)
                        Log.v("Test", result.data?.listCampaign.toString())
//                        Toast.makeText(this, "Berhasil Mendapatkan Data Campaign", Toast.LENGTH_SHORT).show()
                    }
                    is Resource.Error -> {
                        Log.v("Test", "Gagal Mendapatkan Data Campaign\"")
//                        Toast.makeText(this, "Gagal Mendapatkan Data Campaign", Toast.LENGTH_SHORT).show()
                    }
                    else -> {}
                }
            }
        }
    }

    override fun onStoryClick(campaign: Campaign) {
        val storyDetailIntent = Intent(this, DetailCampaignActivity::class.java)
        storyDetailIntent.putExtra(DetailCampaignActivity.ID, campaign.id)
        startActivity(storyDetailIntent)
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
