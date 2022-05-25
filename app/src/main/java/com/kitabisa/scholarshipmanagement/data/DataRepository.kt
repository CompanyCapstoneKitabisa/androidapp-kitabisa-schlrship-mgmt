package com.kitabisa.scholarshipmanagement.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData

class DataRepository private constructor(private val apiService: ApiService) {

    suspend fun getDetailApplicant(token: String, id: String): DetailApplicantResponse {
        return apiService.getDetailApplicant(token, id)
    }

    companion object {
        @Volatile
        private var instance: DataRepository? = null
        fun getInstance(
            apiService: ApiService,
        ): DataRepository =
            instance ?: synchronized(this) {
                instance ?: DataRepository(apiService)
            }.also { instance = it }
    }

    fun getCampaign(token: String): LiveData<Resource<CampaignResponse>> = liveData {
        emit(Resource.Loading())
        try {
            val response = apiService.getCampaigns(token)
            emit(Resource.Success(response.body()))
        } catch (e: Exception) {
            Log.d("DataRepository", "data: ${e.message.toString()} ")
            emit(Resource.Error(e.message.toString()))
        }
    }

    fun getCampaignDetail(token: String, id: String): LiveData<Resource<CampaignDetailResponse>> = liveData {
        emit(Resource.Loading())
        try {
            val response = apiService.getCampaignDetail(token, id)
            emit(Resource.Success(response.body()))
        } catch (e: Exception) {
            Log.d("DataRepository", "data: ${e.message.toString()} ")
            emit(Resource.Error(e.message.toString()))
        }
    }

    fun getAllApplicant(token: String, id: String): LiveData<Resource<AllApplicantResponse>> = liveData {
        emit(Resource.Loading())
        try {
            val response = apiService.getAllApplicant(token, id)
            emit(Resource.Success(response.body()))
        } catch (e: Exception) {
            Log.d("DataRepository", "data: ${e.message.toString()} ")
            emit(Resource.Error(e.message.toString()))
        }
    }


}