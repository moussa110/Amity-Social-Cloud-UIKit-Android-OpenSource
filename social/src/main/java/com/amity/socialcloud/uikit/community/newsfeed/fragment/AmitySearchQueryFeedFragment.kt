package com.amity.socialcloud.uikit.community.newsfeed.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.amity.socialcloud.sdk.model.core.user.AmityUser
import com.amity.socialcloud.sdk.model.social.community.AmityCommunity
import com.amity.socialcloud.uikit.common.model.AmityEventIdentifier
import com.amity.socialcloud.uikit.common.utils.SharedPrefsUtils
import com.amity.socialcloud.uikit.community.databinding.AmityViewGlobalFeedEmptyBinding
import com.amity.socialcloud.uikit.community.home.fragments.AmityCommunityHomeViewModel
import com.amity.socialcloud.uikit.community.newsfeed.events.AmityFeedRefreshEvent
import com.amity.socialcloud.uikit.community.newsfeed.listener.AmityCommunityClickListener
import com.amity.socialcloud.uikit.community.newsfeed.listener.AmityUserClickListener
import com.amity.socialcloud.uikit.community.newsfeed.viewmodel.AmityGlobalFeedViewModel
import com.amity.socialcloud.uikit.community.newsfeed.viewmodel.AmitySearchQueryFeedViewModel
import com.amity.socialcloud.uikit.community.ui.view.AmityCommunityCreatorActivity
import com.amity.socialcloud.uikit.feed.settings.AmityPostShareClickListener
import com.amity.socialcloud.uikit.social.AmitySocialUISettings
import io.reactivex.rxjava3.core.Flowable

class AmitySearchQueryFeedFragment : AmityFeedFragment() {

    lateinit var mViewModel:AmitySearchQueryFeedViewModel

    override fun getViewModel(): AmitySearchQueryFeedViewModel {
        return mViewModel
    }

    override fun onResume() {
        super.onResume()
        if (isNeedToRefreshing) {
            isNeedToRefreshing = false
            refresh()
        }
    }

    override fun getEmptyView(inflater: LayoutInflater): View {
        val binding = AmityViewGlobalFeedEmptyBinding.inflate(
            inflater,
            requireView().parent as ViewGroup,
            false
        )

        binding.btnExplore.setOnClickListener {
            communityHomeViewModel.triggerEvent(AmityEventIdentifier.EXPLORE_COMMUNITY)
        }

        binding.tvCreateCommunity.setOnClickListener {
            val intent = Intent(requireContext(), AmityCommunityCreatorActivity::class.java)
            startActivity(intent)
        }
        return binding.root
    }


    class Builder internal constructor() {
        private var userClickListener: AmityUserClickListener? = null
        private var communityClickListener: AmityCommunityClickListener? = null
        private var postShareClickListener: AmityPostShareClickListener = AmitySocialUISettings.postShareClickListener
        private var feedRefreshEvents = Flowable.never<AmityFeedRefreshEvent>()

        fun build(activity: AppCompatActivity,query:String): AmitySearchQueryFeedFragment {
            val fragment = AmitySearchQueryFeedFragment()
            fragment.mViewModel = ViewModelProvider(activity)[AmitySearchQueryFeedViewModel::class.java]
            fragment.mViewModel.query = query

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
            return fragment
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
         var isNeedToRefreshing = false
        fun newInstance(): Builder {
            return Builder()
        }
    }

}