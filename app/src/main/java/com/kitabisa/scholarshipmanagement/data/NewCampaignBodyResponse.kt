package com.kitabisa.scholarshipmanagement.data

import com.google.gson.annotations.SerializedName

data class NewCampaignBodyResponse(

	@field:SerializedName("error")
	val error: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null
)
