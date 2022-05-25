package com.kitabisa.scholarshipmanagement.data

import com.google.gson.annotations.SerializedName

data class CampaignResponse (

    @field:SerializedName("error")
    val error: Boolean,

    @field:SerializedName("message")
    val message: String,

    @field:SerializedName("listCampaign")
    val listCampaign: ArrayList<Campaign>
)