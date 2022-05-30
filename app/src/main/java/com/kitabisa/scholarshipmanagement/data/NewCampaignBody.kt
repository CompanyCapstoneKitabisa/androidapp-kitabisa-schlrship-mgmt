package com.kitabisa.scholarshipmanagement.data

import com.google.gson.annotations.SerializedName

data class NewCampaignBody(
    @field:SerializedName("namaBeasiswa")
    val namaBeasiswa: String,

    @field:SerializedName("penggalangDana")
    val pengalangDana: String,

    @field:SerializedName("SnK")
    val SnK: String,

    @field:SerializedName("idGSheet")
    val idGSheet: String,

    @field:SerializedName("photoUrl")
    val photoUrl: String
    )

