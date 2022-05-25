package com.kitabisa.scholarshipmanagement.ui.home

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import com.kitabisa.scholarshipmanagement.R
import com.kitabisa.scholarshipmanagement.ui.DataViewModelFactory
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.kitabisa.scholarshipmanagement.data.Campaign
import com.kitabisa.scholarshipmanagement.data.Resource
import com.kitabisa.scholarshipmanagement.databinding.ActivityHomeBinding
import com.kitabisa.scholarshipmanagement.ui.detailcampaign.DetailCampaignActivity

class HomeActivity : AppCompatActivity(), CampaignAdapter.CampaignCallback {

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
}