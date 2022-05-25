package com.kitabisa.scholarshipmanagement.ui.detailcampaign

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.annotation.StringRes
import androidx.recyclerview.widget.LinearLayoutManager
import com.kitabisa.scholarshipmanagement.R
import com.kitabisa.scholarshipmanagement.data.Resource
import com.kitabisa.scholarshipmanagement.databinding.ActivityDetailCampaignBinding
import com.kitabisa.scholarshipmanagement.databinding.ActivityHomeBinding
import com.kitabisa.scholarshipmanagement.ui.DataViewModelFactory
import com.kitabisa.scholarshipmanagement.ui.home.HomeViewModel

class DetailCampaignActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailCampaignBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailCampaignBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        val factory: DataViewModelFactory = DataViewModelFactory.getInstance()
        val detailCampaignViewModel: DetailCampaignViewModel by viewModels {
            factory
        }

        detailCampaignViewModel.getCampaignDetail(ID).observe(this) { result ->
            if (result != null) {
                when (result) {
                    is Resource.Success -> {
                        Log.v("Test", result.data?.Data.toString())
                    }
                    is Resource.Error -> {
                        Log.v("Test", "Gagal Mendapatkan Data Detail Campaign\"")
                    }
                    else -> {}
                }
            }
        }
    }

    companion object {
        const val ID = "id"
    }
}