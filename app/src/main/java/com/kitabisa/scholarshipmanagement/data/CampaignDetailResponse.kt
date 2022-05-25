package com.kitabisa.scholarshipmanagement.data

import com.google.gson.annotations.SerializedName

data class CampaignDetailResponse (

    @field:SerializedName("error")
    val error: Boolean,

    @field:SerializedName("message")
    val message: String,

    @field:SerializedName("Data")
    val Data: CampaignDetail
)