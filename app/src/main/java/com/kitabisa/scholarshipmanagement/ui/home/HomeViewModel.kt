package com.kitabisa.scholarshipmanagement.ui.home

import androidx.lifecycle.ViewModel
import com.kitabisa.scholarshipmanagement.data.DataRepository

class HomeViewModel(private val dataRepository: DataRepository) : ViewModel() {

    fun getCampaign(token: String) = dataRepository.getCampaign(token)

}