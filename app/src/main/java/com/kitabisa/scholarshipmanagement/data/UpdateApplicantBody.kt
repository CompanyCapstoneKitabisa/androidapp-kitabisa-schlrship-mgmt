package com.kitabisa.scholarshipmanagement.data

import com.google.gson.annotations.SerializedName

data class UpdateApplicantStatusBody(
    @field:SerializedName("reviewer")
    val reviewer: String,

    @field:SerializedName("status")
    val status: String,

    @field:SerializedName("notes")
    val notes: String,

    @field:SerializedName("currStatus")
    val currStatus: String,

    @field:SerializedName("idCampaign")
    val idCampaign: String
)
