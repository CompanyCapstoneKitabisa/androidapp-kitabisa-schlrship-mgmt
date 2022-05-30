package com.kitabisa.scholarshipmanagement.di

import com.kitabisa.scholarshipmanagement.data.ApiConfig
import com.kitabisa.scholarshipmanagement.data.AuthenticationRepository
import com.kitabisa.scholarshipmanagement.data.DataRepository

object Injection {
    fun provideAuthenRepository(): AuthenticationRepository {
        val apiService = ApiConfig.getApiService()
        return AuthenticationRepository.getInstance(apiService)
    }

    fun provideDataRepository(): DataRepository {
        val apiService = ApiConfig.getApiService()
        return DataRepository.getInstance(apiService)
    }
}