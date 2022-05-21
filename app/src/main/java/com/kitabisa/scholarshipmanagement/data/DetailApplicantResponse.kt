package com.kitabisa.scholarshipmanagement.data

import com.google.gson.annotations.SerializedName

data class DetailApplicantResponse(

	@field:SerializedName("fetchedData")
	val fetchedData: FetchedData,

	@field:SerializedName("error")
	val error: Boolean,

	@field:SerializedName("message")
	val message: String
)

data class FetchedData(

	@field:SerializedName("provinsi")
	val provinsi: String,

	@field:SerializedName("statusKepemilikanRumah")
	val statusKepemilikanRumah: String,

	@field:SerializedName("notes")
	val notes: String,

	@field:SerializedName("IPK")
	val IPK: String,

	@field:SerializedName("KTM")
	val KTM: String,

	@field:SerializedName("university")
	val university: String,

	@field:SerializedName("KTP")
	val KTP: String,

	@field:SerializedName("lampiranDokumen")
	val lampiranDokumen: String,

	@field:SerializedName("kotaKabupaten")
	val kotaKabupaten: String,

	@field:SerializedName("pilihanBantuanBiaya")
	val pilihanBantuanBiaya: String,

	@field:SerializedName("fotoRumah")
	val fotoRumah: String,

	@field:SerializedName("ceritaKondisi")
	val ceritaKondisi: String,

	@field:SerializedName("ceritaKegiatan")
	val ceritaKegiatan: String,

	@field:SerializedName("kebutuhan2")
	val kebutuhan2: String,

	@field:SerializedName("id")
	val id: String,

	@field:SerializedName("kebutuhan1")
	val kebutuhan1: String,

	@field:SerializedName("email")
	val email: String,

	@field:SerializedName("buktiIPK")
	val buktiIPK: String,

	@field:SerializedName("jumlahBiayaUKT")
	val jumlahBiayaUKT: String,

	@field:SerializedName("noPonsel")
	val noPonsel: String,

	@field:SerializedName("angkatan")
	val angkatan: String,

	@field:SerializedName("sosmedAcc")
	val sosmedAcc: String,

	@field:SerializedName("IP")
	val IP: String,

	@field:SerializedName("photo")
	val photo: String,

	@field:SerializedName("jurusan")
	val jurusan: String,

	@field:SerializedName("fotoKegiatan")
	val fotoKegiatan: String,

	@field:SerializedName("kelurahan")
	val kelurahan: String,

	@field:SerializedName("alamat")
	val alamat: String,

	@field:SerializedName("NIK")
	val NIK: String,

	@field:SerializedName("biayaKebutuhan1")
	val biayaKebutuhan1: String,

	@field:SerializedName("NIM")
	val NIM: String,

	@field:SerializedName("biayaKebutuhan2")
	val biayaKebutuhan2: String,

	@field:SerializedName("deadlinePembayaran")
	val deadlinePembayaran: String,

	@field:SerializedName("buktiIP")
	val buktiIP: String,

	@field:SerializedName("name")
	val name: String,

	@field:SerializedName("kecamatan")
	val kecamatan: String,

	@field:SerializedName("ceritaSeberapaPenting")
	val ceritaSeberapaPenting: String
)
