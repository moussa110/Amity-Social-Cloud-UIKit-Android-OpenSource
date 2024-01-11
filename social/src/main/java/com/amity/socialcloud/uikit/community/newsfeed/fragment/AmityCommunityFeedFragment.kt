package com.amity.socialcloud.uikit.community.newsfeed.fragment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.amity.socialcloud.sdk.model.core.user.AmityUser
import com.amity.socialcloud.sdk.model.social.community.AmityCommunity
import com.amity.socialcloud.sdk.model.social.feed.AmityFeedType
import com.amity.socialcloud.uikit.community.databinding.AmityViewMyTimelineFeedEmptyBinding
import com.amity.socialcloud.uikit.community.databinding.AmityViewOtherUserTimelineEmptyBinding
import com.amity.socialcloud.uikit.community.databinding.AmityViewPendingPostsEmptyBinding
import com.amity.socialcloud.uikit.community.databinding.AmityViewReviewFeedHeaderBinding
import com.amity.socialcloud.uikit.community.newsfeed.events.AmityFeedRefreshEvent
import com.amity.socialcloud.uikit.community.newsfeed.listener.AmityCommunityClickListener
import com.amity.socialcloud.uikit.community.newsfeed.listener.AmityUserClickListener
import com.amity.socialcloud.uikit.community.newsfeed.viewmodel.AmityCommunityFeedViewModel
import com.amity.socialcloud.uikit.feed.settings.AmityPostShareClickListener
import com.amity.socialcloud.uikit.social.AmitySocialUISettings
import com.ekoapp.rxlifecycle.extension.untilLifecycleEnd
import io.reactivex.rxjava3.core.Flowable

class AmityCommunityFeedFragment : AmityFeedFragment() {

    lateinit var mViewModel: AmityCommunityFeedViewModel
    override fun getViewModel(): AmityCommunityFeedViewModel {
        return mViewModel
    }

    override fun setupFeed() {
        getViewModel().observeCommunityStatus(
            onReadyToRender = {
                super.setupFeed()
            },
            onRefreshNeeded = {
                AmityGlobalFeedFragment.isNeedToRefreshing = true
                refresh()
            })
            .untilLifecycleEnd(this)
            .subscribe()
    }

    override fun getEmptyView(inflater: LayoutInflater): View {
        return if (getViewModel().feedType == AmityFeedType.REVIEWING) {
            getReviewFeedEmptyView(inflater)
        } else {
            if (getViewModel().latestReviewPermissionState == true) {
                getModeratorUserFeedEmptyView(inflater)
            } else {
                getOtherUserFeedEmptyView(inflater)
            }
        }
    }

    private fun getReviewFeedEmptyView(inflater: LayoutInflater): View {
        val binding = AmityViewPendingPostsEmptyBinding.inflate(
            inflater,
            requireView().parent as ViewGroup,
            false
        )
        return binding.root
    }

    private fun getOtherUserFeedEmptyView(inflater: LayoutInflater): View {
        val binding = AmityViewOtherUserTimelineEmptyBinding.inflate(
            inflater,
            requireView().parent as ViewGroup,
            false
        )
        return binding.root
    }

    private fun getModeratorUserFeedEmptyView(inflater: LayoutInflater): View {
        val binding = AmityViewMyTimelineFeedEmptyBinding.inflate(
            inflater,
            requireView().parent as ViewGroup,
            false
        )
        return binding.root
    }

    override fun getFeedHeaderView(inflater: LayoutInflater): List<View> {
        return if (getViewModel().feedType == AmityFeedType.REVIEWING) {
            val headerBinding = AmityViewReviewFeedHeaderBinding.inflate(
                inflater,
                requireView().parent as ViewGroup,
                false
            )
            listOf(headerBinding.root)
        } else {
            listOf()
        }
    }

    class Builder internal constructor() {
        private lateinit var communityId: String
        private var feedType: AmityFeedType = AmityFeedType.PUBLISHED
        private var userClickListener: AmityUserClickListener? = null
        private var communityClickListener: AmityCommunityClickListener? = null
        private var postShareClickListener: AmityPostShareClickListener =
            AmitySocialUISettings.postShareClickListener
        private var feedRefreshEvents = Flowable.never<AmityFeedRefreshEvent>()

        fun build(activity: AppCompatActivity): AmityCommunityFeedFragment {
            val fragment = AmityCommunityFeedFragment()
            fragment.mViewModel = ViewModelProvider(activity).get(AmityCommunityFeedViewModel::class.java)
            fragment.mViewModel.communityId = communityId
            if (userClickListener == null) {
                userClickListener = object : AmityUserClickListener {
                    override fun onClickUser(user: AmityUser) {
                        AmitySocialUISettings.globalUserClickListener.onClickUser(fragment, user)
                    }
                }
            }
            fragment.mViewModel.userClickListener = userClickListener!!

            if (communityClickListener == null) {
                communityClickListener = object : AmityCommunityClickListener {
                    override fun onClickCommunity(community: AmityCommunity) {
                        AmitySocialUISettings.globalCommunityClickListener.onClickCommunity(
                            fragment,
                            community
                        )
                    }
                }
            }
            fragment.mViewModel.communityClickListener = communityClickListener!!
            fragment.mViewModel.postShareClickListener = postShareClickListener
            fragment.mViewModel.feedRefreshEvents = feedRefreshEvents
            fragment.mViewModel.feedType = feedType
            return fragment
        }

        internal fun communityId(communityId: String): Builder {
            return apply { this.communityId = communityId }
        }

        internal fun community(community: AmityCommunity): Builder {
            return apply {
                this.communityId = community.getCommunityId()
            }
        }

        fun feedType(feedType: AmityFeedType): Builder {
            return apply {
                this.feedType = feedType
            }
        }

        fun userClickListener(userClickListener: AmityUserClickListener): Builder {
            return apply { this.userClickListener = userClickListener }
        }

        fun postShareClickListener(postShareClickListener: AmityPostShareClickListener): Builder {
            return apply { this.postShareClickListener = postShareClickListener }
        }

        fun feedRefreshEvents(feedRefreshEvents: Flowable<AmityFeedRefreshEvent>): Builder {
            return apply { this.feedRefreshEvents = feedRefreshEvents }
        }
    }

    companion object {

        fun newInstance(communityId: String): Builder {
            return Builder().communityId(communityId)
        }

        fun newInstance(community: AmityCommunity): Builder {
            return Builder().community(community)
        }

    }

}