package com.kitabisa.scholarshipmanagement.data

import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

data class Campaign(
    @PrimaryKey
    @field:SerializedName("id")
    val id: String,

    @field:SerializedName("name")
    val name: String?,

    @field:SerializedName("penggalangDana")
    val penggalangDana: String,

    @field:SerializedName("photoUrl")
    val photoUrl: String
)