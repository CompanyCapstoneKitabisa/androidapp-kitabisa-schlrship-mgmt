package com.kitabisa.scholarshipmanagement.ui.detailcampaign

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.kitabisa.scholarshipmanagement.data.DataRepository
import com.kitabisa.scholarshipmanagement.data.ListApplicantsItem
import kotlinx.coroutines.runBlocking

class DetailCampaignViewModel(private val dataRepository: DataRepository) : ViewModel() {

    fun getCampaignDetail(token: String, id: String) = dataRepository.getCampaignDetail(token, id)

    fun getAllApplicant(
        status: String,
        nama: String,
        provinsi: String,
        statusRumah: String,
        statusData: String,
        token: String,
        id: String): LiveData<PagingData<ListApplicantsItem>> {
        return dataRepository.getAllApplicant(status, nama, provinsi, statusRumah, statusData, token, id).cachedIn(viewModelScope)
    }

}