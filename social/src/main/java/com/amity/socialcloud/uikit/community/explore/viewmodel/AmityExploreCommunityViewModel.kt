package com.amity.socialcloud.uikit.community.explore.viewmodel

import androidx.databinding.ObservableBoolean
import androidx.paging.PagingData
import androidx.paging.filter
import com.amity.socialcloud.sdk.api.social.AmitySocialClient
import com.amity.socialcloud.sdk.api.social.category.query.AmityCommunityCategorySortOption
import com.amity.socialcloud.sdk.model.social.category.AmityCommunityCategory
import com.amity.socialcloud.sdk.model.social.community.AmityCommunity
import com.amity.socialcloud.uikit.common.base.AmityBaseViewModel
import com.amity.socialcloud.uikit.common.utils.AmityConstants
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.schedulers.Schedulers

class AmityExploreCommunityViewModel : AmityBaseViewModel() {

    val emptyRecommendedList = ObservableBoolean(false)
    val emptyTrendingList = ObservableBoolean(false)
    val emptyCategoryList = ObservableBoolean(false)


    fun getRecommendedCommunity(onRecommendedCommunitiesLoaded: (List<AmityCommunity>) -> Unit): Completable {
        return AmitySocialClient.newCommunityRepository()
            .getRecommendedCommunities()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext { onRecommendedCommunitiesLoaded.invoke(it) }
            .doOnError { }
            .ignoreElements()
    }

    fun getTrendingCommunity(onTrendingCommunitiesLoaded: (List<AmityCommunity>) -> Unit): Completable {
        return AmitySocialClient.newCommunityRepository()
            .getTrendingCommunities()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext { onTrendingCommunitiesLoaded.invoke(it) }
            .doOnError { }
            .ignoreElements()
    }
    private val adminCategories = listOf(AmityConstants.APP_FANTASY_CATEGORY_ID,
        AmityConstants.APP_PREDICTION_CATEGORY_ID)

    fun getCommunityCategory(onCommunityCategoriesLoaded: (PagingData<AmityCommunityCategory>) -> Unit): Completable {
        val communityRepository = AmitySocialClient.newCommunityRepository()
        return communityRepository.getCategories()
            .sortBy(AmityCommunityCategorySortOption.NAME)
            .includeDeleted(false)
            .build()
            .query()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .map { pagingData ->
                // Apply your filtering logic here
                // For example, let's filter by a certain condition
                pagingData.filter { category ->
                    !adminCategories.contains(category.getCategoryId())
                }
            }
            .doOnNext { onCommunityCategoriesLoaded.invoke(it) }
            .doOnError { }
            .ignoreElements()
    }
}