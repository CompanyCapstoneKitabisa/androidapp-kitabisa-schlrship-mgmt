package com.kitabisa.scholarshipmanagement.data

import com.google.gson.annotations.SerializedName

data class DownloadFileResponse(

	@field:SerializedName("fileDownload")
	val fileDownload: List<String>,

	@field:SerializedName("error")
	val error: Boolean
)
