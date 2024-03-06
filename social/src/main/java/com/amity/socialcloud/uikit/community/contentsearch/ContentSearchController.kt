package com.amity.socialcloud.uikit.community.contentsearch

import androidx.paging.PagingData
import com.amity.socialcloud.sdk.api.core.AmityCoreClient
import com.amity.socialcloud.sdk.api.core.endpoint.AmityEndpoint
import com.amity.socialcloud.sdk.api.core.token.AmityUserTokenManager
import com.amity.socialcloud.sdk.api.social.AmitySocialClient
import com.amity.socialcloud.sdk.model.core.session.AmityUserToken
import com.amity.socialcloud.sdk.model.core.user.AmityUser
import com.amity.socialcloud.sdk.model.social.post.AmityPost
import com.amity.socialcloud.uikit.common.BuildConfig
import com.amity.socialcloud.uikit.community.contentsearch.data.ContentSearchRequest
import com.amity.socialcloud.uikit.community.contentsearch.data.ContentSearchResponse
import com.amity.socialcloud.uikit.community.contentsearch.network.ContentSearchApi
import io.reactivex.rxjava3.core.Flowable
import org.reactivestreams.Publisher

class ContentSearchController {
	fun getPosts(request: ContentSearchRequest): Flowable<PagingData<AmityPost>> {
		return getSearchIdsResponse(request).flatMap {
			getPostsFromIds(it.postIds.toSet())
		}.map {
			createPagerFromList(it)
		}
	}

	private fun createPagerFromList(myItemList: List<AmityPost>): PagingData<AmityPost> {
		return PagingData.from(myItemList)
	}

	private fun getPostsFromIds(ids: Set<String>): Flowable<List<AmityPost>> = AmitySocialClient.newPostRepository().getPostByIds(ids)


	private fun getSearchIdsResponse(request: ContentSearchRequest): Flowable<ContentSearchResponse> {
		return getTokenFromUser().flatMap {
			getResponse(request, it.accessToken)
		}
	}

	private fun getResponse(request: ContentSearchRequest, token: String) = ContentSearchApi.getContentSearchWebServices().search(request, "Bearer $token")

	private fun getTokenFromUser(): Flowable<AmityUserToken> {
		return getUserData().flatMap {
			createUserToken(it)
		}
	}

	private fun createUserToken(user: AmityUser): Publisher<out AmityUserToken> {
		return AmityUserTokenManager(apiKey = BuildConfig.AMITY_API_KEY,
			endpoint = AmityEndpoint.EU).createUserToken(userId = user.getUserId(),
			displayName = user.getDisplayName(),
			secureToken = null).toFlowable()
	}


	private fun getUserData(): Flowable<AmityUser> {
		return AmityCoreClient.newUserRepository().getCurrentUser()
	}
}