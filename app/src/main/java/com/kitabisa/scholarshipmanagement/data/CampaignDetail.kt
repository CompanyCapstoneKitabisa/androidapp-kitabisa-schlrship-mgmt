package com.kitabisa.scholarshipmanagement.data

import com.google.gson.annotations.SerializedName

data class CampaignDetail(
    @field:SerializedName("name")
    val name: String?,

    @field:SerializedName("penggalangDana")
    val penggalangDana: String,

    @field:SerializedName("photoUrl")
    val photoUrl: String,

    @field:SerializedName("applicantsCount")
    val applicantsCount: Int,

    @field:SerializedName("acceptedApplicants")
    val acceptedApplicants: Int,

    @field:SerializedName("rejectedApplicants")
    val rejectedApplicants: Int
)