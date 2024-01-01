package com.amity.socialcloud.uikit.community.newsfeed.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.paging.PagingData
import com.amity.socialcloud.sdk.api.core.AmityCoreClient
import com.amity.socialcloud.sdk.api.social.AmitySocialClient
import com.amity.socialcloud.sdk.api.social.community.query.AmityCommunitySortOption
import com.amity.socialcloud.sdk.model.core.user.AmityUser
import com.amity.socialcloud.sdk.model.social.community.AmityCommunity
import com.amity.socialcloud.sdk.model.social.community.AmityCommunityFilter
import com.amity.socialcloud.uikit.community.newsfeed.model.SharedPostData
import io.reactivex.rxjava3.core.Flowable

private const val SAVED_POST_CREATION_TYPE = "SAVED_POST_CREATION_TYPE"
private const val SAVED_SHARED_POST_DATA = "SAVED_SHARED_POST_DATA"

class AmityPostTargetViewModel(private val savedState: SavedStateHandle) : ViewModel() {

    var postCreationType: String = ""
        set(value) {
            savedState.set(SAVED_POST_CREATION_TYPE, value)
            field = value
        }

    var sharedPostData: SharedPostData = SharedPostData(postId = "")
        set(value) {
            savedState.set(SAVED_SHARED_POST_DATA, value)
            field = value
        }

    init {
        savedState.get<String>(SAVED_POST_CREATION_TYPE)?.let { postCreationType = it }
        savedState.get<SharedPostData>(SAVED_SHARED_POST_DATA)?.let { sharedPostData = it }
    }

    fun getUser(): AmityUser {
        return AmityCoreClient.getCurrentUser().blockingFirst()
    }


    fun getCommunityList(): Flowable<PagingData<AmityCommunity>> {
        val communityRepository = AmitySocialClient.newCommunityRepository()
        return communityRepository.getCommunities()
            .filter(AmityCommunityFilter.MEMBER)
            .sortBy(AmityCommunitySortOption.DISPLAY_NAME)
            .includeDeleted(false)
            .build()
            .query()
    }
}