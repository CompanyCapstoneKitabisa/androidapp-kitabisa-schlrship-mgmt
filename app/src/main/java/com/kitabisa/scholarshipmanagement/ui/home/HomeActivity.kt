package com.kitabisa.scholarshipmanagement.ui.home

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
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
import com.kitabisa.scholarshipmanagement.data.Result
import com.kitabisa.scholarshipmanagement.ui.CustomLoadingDialog
import com.kitabisa.scholarshipmanagement.ui.detailcampaign.DetailCampaignActivity

class HomeActivity : AppCompatActivity(), CampaignAdapter.CampaignCallback {

//    private lateinit var auth: FirebaseAuth
    lateinit var mGoogleSignInClient: GoogleSignInClient

    private val auth by lazy {
        FirebaseAuth.getInstance()
    }

    private lateinit var binding: ActivityHomeBinding
    private val campaignAdapter = CampaignAdapter(this)
    private lateinit var customLoadingDialog: CustomLoadingDialog
    private var listCampaign = ArrayList<Campaign>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        customLoadingDialog = CustomLoadingDialog(this)
        val factory: DataViewModelFactory = DataViewModelFactory.getInstance()
        val homeViewModel: HomeViewModel by viewModels {
            factory
        }

        val firebaseUser = auth.currentUser

        //comment code to use local data
        firebaseUser?.getIdToken(true)?.addOnSuccessListener { res ->
            homeViewModel.getCampaign(res.token.toString()).observe(this) { result ->
                if (result != null) {
                    when (result) {
                        is Resource.Success -> {
                            listCampaign = result.data!!.listCampaign
                            renderLoading(false)
                            binding.root.visibility = View.VISIBLE
                        }
                        is Resource.Error -> {
                            finish()
                            Toast.makeText(this, result.data?.error.toString(), Toast.LENGTH_SHORT).show()
                        }
                        is Resource.Loading -> {
                            renderLoading(true)
                            binding.root.visibility = View.GONE
                        }
                    }
                }
            }
        }
        //end

        //local data (hapus ini nanti)
        if (listCampaign.isEmpty()){
            for (y in 1..3) {
                val campaign = Campaign(y.toString(), "Beasiswa Narasi", "Narasi", "https://campuspedia.id/news/wp-content/uploads/2021/08/Beasiswa-Celengan-Narasi.jpg")
                listCampaign.add(campaign)
            }
        }
        //end


        campaignAdapter.setData(listCampaign)


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

    private fun renderLoading(state: Boolean) {
        if (state) {
            customLoadingDialog.show()
            customLoadingDialog.setCancelable(false)
        } else {
            customLoadingDialog.dismiss()
        }
    }
}
