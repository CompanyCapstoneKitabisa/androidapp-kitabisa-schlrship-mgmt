package com.kitabisa.scholarshipmanagement.ui.superadmin

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kitabisa.scholarshipmanagement.data.ApiService
import com.kitabisa.scholarshipmanagement.data.Campaign
import com.kitabisa.scholarshipmanagement.data.DataRepository
import com.kitabisa.scholarshipmanagement.data.NewCampaignBody
import com.kitabisa.scholarshipmanagement.ui.DataViewModelFactory

class AdminCampaignViewModel(private val dataRepository: DataRepository) : ViewModel() {

    fun getCampaign(token: String) = dataRepository.getCampaign(token)

    fun addCampaign(token: String, body: NewCampaignBody) = dataRepository.addCampaign(token, body)


}