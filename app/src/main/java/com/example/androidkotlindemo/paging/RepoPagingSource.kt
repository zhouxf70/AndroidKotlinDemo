package com.example.androidkotlindemo.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.androidkotlindemo.network.ApiService
import com.example.androidkotlindemo.network.bean.Repo

/**
 * Created by zxf on 2021/6/30
 */
class RepoPagingSource(private val apiService: ApiService) : PagingSource<Int, Repo>() {

    override fun getRefreshKey(state: PagingState<Int, Repo>): Int? = null

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Repo> {
        val page = params.key ?: 1
        val repos = apiService.searchRepos("Android", page, 10)
        val prePage = if (page > 1) page else null
        val nextPage = if (repos.items.isEmpty()) null else page + 1
        return LoadResult.Page(repos.items, prePage, nextPage)
    }
}