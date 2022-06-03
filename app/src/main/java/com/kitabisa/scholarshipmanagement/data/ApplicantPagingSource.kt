package com.kitabisa.scholarshipmanagement.data

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState

class ApplicantPagingSource(
    private val apiService: ApiService,
    private val options: Map<String, String>,
    private val authToken: String,
    private val id: String
) : PagingSource<Int, ListApplicantsItem>() {

    private companion object {
        const val INITIAL_PAGE_INDEX = 1
    }

    override fun getRefreshKey(state: PagingState<Int, ListApplicantsItem>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ListApplicantsItem> {
        return try {
            Log.v("di jyo load paging", id)
            val position = params.key ?: INITIAL_PAGE_INDEX
            val responseData = apiService.getAllApplicant(authToken, id, position, options)
            Log.v("di jyo load respon", responseData.toString())
            LoadResult.Page(
                data = responseData.body()!!.listApplicants,
                prevKey = if (position == INITIAL_PAGE_INDEX) null else position - 1,
                nextKey = if (responseData.body()!!.listApplicants.isNullOrEmpty()) null else position + 1
            )
        } catch (exception: Exception) {
            Log.v("di jyo error", exception.toString())
            return LoadResult.Error(exception)
        }
    }

}