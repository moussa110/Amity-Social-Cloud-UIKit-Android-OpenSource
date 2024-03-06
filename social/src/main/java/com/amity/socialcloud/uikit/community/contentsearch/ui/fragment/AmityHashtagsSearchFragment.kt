package com.amity.socialcloud.uikit.community.contentsearch.ui.fragment

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.amity.socialcloud.uikit.common.base.AmityBaseFragment
import com.amity.socialcloud.uikit.community.R
import com.amity.socialcloud.uikit.community.contentsearch.ui.activity.AmityHashTagPostsActivity.Companion.EXTRA_PARAM_HASHTAG
import com.amity.socialcloud.uikit.community.databinding.AmityFragmentHashtagSearchBinding
import com.amity.socialcloud.uikit.community.newsfeed.events.AmityFeedRefreshEvent
import com.amity.socialcloud.uikit.community.newsfeed.fragment.AmityGlobalFeedFragment
import com.amity.socialcloud.uikit.community.newsfeed.fragment.AmityPostCreatorFragment
import com.amity.socialcloud.uikit.community.newsfeed.fragment.AmitySearchQueryFeedFragment
import com.amity.socialcloud.uikit.community.utils.EXTRA_PARAM_COMMUNITY_ID
import com.amity.socialcloud.uikit.community.utils.EXTRA_PARAM_POST_ATTACHMENT_OPTIONS
import com.google.android.material.appbar.AppBarLayout
import io.reactivex.rxjava3.core.BackpressureStrategy
import io.reactivex.rxjava3.subjects.BehaviorSubject

class AmityHashtagsSearchFragment : AmityBaseFragment(), AppBarLayout.OnOffsetChangedListener {

	private lateinit var binding: AmityFragmentHashtagSearchBinding
	private var refreshEventPublisher = BehaviorSubject.create<AmityFeedRefreshEvent>()
	private lateinit var hashtagQuery:String
	companion object {
		fun newInstance(): Builder {
			return Builder()
		}
	}

	override fun onCreateView(inflater: LayoutInflater,
	                          container: ViewGroup?,
	                          savedInstanceState: Bundle?): View {
		binding = DataBindingUtil.inflate(inflater,
			R.layout.amity_fragment_hashtag_search,
			container,
			false)
		return binding.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		setupRefreshLayout()
		arguments?.getString(EXTRA_PARAM_HASHTAG)?.let {
			hashtagQuery = it
			binding.hashtagName.text = it
		}
		loadSearchedPost()
	}

	private fun setupRefreshLayout() {
		binding.refreshLayout.setColorSchemeResources(R.color.amityColorPrimary)
		binding.refreshLayout.setOnRefreshListener {
			refreshFeed()
			Handler(Looper.getMainLooper()).postDelayed({
				binding.refreshLayout.isRefreshing = false
			}, 1000)
		}
	}

	private fun loadSearchedPost() {
		val fragmentTransaction = childFragmentManager.beginTransaction()
		fragmentTransaction.replace(R.id.searchedPostsContainer, getSearchedPosts())
		fragmentTransaction.commit()
	}

	override fun onOffsetChanged(appBarLayout: AppBarLayout?, verticalOffset: Int) {
		binding.refreshLayout.isEnabled = (verticalOffset == 0)
	}

	override fun onResume() {
		super.onResume()
		binding.appBar.addOnOffsetChangedListener(this)
	}

	override fun onPause() {
		super.onPause()
		binding.appBar.removeOnOffsetChangedListener(this)
	}

	private fun refreshFeed() {
		childFragmentManager.fragments.forEach { fragment ->
			when (fragment) {
				is AmityGlobalFeedFragment -> {
					refreshEventPublisher.onNext(AmityFeedRefreshEvent())
				}
			}
		}
	}


	private fun getSearchedPosts(): Fragment {
		return AmitySearchQueryFeedFragment.Builder()
			.feedRefreshEvents(refreshEventPublisher.toFlowable(BackpressureStrategy.BUFFER))
			.build(activity as AppCompatActivity,hashtagQuery)
	}

	class Builder internal constructor() {

		fun build(hashtag: String): AmityHashtagsSearchFragment {
			AmityHashtagsSearchFragment().apply {
				arguments = Bundle().apply {
					putString(EXTRA_PARAM_HASHTAG, hashtag)
				}
				return this
			}
		}
	}
}