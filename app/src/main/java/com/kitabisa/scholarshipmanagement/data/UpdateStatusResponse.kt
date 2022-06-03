package com.kitabisa.scholarshipmanagement.data

import com.google.gson.annotations.SerializedName

data class UpdateStatusResponse(

    @field:SerializedName("error")
    val error: Boolean,

    @field:SerializedName("message")
    val message: Message
)

data class Message(

    @field:SerializedName("statusApplicant")
    val statusApplicant: String
)
