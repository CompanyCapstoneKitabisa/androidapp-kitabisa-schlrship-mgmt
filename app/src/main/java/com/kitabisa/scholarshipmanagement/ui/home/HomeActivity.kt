package com.kitabisa.scholarshipmanagement.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.kitabisa.scholarshipmanagement.data.Campaign
import com.kitabisa.scholarshipmanagement.data.Resource
import com.kitabisa.scholarshipmanagement.databinding.ActivityHomeBinding
import com.kitabisa.scholarshipmanagement.ui.CustomLoadingDialog
import com.kitabisa.scholarshipmanagement.ui.DataViewModelFactory
import com.kitabisa.scholarshipmanagement.ui.detailcampaign.DetailCampaignActivity
import com.kitabisa.scholarshipmanagement.ui.login.LoginActivity

class HomeActivity : AppCompatActivity(), CampaignAdapter.CampaignCallback {

    private val auth by lazy {
        FirebaseAuth.getInstance()
    }

    private var tempToken: String = ""

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
        renderLoading(true)
        binding.root.visibility = View.GONE

        val homeViewModel: HomeViewModel by viewModels {
            factory
        }

        val firebaseUser = auth.currentUser
        firebaseUser?.getIdToken(true)?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                tempToken = task.result.token.toString()
                homeViewModel.getCampaign(
                    tempToken
                ).observe(this) { result ->
                    if (result != null) {
                        when (result) {
                            is Resource.Success -> {
                                if (result.data?.listCampaign != null) {
                                    listCampaign = result.data.listCampaign
                                    campaignAdapter.setData(listCampaign)
                                    renderLoading(false)
                                    binding.root.visibility = View.VISIBLE
                                    Toast.makeText(
                                        this, result.data.message,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } else {
                                    renderLoading(false)
                                    binding.tvDataNull.visibility = View.VISIBLE
                                    binding.root.visibility = View.VISIBLE
                                }
                            }
                            is Resource.Error -> {
                                Toast.makeText(
                                    this,
                                    result.message.toString(),
                                    Toast.LENGTH_SHORT
                                ).show()
                                signOut()
                            }
                            is Resource.Loading -> {
                                renderLoading(true)
                                binding.root.visibility = View.GONE
                            }
                        }
                    }
                }
            }
        }?.addOnFailureListener {
            renderLoading(false)
            Toast.makeText(this, it.message.toString(), Toast.LENGTH_SHORT).show()
            signOut()
        }
        //end

        binding.apply {
            val layoutManager = LinearLayoutManager(this@HomeActivity)
            rvCampaign.layoutManager = layoutManager
            rvCampaign.setHasFixedSize(true)
            rvCampaign.adapter = campaignAdapter
        }

        binding.logout.setOnClickListener {
            signOut()
        }

    }

    override fun onStoryClick(campaign: Campaign) {
        val campaignDetailIntent = Intent(this, DetailCampaignActivity::class.java)
        campaignDetailIntent.putExtra(DetailCampaignActivity.ID_CAMPAIGN, campaign.id)
        startActivity(campaignDetailIntent)
    }

    private fun signOut() {
        GoogleSignIn.getClient(
            this,
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()
        ).signOut()
        auth.signOut()
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
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
