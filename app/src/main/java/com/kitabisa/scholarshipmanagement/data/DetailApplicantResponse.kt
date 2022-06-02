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

data class MotivationLetter(

    @field:SerializedName("ceritaPerjuangan")
    val ceritaPerjuangan: String,

    @field:SerializedName("fotoBuktiKegiatan")
    val fotoBuktiKegiatan: String,

    @field:SerializedName("ceritaLatarBelakang")
    val ceritaLatarBelakang: String,

    @field:SerializedName("ceritaPentingnyaBeasiswa")
    val ceritaPentingnyaBeasiswa: String,

    @field:SerializedName("ceritakegiatanYangDigeluti")
    val ceritakegiatanYangDigeluti: String
)

data class BioPendidikan(

    @field:SerializedName("tingkatPendidikan")
    val tingkatPendidikan: String,

    @field:SerializedName("NIM")
    val NIM: String,

    @field:SerializedName("NPSN")
    val NPSN: String,

    @field:SerializedName("jurusan")
    val jurusan: String,

    @field:SerializedName("fotoKTM")
    val fotoKTM: String,

    @field:SerializedName("fotoIPKAtauRapor")
    val fotoIPKAtauRapor: String
)

data class BioDiri(

    @field:SerializedName("provinsi")
    val provinsi: String,

    @field:SerializedName("NIK")
    val NIK: String,

    @field:SerializedName("nama")
    val nama: String,

    @field:SerializedName("sosmedAcc")
    val sosmedAcc: String,

    @field:SerializedName("noTlp")
    val noTlp: String,

    @field:SerializedName("fotoKTP")
    val fotoKTP: String,

    @field:SerializedName("fotoDiri")
    val fotoDiri: String,

    @field:SerializedName("kotaKabupaten")
    val kotaKabupaten: String,

    @field:SerializedName("alamat")
    val alamat: String
)

data class Misc(

    @field:SerializedName("beasiswaTerdaftar")
    val beasiswaTerdaftar: String
)

data class FetchedData(

    @field:SerializedName("motivationLetter")
    val motivationLetter: MotivationLetter,

    @field:SerializedName("notes")
    val notes: String,

    @field:SerializedName("lampiranTambahan")
    val lampiranTambahan: String,

    @field:SerializedName("statusRumah")
    val statusRumah: String,

    @field:SerializedName("statusApplicant")
    val statusApplicant: String,

    @field:SerializedName("bioDiri")
    val bioDiri: BioDiri,

    @field:SerializedName("reviewer")
    val reviewer: String,

    @field:SerializedName("bioPendidikan")
    val bioPendidikan: BioPendidikan,

    @field:SerializedName("statusData")
    val statusData: String,

    @field:SerializedName("lembarPersetujuan")
    val lembarPersetujuan: String,

    @field:SerializedName("pengajuanBantuan")
    val pengajuanBantuan: PengajuanBantuan,

    @field:SerializedName("misc")
    val misc: Misc,

    @field:SerializedName("scoreApplicant")
    val scoreApplicant: ScoreApplicant
)

data class PengajuanBantuan(

    @field:SerializedName("fotoBuktiTunggakan")
    val fotoBuktiTunggakan: String,

    @field:SerializedName("fotoRumah")
    val fotoRumah: String,

    @field:SerializedName("kebutuhan")
    val kebutuhan: String,

    @field:SerializedName("totalBiaya")
    val totalBiaya: String,

    @field:SerializedName("kepemilikanRumah")
    val kepemilikanRumah: String,

    @field:SerializedName("ceritaPenggunaanDana")
    val ceritaPenggunaanDana: String
)

data class ScoreApplicant(
    @field:SerializedName("total")
    val total: Int,

    @field:SerializedName("scoreRumah")
    val scoreRumah: Int,

    @field:SerializedName("scoreProvinsi")
    val scoreProvinsi: Int,

    @field:SerializedName("scorePerjuangan")
    val scorePerjuangan: Int,

    @field:SerializedName("scoreNIK")
    val scoreNIK: Int,

    @field:SerializedName("scoreMedsos")
    val scoreMedsos: Int,

    @field:SerializedName("scoreLatarBelakang")
    val scoreLatarBelakang: Int,

    @field:SerializedName("scoreKota")
    val scoreKota: Int,

    @field:SerializedName("scoreKegiatan")
    val scoreKegiatan: Int,

    @field:SerializedName("scoreDana")
    val scoreDana: Int,
)
