package com.kitabisa.scholarshipmanagement.ui.detailapplicant

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kitabisa.scholarshipmanagement.data.DataRepository
import com.kitabisa.scholarshipmanagement.data.FetchedData
import com.kitabisa.scholarshipmanagement.data.Result
import kotlinx.coroutines.launch
import java.lang.Exception

class DetailApplicantViewModel(private val dataRepository: DataRepository) : ViewModel() {

    val _fetchedData = MutableLiveData<Result<FetchedData>>()
    val fetchedData: LiveData<Result<FetchedData>> = _fetchedData

    fun getDetailApplicant(id: String) {
        _fetchedData.postValue(Result.Loading)
        viewModelScope.launch {
            try {
                val tempData = dataRepository.getDetailApplicant(id).fetchedData
                _fetchedData.postValue(Result.Success(tempData))
            } catch (e: Exception) {
                _fetchedData.postValue(Result.Error(e.message.toString()))
            }
        }
    }

}