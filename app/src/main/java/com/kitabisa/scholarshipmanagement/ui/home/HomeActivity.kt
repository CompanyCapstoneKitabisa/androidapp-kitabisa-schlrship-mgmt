package com.kitabisa.scholarshipmanagement.ui.home

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.kitabisa.scholarshipmanagement.databinding.ActivityHomeBinding
import com.kitabisa.scholarshipmanagement.ui.LoginActivity
import com.kitabisa.scholarshipmanagement.ui.DataViewModelFactory
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.kitabisa.scholarshipmanagement.data.Campaign
import com.kitabisa.scholarshipmanagement.data.Resource
import com.kitabisa.scholarshipmanagement.ui.detailcampaign.DetailCampaignActivity

class HomeActivity : AppCompatActivity(), CampaignAdapter.CampaignCallback {

//    private lateinit var auth: FirebaseAuth
    lateinit var mGoogleSignInClient: GoogleSignInClient

    private val auth by lazy {
        FirebaseAuth.getInstance()
    }

    private lateinit var binding: ActivityHomeBinding
    private val campaignAdapter = CampaignAdapter(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        val factory: DataViewModelFactory = DataViewModelFactory.getInstance()
        val homeViewModel: HomeViewModel by viewModels {
            factory
        }

        val firebaseUser = auth.currentUser

//        firebaseUser?.getIdToken(true)?.addOnSuccessListener { res ->
//            homeViewModel.getCampaign(res.token.toString()).observe(this) { result ->
//                if (result != null) {
//                    when (result) {
//                        is Resource.Success -> {
//                            campaignAdapter.setData(result.data?.listCampaign)
//                            Log.v("Test", result.data?.listCampaign.toString())
//                        }
//                        is Resource.Error -> {
//                            Log.v("Test", "Gagal Mendapatkan Data Campaign\"")
//                        }
//                        else -> {}
//                    }
//                }
//            }
//        }

        //local data (hapus ini nanti)
        val listCampaign = ArrayList<Campaign>()

        for (y in 1..3) {
            val campaign = Campaign(y.toString(), "Beasiswa Narasi", "Narasi", "https://campuspedia.id/news/wp-content/uploads/2021/08/Beasiswa-Celengan-Narasi.jpg")
            listCampaign.add(campaign)
        }

        campaignAdapter.setData(listCampaign)

        //end

        binding.apply {
            val layoutManager = LinearLayoutManager(this@HomeActivity)
            rvCampaign.layoutManager = layoutManager
            rvCampaign.setHasFixedSize(true)
            rvCampaign.adapter = campaignAdapter
        }

        Log.d("EMAIL", firebaseUser?.email.toString())

//        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//            .requestIdToken(getString(R.string.default_web_client_id))
//            .requestEmail()
//            .build()
//        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        binding.logout.setOnClickListener {
            signOut()
        }

        Log.v("YOYYYY", "Berhasil Mendapatkan Data Campaign\"")
    }

    override fun onStoryClick(campaign: Campaign) {
        val campaignDetailIntent = Intent(this, DetailCampaignActivity::class.java)
        campaignDetailIntent.putExtra(DetailCampaignActivity.ID_CAMPAIGN, campaign.id)
        startActivity(campaignDetailIntent)
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
