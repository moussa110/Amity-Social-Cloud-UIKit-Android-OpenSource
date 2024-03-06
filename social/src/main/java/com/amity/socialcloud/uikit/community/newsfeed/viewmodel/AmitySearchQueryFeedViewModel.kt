package com.amity.socialcloud.uikit.community.newsfeed.viewmodel

import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingData
import androidx.paging.map
import com.amity.socialcloud.sdk.model.social.post.AmityPost
import com.amity.socialcloud.uikit.community.contentsearch.ContentSearchController
import com.amity.socialcloud.uikit.community.contentsearch.data.ContentSearchRequest
import com.amity.socialcloud.uikit.community.newsfeed.model.AmityBasePostHeaderItem
import com.amity.socialcloud.uikit.community.newsfeed.model.AmityBasePostItem
import com.amity.socialcloud.uikit.social.AmitySocialUISettings
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.schedulers.Schedulers

class AmitySearchQueryFeedViewModel : AmityFeedViewModel() {
	lateinit var query: String
	private val controller = ContentSearchController()

	@ExperimentalPagingApi
	override fun getFeed(onPageLoaded: (posts: PagingData<AmityBasePostItem>) -> Unit): Completable {
		return controller.getPosts(createRequest())
			.subscribeOn(Schedulers.io())
			.observeOn(AndroidSchedulers.mainThread())
			.map { posts ->
				posts.map { createPostItem(it) }
			}.doOnNext {
				onPageLoaded.invoke(it)
			}.ignoreElements()
	}

	private fun createRequest(): ContentSearchRequest {
		ContentSearchRequest.Query().also { query ->
			query.publicSearch = true
			query.text = this@AmitySearchQueryFeedViewModel.query
			query.haghtagList = listOf(this@AmitySearchQueryFeedViewModel.query)
			return ContentSearchRequest(query)
		}
	}

	override fun createPostHeaderItems(post: AmityPost): List<AmityBasePostHeaderItem> {
		val showHeader = AmitySocialUISettings.getViewHolder(getDataType(post)).enableHeader()
		return if (showHeader) {
			listOf(AmityBasePostHeaderItem(post = post, showTarget = true))
		} else {
			emptyList()
		}
	}
}