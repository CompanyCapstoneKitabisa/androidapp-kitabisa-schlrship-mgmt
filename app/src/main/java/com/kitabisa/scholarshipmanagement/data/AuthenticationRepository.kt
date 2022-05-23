package com.kitabisa.scholarshipmanagement.data

class AuthenticationRepository private constructor(private val apiService: ApiService) {


    companion object {
        @Volatile
        private var instance: AuthenticationRepository? = null
        fun getInstance(
            apiService: ApiService,
        ): AuthenticationRepository =
            instance ?: synchronized(this) {
                instance ?: AuthenticationRepository(apiService)
            }.also { instance = it }
    }
}