package com.kitabisa.scholarshipmanagement.data

import com.google.gson.annotations.SerializedName

data class CampaignDetailResponse(

    @field:SerializedName("error")
    val error: Boolean,

    @field:SerializedName("message")
    val message: String,

    @field:SerializedName("Data")
    val Data: CampaignDetail
)

data class CampaignDetail(
    @field:SerializedName("name")
    val name: String,

    @field:SerializedName("penggalangDana")
    val penggalangDana: String,

    @field:SerializedName("photoUrl")
    val photoUrl: String,

    @field:SerializedName("processData")
    val processData: String,

    @field:SerializedName("processPageNumber")
    val processPageNumber: String,

    @field:SerializedName("pendingApplicants")
    val pendingApplicants: Int,

    @field:SerializedName("acceptedApplicants")
    val acceptedApplicants: Int,

    @field:SerializedName("rejectedApplicants")
    val rejectedApplicants: Int,

    @field:SerializedName("onHoldApplicants")
    val onHoldApplicants: Int
)