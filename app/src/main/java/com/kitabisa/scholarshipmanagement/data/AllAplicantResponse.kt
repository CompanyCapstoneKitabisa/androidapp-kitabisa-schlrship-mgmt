package com.kitabisa.scholarshipmanagement.data

import com.google.gson.annotations.SerializedName

data class AllAplicantResponse(

	@field:SerializedName("campaign")
	val campaign: String,

	@field:SerializedName("listApplicants")
	val listApplicants: ArrayList<ListApplicantsItem>,

	@field:SerializedName("error")
	val error: Boolean,

	@field:SerializedName("message")
	val message: String
)

data class Score(

	@field:SerializedName("scoreNIK")
	val scoreNIK: Int,

	@field:SerializedName("total")
	val total: Int,

	@field:SerializedName("scorePenting")
	val scorePenting: Int,

	@field:SerializedName("scoreKota")
	val scoreKota: Int,

	@field:SerializedName("scoreMedsos")
	val scoreMedsos: Int,

	@field:SerializedName("scoreKepemilikanRumah")
	val scoreKepemilikanRumah: Int,

	@field:SerializedName("scorePerjuangan")
	val scorePerjuangan: Int,

	@field:SerializedName("scoreKegiatan")
	val scoreKegiatan: Int,

	@field:SerializedName("scoreDana")
	val scoreDana: Int,

	@field:SerializedName("scoreLatarBelakang")
	val scoreLatarBelakang: Int,

	@field:SerializedName("scoreProvinsi")
	val scoreProvinsi: Int,

	@field:SerializedName("scoreRumah")
	val scoreRumah: Int
)

data class ListApplicantsItem(

	@field:SerializedName("provinsi")
	val provinsi: String,

	@field:SerializedName("photoURL")
	val photoURL: String,

	@field:SerializedName("score")
	val score: Score,

	@field:SerializedName("kota")
	val kota: String,

	@field:SerializedName("statusRumah")
	val statusRumah: String,

	@field:SerializedName("university")
	val university: String,

	@field:SerializedName("statusApplicant")
	val statusApplicant: String,

	@field:SerializedName("name")
	val name: String,

	@field:SerializedName("id")
	val id: String,

	@field:SerializedName("statusData")
	val statusData: String
)
