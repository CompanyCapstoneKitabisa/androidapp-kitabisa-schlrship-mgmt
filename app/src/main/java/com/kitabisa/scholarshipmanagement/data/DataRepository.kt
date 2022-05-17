package com.kitabisa.scholarshipmanagement.data

class DataRepository private constructor(private val apiService: ApiService) {

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