package com.kitabisa.scholarshipmanagement.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData

class DataRepository private constructor(private val apiService: ApiService) {

    fun getDetailApplicant(
        token: String,
        id: String
    ): LiveData<Resource<DetailApplicantResponse>> = liveData {
        emit(Resource.Loading())
        try {
            val response = apiService.getDetailApplicant(token, id)
            emit(Resource.Success(response.body()))
        } catch (e: Exception) {
            Log.d("DataRepository", "data: ${e.message.toString()} ")
            emit(Resource.Error(e.message.toString()))
        }
    }

    fun setApplicantStatus(
        token: String,
        id: String,
        updateApplicantStatusBody: UpdateApplicantStatusBody
    ): LiveData<Resource<UpdateStatusResponse>> = liveData {
        emit(Resource.Loading())
        try {
            val response = apiService.setApplicantStatus(token, id, updateApplicantStatusBody)
            emit(Resource.Success(response.body()))
        } catch (e: Exception) {
            Log.d("DataRepository", "data: ${e.message.toString()} ")
            emit(Resource.Error(e.message.toString()))
        }
    }

    fun addCampaign(
        token: String,
        body: NewCampaignBody
    ): LiveData<Resource<NewCampaignBodyResponse>> = liveData {
        emit(Resource.Loading())
        try{
            val response = apiService.addCampaign(token, body)
            emit(Resource.Success(response.body()))
        }catch (e: Exception){
            Log.d("DataRepository", "data: ${e.message.toString()} ")
            emit(Resource.Error(e.message.toString()))
        }
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
            if(response.isSuccessful) {
                emit(Resource.Success(response.body()))
            } else {
                emit(Resource.Error("Session Expired, You can Re-Login"))
            }
        } catch (e: Exception) {
            Log.d("DataRepository", "data: ${e.message.toString()} ")
            emit(Resource.Error(e.message.toString()))
        }
    }

    fun getCampaignDetail(token: String, id: String): LiveData<Resource<CampaignDetailResponse>> =
        liveData {
            emit(Resource.Loading())
            try {
                val response = apiService.getCampaignDetail(token, id)
                emit(Resource.Success(response.body()))
            } catch (e: Exception) {
                Log.d("DataRepository", "data: ${e.message.toString()} ")
                emit(Resource.Error(e.message.toString()))
            }
        }

    fun getAllApplicant(token: String, id: String): LiveData<Resource<AllApplicantResponse>> =
        liveData {
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