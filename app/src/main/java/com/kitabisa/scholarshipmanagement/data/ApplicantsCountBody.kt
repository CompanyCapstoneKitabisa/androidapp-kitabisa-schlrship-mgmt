package com.kitabisa.scholarshipmanagement.data

import com.google.gson.annotations.SerializedName

data class ApplicantsCountBody (
    @field:SerializedName("applicantsCount")
    val applicantsCount: Int
)