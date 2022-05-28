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

    @field:SerializedName("statusApplicant")
    val status: String,

    @field:SerializedName("kota")
    val city: String,

    @field:SerializedName("provinsi")
    val province: String,

    @field:SerializedName("statusData")
    val dataStatus: String,

    @field:SerializedName("statusRumah")
    val rumahStatus: String,

    @field:SerializedName("photoURL")
    val photoUrl: String
)