package com.kitabisa.scholarshipmanagement.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.kitabisa.scholarshipmanagement.data.DataRepository
import com.kitabisa.scholarshipmanagement.di.Injection
import com.kitabisa.scholarshipmanagement.ui.detailapplicant.DetailApplicantViewModel
import com.kitabisa.scholarshipmanagement.ui.detailcampaign.DetailCampaignViewModel
import com.kitabisa.scholarshipmanagement.ui.home.HomeViewModel
import com.kitabisa.scholarshipmanagement.ui.superadmin.AdminCampaignViewModel

class DataViewModelFactory private constructor(private val dataRepository: DataRepository) :
    ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DetailApplicantViewModel::class.java)) {
            return DetailApplicantViewModel(dataRepository) as T
        } else if (modelClass.isAssignableFrom(DetailCampaignViewModel::class.java)) {
            return DetailCampaignViewModel(dataRepository) as T
        } else if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            return HomeViewModel(dataRepository) as T
        }else if (modelClass.isAssignableFrom(AdminCampaignViewModel::class.java)){
            return AdminCampaignViewModel(dataRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }

    companion object {
        @Volatile
        private var instance: DataViewModelFactory? = null
        fun getInstance(): DataViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: DataViewModelFactory(Injection.provideDataRepository())
            }.also { instance = it }
    }

}