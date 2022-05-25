package com.kitabisa.scholarshipmanagement.data

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @GET("campaigns")
    suspend fun getCampaigns(): Response<CampaignResponse>

    @GET("campaigns/{id}")
    suspend fun getCampaignDetail(
        @Path("id") id: String,
    ): Response<CampaignDetailResponse>
}