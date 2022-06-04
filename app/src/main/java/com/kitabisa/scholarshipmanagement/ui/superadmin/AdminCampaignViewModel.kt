package com.kitabisa.scholarshipmanagement.ui.superadmin

import androidx.lifecycle.ViewModel
import com.kitabisa.scholarshipmanagement.data.DataRepository
import com.kitabisa.scholarshipmanagement.data.NewCampaignBody

class AdminCampaignViewModel(private val dataRepository: DataRepository) : ViewModel() {

    fun getCampaign(token: String) = dataRepository.getCampaign(token)

    fun addCampaign(token: String, body: NewCampaignBody) = dataRepository.addCampaign(token, body)

    fun triggerDataProcess(token: String, id: String) = dataRepository.triggerDataProcess(token, id, 0)

    fun triggerPagingData(token: String, id: String) = dataRepository.triggerPagingData(token, id)
}