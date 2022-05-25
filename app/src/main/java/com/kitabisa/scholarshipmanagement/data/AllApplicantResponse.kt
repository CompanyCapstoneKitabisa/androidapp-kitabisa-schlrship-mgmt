package com.kitabisa.scholarshipmanagement.data

import com.google.gson.annotations.SerializedName

data class AllApplicantResponse (

    @field:SerializedName("error")
    val error: Boolean,

    @field:SerializedName("message")
    val message: String,

    @field:SerializedName("listApplicants")
    val listApplicants: ArrayList<Applicant>
)

data class Applicant (

    @field:SerializedName("id")
    val id: String,

    @field:SerializedName("name")
    val name: String,

    @field:SerializedName("university")
    val university: String,

    @field:SerializedName("status")
    val status: String,

    @field:SerializedName("city")
    val city: String,

    @field:SerializedName("province")
    val province: String,

    @field:SerializedName("dataStatus")
    val dataStatus: String,

    @field:SerializedName("rumahStatus")
    val rumahStatus: String,

    @field:SerializedName("photoUrl")
    val photoUrl: String
)