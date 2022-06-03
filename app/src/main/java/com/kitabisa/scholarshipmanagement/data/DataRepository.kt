package com.kitabisa.scholarshipmanagement.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData

class DataRepository private constructor(private val apiService: ApiService) {

    fun getDetailApplicant(
        token: String,
        id: String
    ): LiveData<Resource<DetailApplicantResponse>> = liveData {
        emit(Resource.Loading())
        try {
            val response = apiService.getDetailApplicant(token, id)
            if (response.isSuccessful) {
                emit(Resource.Success(response.body()))
            } else {
                emit(Resource.Error("Session Expired, You can Re-Login"))
            }
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
            if (response.isSuccessful) {
                emit(Resource.Success(response.body()))
            } else {
                emit(Resource.Error("Session Expired, You can Re-Login"))
            }
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
        try {
            val response = apiService.addCampaign(token, body)
            if (response.isSuccessful) {
                emit(Resource.Success(response.body()))
            } else {
                emit(Resource.Error("Response Code : ${response.code()}"))
            }
        } catch (e: Exception) {
            Log.d("DataRepository", "data: ${e.message.toString()} ")
            emit(Resource.Error(e.message.toString()))
        }
    }

    fun getCampaign(token: String): LiveData<Resource<CampaignResponse>> = liveData {
        emit(Resource.Loading())
        try {
            val response = apiService.getCampaigns(token)
            if (response.isSuccessful) {
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
                if (response.isSuccessful) {
                    emit(Resource.Success(response.body()))
                } else {
                    emit(Resource.Error("Session Expired, You can Re-Login"))
                }
            } catch (e: Exception) {
                Log.d("DataRepository", "data: ${e.message.toString()} ")
                emit(Resource.Error(e.message.toString()))
            }
        }

    fun getAllApplicant(
        options: Map<String, String>,
        authToken: String,
        id: String
    ): LiveData<PagingData<ListApplicantsItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            pagingSourceFactory = {
                ApplicantPagingSource(apiService, options, authToken, id)
            }
        ).liveData
    }

    fun triggerDataProcess(token: String, id: String): LiveData<Resource<TriggerProcessResponse>> =
        liveData {
            emit(Resource.Loading())
            try {
                val response = apiService.triggerDataProcess(token, id)
                if (response.isSuccessful) {
                    emit(Resource.Success(response.body()))
                } else if (response.code() == 404) {
                    emit(Resource.Error("ID Sheet Not Found, 404"))
                } else if (response.code() == 403) {
                    emit(Resource.Error("Session Expired, You can Re-Login, 403"))
                }
            } catch (e: Exception) {
                Log.d("DataRepository", "data: ${e.message.toString()} ")
                emit(Resource.Error(e.message.toString()))
            }
        }

    fun triggerPagingData(token: String, id: String): LiveData<Resource<TriggerProcessResponse>> =
        liveData {
            emit(Resource.Loading())
            try {
                val response = apiService.triggerPagingData(token, id)
                if (response.isSuccessful) {
                    emit(Resource.Success(response.body()))
                } else {
                    emit(Resource.Error("Response Code : ${response.code()}"))
                }
            } catch (e: Exception) {
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

}