package com.kitabisa.scholarshipmanagement.data

import com.google.gson.annotations.SerializedName

data class ApplicantsCountBody (
    @field:SerializedName("totalApplicant")
    val totalApplicant: Int
)