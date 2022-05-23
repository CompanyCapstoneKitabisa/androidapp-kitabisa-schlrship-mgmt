package com.kitabisa.scholarshipmanagement.data

import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface ApiService {
    @GET("applicants")
    suspend fun getDetailApplicant(@Header("authToken") token: String, @Query("id") id: String): DetailApplicantResponse
}