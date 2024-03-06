/*
package com.amity.socialcloud.uikit.community.contentsearch

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.amity.socialcloud.uikit.community.contentsearch.data.ContentSearchResponse
import com.amity.socialcloud.uikit.community.contentsearch.network.ContentSearchWebServices
import com.bumptech.glide.load.HttpException
import java.io.IOException

private const val STARTING_PAGE_INDEX = 1


class PostsPagingSource(
	private val service: ContentSearchWebServices
) : PagingSource<Int, ContentSearchResponse>() {


	override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ContentSearchResponse> {
		val pageIndex = params.key ?: STARTING_PAGE_INDEX
		return try {
			val response = service.getTopRatedMovies(
				language = "en-US",
				page = pageIndex
			)
			val movies = response.results
			val nextKey =
				if (movies.isEmpty()) {
					null
				} else {
					// By default, initial load size = 3 * NETWORK PAGE SIZE
					// ensure we're not requesting duplicating items at the 2nd request
					pageIndex + (params.loadSize / NETWORK_PAGE_SIZE)
				}
			LoadResult.Page(
				data = movies,
				prevKey = if (pageIndex == TMDB_STARTING_PAGE_INDEX) null else pageIndex,
				nextKey = nextKey
			)
		} catch (exception: IOException) {
			return LoadResult.Error(exception)
		} catch (exception: HttpException) {
			return LoadResult.Error(exception)
		}
	}

	*/
/**
	 * The refresh key is used for subsequent calls to PagingSource.Load after the initial load.
	 *//*

	override fun getRefreshKey(state: PagingState<Int, MovieResponse>): Int? {
		// We need to get the previous key (or next key if previous is null) of the page
		// that was closest to the most recently accessed index.
		// Anchor position is the most recently accessed index.
		return state.anchorPosition?.let { anchorPosition ->
			state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
				?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
		}
	}
}*/
