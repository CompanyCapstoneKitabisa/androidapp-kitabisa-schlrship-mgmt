package com.kitabisa.scholarshipmanagement.data

import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

data class CampaignResponse(

    @field:SerializedName("error")
    val error: Boolean,

    @field:SerializedName("message")
    val message: String,

    @field:SerializedName("listCampaign")
    val listCampaign: ArrayList<Campaign>
)

data class Campaign(
    @PrimaryKey
    @field:SerializedName("id")
    val id: String,

    @field:SerializedName("name")
    val name: String,

    @field:SerializedName("penggalangDana")
    val penggalangDana: String,

    @field:SerializedName("photoUrl")
    val photoUrl: String
)