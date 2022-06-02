package com.kitabisa.scholarshipmanagement.data

import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    @GET("campaigns")
    suspend fun getCampaigns(@Header("authToken") token: String): Response<CampaignResponse>

    @GET("campaigns/{id}")
    suspend fun getCampaignDetail(
        @Header("authToken") token: String,
        @Path("id") id: String,
    ): Response<CampaignDetailResponse>

    @GET("campaigns/{id}/applicants")
    suspend fun getAllApplicant(
        @Header("authToken") token: String,
        @Path("id") id: String,
        @Query("page") page: Int,
        @Query("status") status: String,
        @Query("nama") nama: String,
        @Query("provinsi") provinsi: String,
        @Query("statusRumah") statusRumah: String,
        @Query("statusData") statusData: String,
    ): Response<AllAplicantResponse>

    @GET("applicants")
    suspend fun getDetailApplicant(
        @Header("authToken") token: String,
        @Query("id") id: String
    ): Response<DetailApplicantResponse>

    @POST("/applicants/{id}/update")
    suspend fun setApplicantStatus(
        @Header("authToken") token: String,
        @Path("id") id: String,
        @Body updateApplicantStatusBody: UpdateApplicantStatusBody
    ): Response<UpdateStatusResponse>

    @POST("/campaigns")
    suspend fun addCampaign(
        @Header("authToken") token: String,
        @Body NewCampaignBody: NewCampaignBody
    ): Response<NewCampaignBodyResponse>

    @GET("campaigns/{id}/applicants/processData")
    suspend fun triggerDataProcess(
        @Header("authToken") token: String,
        @Path("id") id: String,
    ): Response<TriggerProcessResponse>

}