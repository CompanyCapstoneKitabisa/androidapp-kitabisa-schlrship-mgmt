package com.kitabisa.scholarshipmanagement.ui.detailcampaign

import androidx.lifecycle.ViewModel
import com.kitabisa.scholarshipmanagement.data.DataRepository

class DetailCampaignViewModel(private val dataRepository: DataRepository) : ViewModel() {

    fun getCampaignDetail(token: String, id: String) = dataRepository.getCampaignDetail(token, id)

    fun getAllApplicant(token: String, id: String) = dataRepository.getAllApplicant(token, id)
}