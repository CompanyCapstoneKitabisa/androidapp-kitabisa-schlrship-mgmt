package com.kitabisa.scholarshipmanagement.ui.detailcampaign

import androidx.lifecycle.ViewModel
import com.kitabisa.scholarshipmanagement.data.DataRepository

class DetailCampaignViewModel(private val dataRepository: DataRepository) : ViewModel() {

    fun getCampaignDetail(id: String) = dataRepository.getCampaignDetail(id)
}