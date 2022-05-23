package com.kitabisa.scholarshipmanagement.data

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
}