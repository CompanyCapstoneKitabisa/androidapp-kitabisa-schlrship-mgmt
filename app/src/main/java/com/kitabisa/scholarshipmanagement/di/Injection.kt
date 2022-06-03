package com.kitabisa.scholarshipmanagement.di

import com.kitabisa.scholarshipmanagement.data.ApiConfig
import com.kitabisa.scholarshipmanagement.data.DataRepository

object Injection {

    fun provideDataRepository(): DataRepository {
        val apiService = ApiConfig.getApiService()
        return DataRepository.getInstance(apiService)
    }
}