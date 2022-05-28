package com.kitabisa.scholarshipmanagement.ui.detailapplicant

import androidx.lifecycle.ViewModel
import com.kitabisa.scholarshipmanagement.data.DataRepository
import com.kitabisa.scholarshipmanagement.data.UpdateApplicantStatusBody


class DetailApplicantViewModel(private val dataRepository: DataRepository) : ViewModel() {

    fun getDetailApplicant(token: String, id: String) = dataRepository.getDetailApplicant(token, id)

    fun setApplicantStatus(
        token: String,
        id: String,
        updateApplicantStatusBody: UpdateApplicantStatusBody
    ) = dataRepository.setApplicantStatus(token, id, updateApplicantStatusBody)
}